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
        // TODO

        // 6.3:
        // TODO
    }
}