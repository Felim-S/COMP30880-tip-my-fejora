package atlas;

import java.util.*;

public class AnalogyRetriever {

    private final KnowledgeBase kb;

    public AnalogyRetriever(KnowledgeBase kb) {
        this.kb = kb;
    }

    public Set<String> getCandidateSources(String target) {
        Set<String> candidates = new HashSet<>();

        //get all structures about target
        List<Structure> targetStructures = kb.getStructuresForTopic(target);

        //for each structure about target, generate its abstract hash
        for (Structure targetStructure : targetStructures) {
            String hash = StructureAbstractor.getAbstractionHash(targetStructure).intern();

            //
            List<Structure> analogousStructures = kb.getStructuresByHash(hash);

            for(Structure analogous : analogousStructures) {
                String topic = findTopic(analogous);

                if(topic != null && !topic.equals(target)) {
                    candidates.add(topic);
                }
            }
        }
        return  candidates;
    }

    public List<Structure[]> getAlignableStructures(String source, String target) {
        List<Structure[]> alignable = new java.util.ArrayList<>();

        List<Structure> sourceStructures = kb.getStructuresForTopic(source);
        List<Structure> targetStructures = kb.getStructuresForTopic(target);

        //for each structure find target structures with same hash
        Map<String, List<Structure>> sourceByHash = new HashMap<>();
        for(Structure sourceStructure : sourceStructures) {
            String sourceHash = StructureAbstractor.getAbstractionHash(sourceStructure).intern();
            sourceByHash.computeIfAbsent(sourceHash, k -> new ArrayList<>()).add(sourceStructure);
        }

        for(Structure targetStructure : targetStructures) {
            String targetHash = StructureAbstractor.getAbstractionHash(targetStructure).intern();
            //if they share the same abstract shape then they are alignable
            List<Structure> matches = sourceByHash.get(targetHash);
            if (matches != null) {
                for (Structure s : matches) {
                    alignable.add(new Structure[]{s, targetStructure});
                }
            }
        }

        return alignable;
    }



    //helper method
    private String findTopic(Structure structure) {
        return findTopicRecursive(structure);
    }

    private String findTopicRecursive(Structure structure) {
        for (Element element : structure.getElements()) {
            if(element instanceof Symbol symbol){
                if(symbol.getValue().startsWith("*")){
                    return symbol.getValue().substring(1);
                }
            } else if(element instanceof Structure nested){
                String topic = findTopicRecursive(nested);
                if(topic != null) return topic;
            }
        }

        return null;
    }


}
