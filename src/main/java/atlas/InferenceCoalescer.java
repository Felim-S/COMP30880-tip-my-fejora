package atlas;

import java.util.*;

public class InferenceCoalescer {

    public static List<List<Structure>> coalesce(
            List<Structure> candidateInferences,
            Map<String, String> compositeMapping) {

        List<Map<String, String>> inferenceMappings = new ArrayList<>();
        for (Structure inference : candidateInferences) {
            inferenceMappings.add(extractMapping(inference, compositeMapping));
        }

        List<List<Structure>> coalescences = new ArrayList<>();
        List<Map<String, String>> coalescenceMappings = new ArrayList<>();

        for (int i = 0; i < candidateInferences.size(); i++) {
            Structure inference = candidateInferences.get(i);
            Map<String, String> inferenceMapping = inferenceMappings.get(i);
            boolean added = false;

            for (int j = 0; j < coalescences.size(); j++) {
                Map<String, String> groupMapping = coalescenceMappings.get(j);

                if (isConsistent(groupMapping, inferenceMapping)) {
                    coalescences.get(j).add(inference);
                    //merge inferences mapping into the group mapping
                    groupMapping.putAll(inferenceMapping);
                    added = true;
                    break;
                }
            }

            //if it didn't fit anywhere, start a new coalescence group
            if (!added) {
                List<Structure> newGroup = new ArrayList<>();
                newGroup.add(inference);
                coalescences.add(newGroup);

                Map<String, String> newGroupMapping = new HashMap<>(inferenceMapping);
                coalescenceMappings.add(newGroupMapping);
            }
        }

        return coalescences;
    }

    private static Map<String, String> extractMapping(
            Structure inference, Map<String, String> compositeMapping) {

        Map<String, String> subMapping = new HashMap<>();
        Set<String> symbols = extractSymbols(inference);

        //build reverse lookup
        Map<String, String> reverseMapping = new HashMap<>();
        for (Map.Entry<String, String> entry : compositeMapping.entrySet()) {
            reverseMapping.put(entry.getValue(), entry.getKey());
        }

        for (String symbol : symbols) {
            String source = reverseMapping.get(symbol);
            if (source != null && !source.equals(symbol)) {
                subMapping.put(source, symbol);
            }
        }

        return subMapping;
    }

    private static Set<String> extractSymbols(Structure structure) {
        Set<String> symbols = new HashSet<>();
        for (Element element : structure.getElements()) {
            if (element instanceof Structure) {
                symbols.addAll(extractSymbols((Structure) element));
            } else if (element instanceof Symbol && !(element instanceof Predicate)) {
                symbols.add(element.toString());
            }
        }
        return symbols;
    }

    private static boolean isConsistent(
            Map<String, String> groupMapping, Map<String, String> inferenceMapping) {

        for (Map.Entry<String, String> entry : inferenceMapping.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (groupMapping.containsKey(key) && !groupMapping.get(key).equals(value)) {
                return false;
            }

            if (groupMapping.containsValue(value) && !groupMapping.containsKey(key)) {
                return false;
            }
        }

        return true;
    }
}