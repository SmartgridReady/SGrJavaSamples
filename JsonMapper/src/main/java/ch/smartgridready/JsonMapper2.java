package ch.smartgridready;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class JsonMapper2 {

    private static final JmesPath<JsonNode> jmespath = new JacksonRuntime();

    private final Map<String, String> keywordMapTariffOut;

    public JsonMapper2(Map<String, String> keywordMapTariffOut) {
        this.keywordMapTariffOut = keywordMapTariffOut;
    }

    public static class Key implements Comparable<Key>, Serializable {

        private List<Integer> indices = new ArrayList<>();

        public void add(int index) {
            indices.add(index);
        }

        public String getKey() {
            return indices.stream().map(String::valueOf).collect(Collectors.joining());
        }

        public int indexAt(int iteration) {
            return indices.get(iteration);
        }

        @Override
        public int compareTo(Key key) {
            return getKey().compareTo(key.getKey());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Key && compareTo((Key) obj) == 0;
        }

        public static Key copy(Key srcKey) {
            return SerializationUtils.clone(srcKey);
        }
    }

    public static Map<Key, Map<String, String>> mapToFlatList(String jsonFile, Map<String, String> keywordMapTariffOut)
            throws JsonProcessingException {

        JsonMapper2 mapper = new JsonMapper2(keywordMapTariffOut);

        ObjectMapper parser = new ObjectMapper();
        JsonNode root = parser.readTree(jsonFile);

        return mapper.parseJsonTree(root, null, 1);
    }

    private Map<Key, Map<String, String>> parseJsonTree(JsonNode node, Map<Key, Map<String, String>> parentData, int iteration) {
        Map<Key, Map<String, String>> tariffRecordMap = new TreeMap<>();

        Set<Map.Entry<String, String>> keywords = getKeywordsForIteration(iteration);

        if (!keywords.isEmpty()) {
            processChildElements(node, iteration, tariffRecordMap, keywords, parentData, null);
            return parseJsonTree(node, tariffRecordMap, ++iteration);
        } else {
            return parentData;
        }
    }

    private Set<Map.Entry<String, String>> getKeywordsForIteration(int iteration) {
        return keywordMapTariffOut.entrySet().stream()
                .filter(entry -> StringUtils.countMatches(entry.getValue(), "[*]") == iteration)
                .collect(Collectors.toSet());
    }

    private void processChildElements(JsonNode node, int iteration, Map<Key, Map<String, String>> tariffRecordMap,
                                      Set<Map.Entry<String, String>> keywords, Map<Key, Map<String, String>> parentData,
                                      Map.Entry<Key, Map<String, String>> parentRec) {

        Optional<Map.Entry<String, String>> kwOpt = keywords.stream().findFirst();
        kwOpt.ifPresent(keyword -> {
            int noOfElem = getNoOfElem(node, parentRec, keyword, iteration);

            for (int i = 0; i < noOfElem; i++) {
                Key key = parentRec == null ? new Key() : Key.copy(parentRec.getKey());
                key.add(i);

                Map<String, String> record = parentRec == null ? new HashMap<>() : new HashMap<>(parentRec.getValue());
                tariffRecordMap.put(key, record);

                keywords.forEach(kw -> addChildElement(node, tariffRecordMap, key, kw, iteration));
            }
        });
    }

    private void addChildElement(JsonNode node, Map<Key, Map<String, String>> tariffRecordMap, Key key,
                                 Map.Entry<String, String> kw, int iteration) {
        String pattern = buildPattern(kw.getValue(), key, iteration);
        Expression<JsonNode> expression = jmespath.compile(pattern);
        JsonNode value = expression.search(node);
        tariffRecordMap.get(key).put(kw.getKey(), value.toString());
    }

    private String buildPattern(String pattern, Key key, int iteration) {
        StringBuilder patternBuilder = new StringBuilder(pattern);
        for (int i = 0; i < iteration; i++) {
            patternBuilder.replace(patternBuilder.indexOf("[*]"), patternBuilder.indexOf("[*]") + 3, "[" + key.indexAt(i) + "]");
        }
        return patternBuilder.toString();
    }

    private int getNoOfElem(JsonNode node, Map.Entry<Key, Map<String, String>> parentRec,
                            Map.Entry<String, String> keyword, int iteration) {
        String searchPattern = buildSearchPattern(keyword.getValue(), parentRec, iteration);
        Expression<JsonNode> jmesQuery = jmespath.compile(searchPattern + " | length(@)");
        JsonNode result = jmesQuery.search(node);
        return result.asInt();
    }

    private String buildSearchPattern(String pattern, Map.Entry<Key, Map<String, String>> parentRec, int iteration) {
        for (int i = 1; i < iteration; i++) {
            pattern = pattern.replaceFirst("\\[\\*\\]", "[" + parentRec.getKey().indexAt(i) + "]");
        }
        return pattern;
    }
}
