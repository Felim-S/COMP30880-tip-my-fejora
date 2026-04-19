package atlas;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        }

        String filename = config.getProperty("kb.file", "structured domains.txt");
        String rulesFile = config.getProperty("rules.file", "rewrite rules.txt");
        String target = args.length > 0 ? args[0] : config.getProperty("target", "Valhalla");
        int beta = Integer.parseInt(config.getProperty("beta", "3"));
        int limit = Integer.parseInt(config.getProperty("results.limit", "10"));


        System.out.println("Loading rules from " + rulesFile);
        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse(rulesFile)));

        System.out.println("Loading structures from " + filename);
        kb.loadStructure(filename);
        System.out.println("Loaded structures across " + kb.getTopics().size() + " topics.\n");

        List<Structure> structures = kb.getStructuresForTopic(target);
        System.out.println("Structures about '" + target + "': " + structures.size());
        structures.forEach(s -> System.out.println("  " + s));

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources(target, beta);
        System.out.println("\nRanked analogies for '" + target + "' (" + ranked.size() + " total):");
        ranked.stream().limit(limit).forEach(source ->
                System.out.printf("  %-20s quality = %.4f%n", source, ranker.quality(source, target, beta))
        );
    }
}
