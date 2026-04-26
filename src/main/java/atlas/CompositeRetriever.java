package atlas;

import java.util.*;
import java.util.stream.Collectors;

public class CompositeRetriever {
    private final KnowledgeBase kb;
    private final AnalogyRetriever retriever;

    public  CompositeRetriever(KnowledgeBase kb) {
        this.kb = kb;
        this.retriever = new AnalogyRetriever(kb);
    }

    // retrieve top n composite analogies for a given target
    public Map<String, HashMap<String, String>> getTopCompositeAnalogies(String target, int n) {

        Set<String> candidates = retriever.getCandidateSources(target);

        // store mapping and richness together
        Map<String, HashMap<String, String>> mappings = new HashMap<>();
        Map<String, Integer> richnessScores = new HashMap<>();

        for (String source : candidates) {
            HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

            int richness = CompositeRanker.mappingRichness(mapping);

            mappings.put(source, mapping);
            richnessScores.put(source, richness);
        }

        // sort sources by richness, limiting to top n
        List<String> topSources = candidates.stream().sorted((a, b) -> Integer.compare(
                richnessScores.get(b), richnessScores.get(a)))
                .limit(n)
                .collect(Collectors.toList());

        Map<String, HashMap<String, String>> result = new LinkedHashMap<>();
        for (String source : topSources) {
            result.put(source, mappings.get(source));
        }

        return result;
    }
}
