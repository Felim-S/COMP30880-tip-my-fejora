package atlas;

import java.util.*;
import java.util.stream.Collectors;


public class CompositeRanker {

    private final KnowledgeBase kb;
    private final AnalogyRetriever retriever;

    public CompositeRanker(KnowledgeBase kb) {
        this.kb = kb;
        this.retriever = new AnalogyRetriever(kb);
    }

    public static int mappingRichness(HashMap<String, String> compositeMapping) {
        int count = 0;
        for (Map.Entry<String, String> entry : compositeMapping.entrySet()) {
            if (!entry.getKey().equals(entry.getValue())) {
                count++;
            }
        }
        return count;
    }



        public List<String> rankSources(String target){
            Set<String> candidates = retriever.getCandidateSources(target);

            Map<String, Integer> richnessBySource = new HashMap<>();
            for(String source : candidates){
                HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);
                richnessBySource.put(source, mappingRichness(mapping));
            }
            return candidates.stream().sorted((a, b) -> Integer.compare(richnessBySource.get(b), richnessBySource.get(a))).collect(Collectors.toList());



        }
    }
