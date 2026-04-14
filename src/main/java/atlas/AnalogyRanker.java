package atlas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalogyRanker {

    private static final int DEFAULT_BETA = 3;

    private KnowledgeBase kb;
    private AnalogyRetriever retriever;

    public AnalogyRanker(KnowledgeBase kb, AnalogyRetriever retriever) {}

    public static double richness(Structure structure) {
        Map<Integer, Integer> count = calculateCount(structure, 0);

        double sum = 0.0;

        for(int i = 0; i < count.size(); i++){
            sum += (count.get(i)) * Math.pow(10, i);
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

    public double quality(String source, String target) { return 0; }

    public double quality(String source, String target, int beta) { return 0; }

    public List<String> rankSources(String target) { return null; }

    public List<String> rankSources(String target, int beta) { return null; }
}
