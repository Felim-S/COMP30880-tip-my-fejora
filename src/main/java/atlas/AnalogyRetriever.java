package atlas;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalogyRetriever {

    private KnowledgeBase kb;

    public AnalogyRetriever(KnowledgeBase kb) {
        this.kb = kb;
    }

    public Set<String> getCandidateSources(String target) {
        Set<String> candidates = new HashSet<>();

        //get all structures about target
        List<Structure> targetStructures = kb.getStructuresForTopic(target);

        //for each structure about target, generate its abstract hash
        for (Structure targetStructure : targetStructures) {
            String hash = StructureAbstractor.generateAbstraction(targetStructure).toString().intern();

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
        for(Structure sourceStructure : sourceStructures) {
            String sourceHash = StructureAbstractor.generateAbstraction(sourceStructure).toString().intern();

            for(Structure targetStructure : targetStructures) {
                String targetHash = StructureAbstractor.generateAbstraction(targetStructure).toString().intern();

                //if they share the same abstract shape then they are alignable
                if(sourceHash.equals(targetHash)) {
                    alignable.add(new Structure[]{sourceStructure, targetStructure});
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
