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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Maps a received Json value into the required SmartGridready Json output value.
// The timestamps are converted to a canonical format first and afterwards
public class JsonMapper
{
    private static final JmesPath<JsonNode> jmespath = new JacksonRuntime();

    private final Map<String, String> keywordMapInput;

    public JsonMapper(Map<String, String> keywordMapInput) {
        this.keywordMapInput = keywordMapInput;
    }

    public static class Key implements Comparable<Key>, Serializable {

        private final List<Integer> indices = new ArrayList<>();

        public void add(int index) {
            indices.add(index);
        }

        public String key() {
            StringBuilder sb = new StringBuilder();
            indices.forEach(sb::append);
            return sb.toString();
        }

        public int indexAt(int iteration) {
            return indices.get(iteration);
        }

        @Override
        public int compareTo(Key o) {
            return key().compareTo(o.key());
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key && this.compareTo((Key)o) == 0;
        }

        @Override
        public int hashCode() {
            return key().hashCode();
        }

        public static Key copy(Key srcKey) {
            return SerializationUtils.clone(srcKey);
        }
    }

    public static Map<Key, Map<String, Object>> mapToFlatList(String jsonFile, Map<String, String> keywordMapInput)
            throws JsonProcessingException {

        JsonMapper mapper = new JsonMapper(keywordMapInput);

        ObjectMapper parser = new ObjectMapper();
        JsonNode root = parser.readTree(jsonFile);

        return mapper.parseJsonTree(root, null, 1);
    }

    private Map<Key, Map<String, Object>> parseJsonTree(JsonNode node, Map<Key, Map<String, Object>> parentData, int iteration) {

        Map<Key, Map<String, Object>> recordMap = new TreeMap<>(); // TreeMap to keep the order of element occurrence

        // Get all keywords for the given iteration depth
        Set<Map.Entry<String, String>> keywords = getKeywordsForIteration(iteration);

        if (iteration <= determineRequiredIterations(keywordMapInput)) {
            if (parentData == null) {
                processChildElements(node, iteration, recordMap, keywords, 0, null);
            } else {
                int parentIndex = 0;
                for (Map.Entry<Key, Map<String, Object>> parentRec : parentData.entrySet()) {
                    processChildElements(node, iteration, recordMap, keywords, parentIndex, parentRec);
                    parentIndex++;
                }
            }
            return parseJsonTree(node, recordMap, ++iteration);
        } else {
            return parentData;
        }
    }

    private Set<Map.Entry<String, String>> getKeywordsForIteration(int iteration) {
        final int iterationDepth = iteration;
        return keywordMapInput.entrySet().stream()
                .filter(entry -> StringUtils.countMatches(entry.getValue(), "[*]")==iterationDepth)
                .collect(Collectors.toSet());
    }

    private static void processChildElements(JsonNode node,
                                             int iteration,
                                             Map<Key, Map<String, Object>> recordMap,
                                             Set<Map.Entry<String, String>> keywords,
                                             int parentIndex,
                                             Map.Entry<Key, Map<String, Object>> parentRec) {

        // Count the number of child records for the given parent record
        Optional<Map.Entry<String, String>> kwOpt = keywords.stream().findFirst();
        if (kwOpt.isPresent()) {
            int noOfElem = getNoOfElem(node, parentIndex, kwOpt.get(), iteration);

            // loop over the child records
            for (int i = 0; i < noOfElem; i++) {
                Key key = parentRec == null ? new Key() : Key.copy(parentRec.getKey());
                key.add(i);
                if (parentRec == null) {
                    // process the root node.
                    recordMap.put(key, new HashMap<>());
                } else {
                    // process the child nodes, mix-in the values of the parent node.
                    recordMap.put(key, new HashMap<>(parentRec.getValue()));
                }

                final int iter = iteration;
                keywords.forEach(kw -> addChildElement(node, recordMap, key, kw, iter));
            }
        }
    }

    private static void addChildElement(
            JsonNode node,
            Map<Key, Map<String, Object>> recordMap,
            Key key,
            Map.Entry<String, String> kw,
            int iteration) {

        String pattern = kw.getValue();

        for (int i=0; i<iteration; i++) {
            pattern = pattern.replaceFirst("\\[\\*\\]", String.format("[%d]", key.indexAt(i)));
        }
        Expression<JsonNode> expression = jmespath.compile(pattern);
        JsonNode value = expression.search(node);

        if (value.isTextual()) {
            recordMap.get(key).put(kw.getKey(), value.asText());
        } if (value.isFloatingPointNumber()) {
            recordMap.get(key).put(kw.getKey(), value.asDouble());
        }
    }

    private static int getNoOfElem(JsonNode node,
                                   int parentIndex,
                                   Map.Entry<String, String> keyword,
                                   int iteration) {

        // Get count the number of records
        String searchPattern = keyword.getValue();
        for (int i=1; i < iteration; i++) {
            searchPattern = keyword.getValue().replaceFirst("\\[\\*\\]", String.format("[%d]", parentIndex));
        }
        Expression<JsonNode> jmesQuery = jmespath.compile(searchPattern + " | length(@)");
        JsonNode result = jmesQuery.search(node);
        return result.asInt();
    }

    private static int determineRequiredIterations(Map<String, String> keywordMap) {
        return keywordMap.values().stream()
                .map(value -> StringUtils.countMatches(value, "[*]"))
                .max(Comparator.naturalOrder()).orElse(0);
    }
}
