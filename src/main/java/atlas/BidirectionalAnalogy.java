package atlas;

import java.util.*;

public class BidirectionalAnalogy {
    public static void generateBidirectionalAnalogy(String source, String target, KnowledgeBase kb) {
        System.out.println("\n\n=== Bidirectional Analogy ===\n\n");

        HashMap<String, String> mappingA = CompositeMapper.generateCompositeMapping(source, target, kb);
        HashMap<String, String> mappingB = CompositeMapper.generateCompositeMapping(target, source, kb);

        System.out.printf("=== Composite Mapping (%s -> %s) ===\n", source, target);
        mappingA.entrySet().stream().limit(5).forEach(
                (k) -> {
                    if (!k.getKey().equals(k.getValue())) System.out.printf("  %s -> %s\n", k.getKey(), k.getValue());
                });
        int richnessA = CompositeRanker.mappingRichness(mappingA);
        System.out.printf("Richness: %d\n\n", richnessA);

        System.out.printf("=== Composite Mapping (%s -> %s) ===\n", target, source);
        mappingB.entrySet().stream().limit(5).forEach(
                (k) -> {
                    if (!k.getKey().equals(k.getValue())) System.out.printf("  %s -> %s\n", k.getKey(), k.getValue());
                });
        int richnessB = CompositeRanker.mappingRichness(mappingB);
        System.out.printf("Richness: %d\n\n", richnessB);

        CandidateInferenceGenerator inferenceGenerator = new CandidateInferenceGenerator(kb);

        List<Structure> inferencesA = inferenceGenerator.generateCandidateInference(mappingA, source, target);
        List<Structure> inferencesB = inferenceGenerator.generateCandidateInference(mappingB, target, source);

        System.out.printf("=== Candidate Inferences (%s -> %s) ===\n", source, target);
        inferencesA.stream().limit(5).forEach(System.out::println);
        System.out.println("... Total: " + inferencesA.size());

        System.out.printf("=== Candidate Inferences (%s -> %s) ===\n", target, source);
        inferencesB.stream().limit(5).forEach(System.out::println);
        System.out.println("... Total: " + inferencesB.size());

        // A confirmed inference is one from A->B that, when reverse-mapped via mappingB,
        // gives a structure already in the source KB.
        Set<String> sourceStructureStrings = new HashSet<>();
        for (Structure s : kb.getStructuresForTopic(source)) {
            sourceStructureStrings.add(s.toString());
        }

        List<Structure> confirmed = new ArrayList<>();
        for (Structure inference : inferencesA) {
            if (sourceStructureStrings.contains(translate(inference, mappingB).toString())) {
                confirmed.add(inference);
            }
        }

        System.out.printf("\n=== Confirmed Inferences (%s <-> %s) ===\n", source, target);
        confirmed.stream().limit(10).forEach(System.out::println);
        System.out.println("... Total: " + confirmed.size());

        // The symmetry score is a metric of which direction was richer (dominant)
        // - if both directions are exactly symmetrical, the score is 1.0
        // - if it is completely one-sided, the score would be 0.0

        double symmetryScore = (richnessA == 0 && richnessB == 0) ? 0.0
                : (double) Math.min(richnessA, richnessB) / Math.max(richnessA, richnessB);
        String dominant = richnessA >= richnessB
                ? String.format("%s -> %s", source, target)
                : String.format("%s -> %s", target, source);
        System.out.printf("\n\nSymmetry Score: %.2f (dominant direction: %s)\n\n", symmetryScore, dominant);
    }

    private static Structure translate(Structure s, Map<String, String> mapping) {
        Structure translated = new Structure();
        for (Element element : s.getElements()) {
            if (element instanceof Predicate) {
                translated.addElement((Predicate) element);
            } else if (element instanceof Symbol) {
                String mapped = mapping.get(element.toString());
                translated.addElement(new Symbol(mapped != null ? mapped : element.toString()));
            } else if (element instanceof Structure) {
                translated.addElement(translate((Structure) element, mapping));
            }
        }
        return translated;
    }
}
