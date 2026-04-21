package atlas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        for (Structure[] pair : alignable) {
            try{
                Map<String, String> partial = StructureMapper.generateMapping(pair[0], pair[1]);
                if(isConsistent(composite, partial)){
                    // merge the two since they are consistent
                    composite.putAll(partial);
                }
            } catch (Exception _){
                // ignore those for which 1-1 mapping is not possible
            }
        }

        return composite;
    }

    // Determines whether a consistent 1-1 mapping is possible
    // between two composite mappings (A, B)
    private static boolean isConsistent(Map<String, String> composite, Map<String, String> partial){
        for(Map.Entry<String, String> entry : partial.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();

            // key must not appear as another value's source key
            if(composite.containsKey(key) && !composite.get(key).equals(value)){
                return false;
            }

            // value must not appear as another key's value
            if(composite.containsValue(value) && !composite.containsKey(key)){
                return false;
            }

            // key must not appear as a target value
            if (composite.containsValue(key) && !composite.containsKey(key)) {
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
