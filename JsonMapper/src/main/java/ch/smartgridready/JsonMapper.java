package ch.smartgridready;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Maps a received Json value into the required SmartGridready Json output value.
// Must group by received timestamps and list all tariffs for a given period.
// The timestamps are converted to a canonical format first and afterwards
public class JsonMapper
{
    private static final JmesPath<JsonNode> jmespath = new JacksonRuntime();

    private final Map<String, String> keywordMapTariffOut;

    public JsonMapper(Map<String, String> keywordMapTariffOut) {
        this.keywordMapTariffOut = keywordMapTariffOut;
    }

    public static Map<Integer, Map<String, String>> mapToFlatJson(String jsonFile, Map<String, String> keywordMapTariffOut) throws Exception {

        JsonMapper mapper = new JsonMapper(keywordMapTariffOut);

        ObjectMapper parser = new ObjectMapper();
        JsonNode root = parser.readTree(jsonFile);

        return mapper.parseJsonTree(root, null, 1);
    }

    private Map<Integer, Map<String, String>> parseJsonTree(JsonNode node, Map<Integer, Map<String, String>> parentData, int iteration) {

        Map<Integer, Map<String, String>> tariffRecordMap = new TreeMap<>(); // TreeMap to keep the order of element occurrence

        // Get all keywords for the given iteration depth
        final int iterationDepth = iteration;
        Set<Map.Entry<String, String>> keywords = keywordMapTariffOut.entrySet().stream()
                .filter(entry -> StringUtils.countMatches(entry.getValue(), "[*]")==iterationDepth)
                .collect(Collectors.toSet());

        if (keywords.size() > 0) {

            if (iteration == 1) {
                processChildElements(node, iteration, tariffRecordMap, keywords, 0, null);
            } else {
                int parentIndex = 0;
                for (Map.Entry<Integer, Map<String, String>> parentRec : parentData.entrySet()) {
                    processChildElements(node, iteration, tariffRecordMap, keywords, parentIndex, parentRec);
                    parentIndex++;
                }
            }
            return parseJsonTree(node, tariffRecordMap, ++iteration);
        } else {
            return parentData;
        }
    }

    /**
     *
     * @param node              The root node to operate on
     * @param iteration         The iteration step. This is the depth in the Json tree hierarchy, starting with 1.
     * @param keywords          The keywords define the name of the child element to be added and the path where to read the value from the Json
     * @param parentIndex       The index of the parent element. Used to build up the Jmes-path with the correct index of the parent element.
     * @param parentRec         The parent record to retrieve the parent element data.
     */
    private static void processChildElements(JsonNode node,
                                             int iteration,
                                             Map<Integer, Map<String, String>> tariffRecordMap,
                                             Set<Map.Entry<String, String>> keywords,
                                             int parentIndex,
                                             Map.Entry<Integer, Map<String, String>> parentRec) {

        // Count the number of child records for the given parent record
        Optional<Map.Entry<String, String>> kwOpt = keywords.stream().findFirst();
        if (kwOpt.isPresent()) {
            int noOfElem = getNoOfElem(node, parentIndex, kwOpt.get(), iteration);

            // loop over the child records
            for (int i = 0; i < noOfElem; i++) {
                final int combinedIndex = parentRec != null ? (((parentRec.getKey() + 1) * 10)) + i : i;
                if (parentRec == null) {
                    // process the root node.
                    tariffRecordMap.put(combinedIndex, new HashMap<>());
                } else {
                    // process the child nodes, mix-in the values of the parent node.
                    tariffRecordMap.put(combinedIndex, new HashMap<>(parentRec.getValue()));
                }

                final int subIndex = i;
                final int iter = iteration;
                keywords.forEach(kw -> {
                    addChildElement(node, tariffRecordMap, parentRec, combinedIndex, subIndex, kw, iter);
                });
            }
        }
    }

    private static void addChildElement(
            JsonNode node, Map<Integer,
            Map<String, String>> tariffRecordMap,
            Map.Entry<Integer, Map<String, String>> parentRec,
            int combinedIndex, int subIndex,
            Map.Entry<String, String> kw,
            int iteration) {

        String pattern = kw.getValue();

        if (iteration == 1) {
            pattern = pattern.replaceFirst("\\[\\*\\]", String.format("[%d]", subIndex));
        }
        if (iteration == 2) {
            pattern = pattern.replaceFirst("\\[\\*\\]", String.format("[%d]", parentRec.getKey()));
            pattern = pattern.replaceFirst("\\[\\*\\]", String.format("[%d]", subIndex));
        }
        Expression<JsonNode> expression = jmespath.compile(pattern);
        JsonNode value = expression.search(node);
        tariffRecordMap.get(combinedIndex).put(kw.getKey(), value.toString());
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
        int noOfElem = result.asInt();
        return noOfElem;
    }
}
