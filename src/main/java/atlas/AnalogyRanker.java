package atlas;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

public class AnalogyRanker {

    private static final int DEFAULT_BETA = 3;

    private KnowledgeBase kb;
    private AnalogyRetriever retriever;

    public AnalogyRanker(KnowledgeBase kb, AnalogyRetriever retriever) {
        this.kb = kb;
        this.retriever = retriever;
    }

    public static double richness(Structure structure) {
        Map<Integer, Integer> count = calculateCount(structure, 0);

        double sum = 0.0;

        for(int i = 0; i < count.size(); i++){
            sum += (count.get(i)) * pow(10, i);
        }

        return Math.log10(sum);
    }

    private static Map<Integer, Integer> calculateCount(Structure structure, int i) {
        Map<Integer, Integer> count = new HashMap<>();

        for (Element element : structure.getElements()) {
            if (element.isStructure()) {
                Map<Integer, Integer> childCount = calculateCount((Structure) element, i + 1);
                for (Map.Entry<Integer, Integer> entry : childCount.entrySet()) {
                    count.put(entry.getKey(), count.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            } else {
                count.put(i, count.getOrDefault(i, 0) + 1);
            }
        }

        return count;
    }

    public static double calculateQuality(List<Structure[]> alignable, int beta) {
        double sum = 0.0;

        for (Structure[] pair : alignable) {
            double r = richness(pair[1]);
            sum += Math.pow(r, beta);
        }

        return sum;
    }

    public double quality(String source, String target) {
        return calculateQuality(retriever.getAlignableStructures(source, target), DEFAULT_BETA);
    }

    public double quality(String source, String target, int beta) {
        return calculateQuality(retriever.getAlignableStructures(source, target), beta);
    }

    public List<String> rankSources(String target) {
        return rankSources(target, DEFAULT_BETA);
    }

    public List<String> rankSources(String target, int beta) {
        Set<String> candidateSources = retriever.getCandidateSources(target);

        return candidateSources.stream()
                .sorted((a,b) -> Double.compare(
                        quality(b, target, beta),
                        quality(a, target, beta)
                ))
                .collect(Collectors.toList());
    }
}
