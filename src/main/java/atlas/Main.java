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
        int topN = Integer.parseInt(config.getProperty("top.n", "5"));

        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse(rulesFile)));
        kb.loadStructure(filename);
        System.out.println("Loaded " + kb.getTopics().size() + " topics from " + filename + "\n");

        // 5.1: Composite Mapping
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);
        System.out.printf("=== 5.1: Composite Mapping (%s -> %s) ===\n", source, target);
        mapping.forEach((k, v) -> { if (!k.equals(v)) System.out.printf("  %s -> %s\n", k, v); });
        System.out.printf("Richness: %d\n\n", CompositeRanker.mappingRichness(mapping));

        // 5.2: Ranked Sources
        CompositeRanker ranker = new CompositeRanker(kb);
        List<String> ranked = ranker.rankSources(target);
        System.out.printf("=== 5.2: Ranked Sources for \"%s\" ===\n", target);
        for (int i = 0; i < Math.min(topN, ranked.size()); i++) {
            String src = ranked.get(i);
            int richness = CompositeRanker.mappingRichness(CompositeMapper.generateCompositeMapping(src, target, kb));
            System.out.printf("  %d. %-20s (richness: %d)\n", i + 1, src, richness);
        }

        // 5.3: Top-N Retrieval
        CompositeRetriever retriever = new CompositeRetriever(kb);
        Map<String, HashMap<String, String>> top = retriever.getTopCompositeAnalogies(target, topN);
        System.out.printf("\n=== 5.3: Top %d Composite Analogies for \"%s\" ===\n", topN, target);
        int rank = 1;
        for (Map.Entry<String, HashMap<String, String>> e : top.entrySet()) {
            System.out.printf("  %d. %s (richness: %d)\n", rank++, e.getKey(), CompositeRanker.mappingRichness(e.getValue()));
            e.getValue().forEach((k, v) -> { if (!k.equals(v)) System.out.printf("       %s -> %s\n", k, v); });
        }
    }
}