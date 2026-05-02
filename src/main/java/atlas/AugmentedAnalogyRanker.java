package atlas;

import java.util.*;

public class AugmentedAnalogyRanker {

    private final KnowledgeBase kb;
    private final AnalogyRetriever retriever;
    private final CandidateInferenceGenerator inferenceGenerator;
    private final int beta;
    private final double lambda;
    private final double gamma;

    public AugmentedAnalogyRanker(KnowledgeBase kb) {
        this(kb, 3, 0.5, 2.0);
    }

    public AugmentedAnalogyRanker(KnowledgeBase kb, int beta, double lambda, double gamma) {
        this.kb = kb;
        this.lambda = lambda;
        this.gamma = gamma;
        this.beta = beta;
        this.retriever = new AnalogyRetriever(kb);
        this.inferenceGenerator = new CandidateInferenceGenerator(kb);
    }

    public double augmentedQuality(String source, String target) {
        return augmentedQuality(source, target, beta);
    }

    // main scoring function, combines base alignment, candidate inferences and coherence
    public double augmentedQuality(String source, String target, int beta) {
        double baseScore = computeBaseScore(source, target, beta);

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);
        List<Structure> inferences = inferenceGenerator.generateCandidateInference(mapping, source, target);

        double inferenceScore = 0.0;
        for (Structure structure : inferences) {
            double r = AnalogyRanker.richness(structure);
            inferenceScore += Math.pow(r, beta);
        }

        List<List<Structure>> groups = InferenceCoalescer.coalesce(inferences, mapping);

        double coherenceScore = 0.0;
        for (List<Structure> group : groups) {
            double groupRichness = 0.0;
            for (Structure structure : group) {
                groupRichness += AnalogyRanker.richness(structure);
            }
            coherenceScore += Math.pow(groupRichness, beta);
        }

        return baseScore + lambda * inferenceScore + gamma * coherenceScore;
    }

    // returns group rankings
    public List<Map.Entry<List<Structure>, Double>> ranking(
            String source,
            String target,
            List<List<Structure>> groups,
            HashMap<String, String> mapping) {

        Map<List<Structure>, Double> scoredGroups = new HashMap<>();

        double baseScore = computeBaseScore(source, target, beta);

        for (List<Structure> group : groups) {
            double inferenceScore = 0.0;
            double groupRichness = 0.0;

            for (Structure structure : group) {
                double r = AnalogyRanker.richness(structure);
                inferenceScore += Math.pow(r, beta);
                groupRichness += r;
            }

            double coherenceScore = Math.pow(groupRichness, beta);

            double finalScore = baseScore + lambda * inferenceScore + gamma * coherenceScore;
            scoredGroups.put(group, finalScore);
        }

        return scoredGroups.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
    }

    public List<Map.Entry<List<Structure>, Double>> ranking(
            String source,
            String target,
            List<List<Structure>> groups,
            HashMap<String, String> mapping,
            int beta) {
        return ranking(source, target, groups, mapping);
    }


    private double computeBaseScore(String source, String target, int beta) {
        double baseScore = 0.0;
        List<Structure[]> alignable = retriever.getAlignableStructures(source, target);
        for (Structure[] structure : alignable) {
            double r = AnalogyRanker.richness(structure[1]);
            baseScore += Math.pow(r, beta);
        }
        return baseScore;
    }
}