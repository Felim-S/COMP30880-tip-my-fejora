package atlas;

import java.util.*;

public class CompositeMapper {

    // Takes a source topic S and target topic T and produces
    // a composite mapping of their structures
    public static HashMap<String, String> generateCompositeMapping(
            String S, String T, KnowledgeBase kb) {
        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        List<Structure[]> alignable = retriever.getAlignableStructures(S, T);

        alignable.sort( (a, b) ->
                Double.compare(
                        AnalogyRanker.richness(b[1]),
                        AnalogyRanker.richness(a[1])
                )
        );

        HashMap<String, String> composite = new HashMap<>();
        HashSet<String> compositeValues = new HashSet<>();

        for (Structure[] pair : alignable) {
            try{
                Map<String, String> partial = StructureMapper.generateMapping(pair[0], pair[1]);
                if(isConsistent(composite, partial, compositeValues)){
                    // merge the two since they are consistent
                    composite.putAll(partial);
                    compositeValues.addAll(partial.values());
                }
            } catch (Exception _){
                // ignore those for which 1-1 mapping is not possible
            }
        }

        return composite;
    }

    // Determines whether a consistent 1-1 mapping is possible
    // between two composite mappings (A, B)
    private static boolean isConsistent(Map<String, String> composite, Map<String, String> partial, Set<String> compositeValues){
        for(Map.Entry<String, String> entry : partial.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();

            // key must not appear as another value's source key
            if(composite.containsKey(key) && !composite.get(key).equals(value)){
                return false;
            }

            // value must not appear as another key's value
            if(compositeValues.contains(value) && !composite.containsKey(key)){
                return false;
            }

            // key must not appear as a target value
            if(compositeValues.contains(key)   && !composite.containsKey(key)){
                return false;
            }

            // value must not appear as a source key
            if (composite.containsKey(value) && !composite.get(value).equals(value)) {
                return false;
            }

        }

        return true;
    }
}
