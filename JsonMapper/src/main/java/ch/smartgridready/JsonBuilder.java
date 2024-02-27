package ch.smartgridready;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> keywordMap;

    public JsonBuilder(Map<String, String> keywordMap) {
        this.keywordMap = keywordMap;
    }

    public String buildJson(Collection<Map<String, Object>> flatDataRecords) throws JsonProcessingException {

        JsonBuilder builder = new JsonBuilder(keywordMap);

        // Group by first level group.
        List<Map.Entry<String, String>> keywordsForIteration = builder.getKeywordsForIteration(1);

        // Put all records that have the same first level values into one group with a combined key, built from the values.
        Map<String, List<Map<String, Object>>> firstLevelGroups = new LinkedHashMap<>();
        flatDataRecords.forEach(flatRecord -> {

            // Build a combined key
            StringBuilder combinedKey = new StringBuilder();
            keywordsForIteration.forEach(keywordEntry -> {
                Object value = flatRecord.get(keywordEntry.getKey());
                combinedKey.append(value);
            });

            // Get the group by the combined key or add a new group if it does not exist.
            List<Map<String, Object>> recordGroup =
                    Optional.ofNullable(firstLevelGroups.get(combinedKey.toString())).orElse(new ArrayList<>());

            // Add the flat record to the group it belongs to
            recordGroup.add(flatRecord);
            // Update the first level group map
            firstLevelGroups.put(combinedKey.toString(), recordGroup);
        });


        // Get a mapping for the source element names to the destination element names
        Map<String, String> firstLevelNameMappings = getFirstLevelElements(keywordsForIteration);

        // Build up the JsonNode
        ArrayNode rootNode = objectMapper.createArrayNode(); // Assuming below the root node is an array
        firstLevelGroups.forEach((groupKey, flatRecordsBeloningToGroup) -> {
            // Add a node for each group to the array node
            ObjectNode firstLevelNode = objectMapper.createObjectNode();
            flatRecordsBeloningToGroup.forEach(flatRecord -> {
                flatRecord.forEach((valueNames, values) ->
                        // Pick all first level elements, map the elementNames get the values and add the elements to the  firstLevel node.
                        keywordsForIteration.forEach(mappingEntry -> {
                                    Object val = flatRecord.get(mappingEntry.getKey());
                                    if (val instanceof Number) {
                                        firstLevelNode.put(firstLevelNameMappings.get(mappingEntry.getKey()), (Double) val);
                                    } else {
                                        firstLevelNode.put(firstLevelNameMappings.get(mappingEntry.getKey()), val.toString());
                                    }
                                }));
                // Then add the second level nodes to the first level node.
                addSecondLevelNodes(firstLevelNode, flatRecordsBeloningToGroup);
            });
            // We have finished adding the first level node.
            rootNode.add(firstLevelNode);
        });

        return objectMapper.writeValueAsString(rootNode);
    }


    private void addSecondLevelNodes(ObjectNode firstLevelNode, List<Map<String, Object>> flatRecordsBelongingToGroup) {


        List<Map.Entry<String, String>> keywordsForIteration = getKeywordsForIteration(2);

        Map<String, List<Map<String, Object>>> secondLevelGroups = new LinkedHashMap<>();

        flatRecordsBelongingToGroup.forEach(flatRecord -> {
                    // Build a combined key
                    StringBuilder combinedKey = new StringBuilder();
                    keywordsForIteration.forEach(keywordEntry -> {
                        Object value = flatRecord.get(keywordEntry.getKey());
                        combinedKey.append(value);
                    });

                    // Get the group by the combined key or add a new group if it does not exist.
                    List<Map<String, Object>> recordGroup =
                            Optional.ofNullable(secondLevelGroups.get(combinedKey.toString())).orElse(new ArrayList<>());

                    recordGroup.add(flatRecord);
                    secondLevelGroups.put(combinedKey.toString(), recordGroup);
                });

        // Build the Json tree
        Map<String, Map<String, String>> secondLeveGroupElements = getSecondLevelElements(keywordsForIteration);
        secondLeveGroupElements.forEach((parentName, childNameMapping) -> {

            ArrayNode arrayNode = firstLevelNode.putArray(parentName);
            secondLevelGroups.forEach( (groupKey, flatRecordsOfGroup) -> {
                ObjectNode objectNode = objectMapper.createObjectNode();
                flatRecordsOfGroup.forEach(flatRecord -> flatRecord.forEach((valueNames, values) -> keywordsForIteration.forEach(mappingEntry -> {
                    Object val = flatRecord.get(mappingEntry.getKey());
                    if (val instanceof Number) {
                        objectNode.put(childNameMapping.get(mappingEntry.getKey()), (Double) val);
                    } else if (val !=null) {
                        objectNode.put(childNameMapping.get(mappingEntry.getKey()), val.toString());
                    }
                })));
                arrayNode.add(objectNode);
            });
        });
    }

    private Map<String, String> getFirstLevelElements(List<Map.Entry<String, String>> keywords) {

        Map<String, String> result = new LinkedHashMap<>();
        keywords.forEach(entry -> {
            Pattern pattern = Pattern.compile("\\[\\*\\]\\.(.*?)$");
            Matcher matcher = pattern.matcher(entry.getValue());
            if (matcher.find()) {
                String targetName = matcher.group(1);
                result.put(entry.getKey(), targetName);
            }
        });
        return result;
    }


    private Map<String, Map<String, String>> getSecondLevelElements(List<Map.Entry<String, String>> keywords) {

        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        keywords.forEach(entry -> {
            Pattern pattern = Pattern.compile("\\[\\*\\]\\.(.*?)\\[\\*\\]");
            Matcher matcher = pattern.matcher(entry.getValue());
            if (matcher.find()) {
                String groupKey = matcher.group(1);
                Map<String, String> valueNames = Optional.ofNullable(result.get(groupKey)).orElse(new LinkedHashMap<>());
                String valueName = entry.getValue().replaceAll("\\[\\*\\]\\.(.*?)\\[\\*\\]\\.", "");
                valueNames.put(entry.getKey(), valueName);
                result.put(groupKey, valueNames);
            }
        });
        return result;
    }

    private List<Map.Entry<String, String>> getKeywordsForIteration(int iteration) {
        final int iterationDepth = iteration;
        return keywordMap.entrySet().stream()
                .filter(entry -> StringUtils.countMatches(entry.getValue(), "[*]")==iterationDepth)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
