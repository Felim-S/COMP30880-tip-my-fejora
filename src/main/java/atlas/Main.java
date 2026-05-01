package atlas;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        }

        String filename = config.getProperty("kb.file", "structured domains.txt");
        String rulesFile = config.getProperty("rules.file", "rewrite rules.txt");
        String source = args.length >= 2 ? args[0] : config.getProperty("source", "priest");
        String target = args.length >= 2 ? args[1] : config.getProperty("target", "scientist");

        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse(rulesFile)));
        long startTime = System.currentTimeMillis();
        kb.loadStructure(filename);
        System.out.println("Loaded " + kb.getTopics().size() + " topics from " + filename + " in " + (System.currentTimeMillis() - startTime) + "ms" + "\n");

        // 6.1: Generating Candidate Inferences
        System.out.printf("=== 6.1: Composite Mapping (%s -> %s) ===\n", source, target);

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);
        mapping.entrySet().stream().limit(10).forEach(
                (k) -> {
                    if (!k.getKey().equals(k.getValue())) System.out.printf("  %s -> %s\n", k.getKey(), k.getValue());
                });
        System.out.println("... Total: " + mapping.size());
        System.out.printf("Richness: %d\n\n", CompositeRanker.mappingRichness(mapping));

        System.out.printf("=== Candidate Inferences (%s -> %s) ===\n", source, target);

        CandidateInferenceGenerator inferenceGenerator = new CandidateInferenceGenerator(kb);
        List<Structure> inferences = inferenceGenerator.generateCandidateInference(mapping, source, target);
        inferences.stream().limit(10).forEach(System.out::println);
        System.out.println("... Total: " + inferences.size());

        // 6.2:
        System.out.printf("\n===6.2: Coalesced Inferences (%s -> %s) ===\n", source, target);
        List<List<Structure>> coalescences = InferenceCoalescer.coalesce(inferences, mapping);
        System.out.println("Total coalescence groups: " + coalescences.size());
        for(int i = 0; i < coalescences.size(); i++){
            System.out.println("\nGroup " + (i + 1) + " (" + coalescences.get(i).size() + " inferences):");
            coalescences.get(i).stream().limit(10).forEach((s) -> System.out.println("  " + s));
        }

        // 6.3:
        System.out.printf("\n===6.3: Ranked Coalesced Inferences (%s -> %s) ===\n", source, target);
        AugmentedAnalogyRanker ranker = new AugmentedAnalogyRanker(kb);

        List<Map.Entry<List<Structure>, Double>> rankedGroups = ranker.ranking(source, target, coalescences, mapping);
        int rank = 1;

        for (var entry : rankedGroups) {
            System.out.printf("\nRank %d (score %.2f):\n", rank++, entry.getValue());

            entry.getKey().stream().limit(10).forEach(s -> System.out.println("  " + s));
        }

        // Extra : Bidirectional Analogy
        // Takes the source and target and computes composite mappings and candidate
        // inferences in both directions, source -> target + target -> source
        BidirectionalAnalogy.generateBidirectionalAnalogy(source, target, kb);
    }
}