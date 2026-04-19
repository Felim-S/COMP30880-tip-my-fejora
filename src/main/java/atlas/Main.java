package atlas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String filename = "structured domains.txt";
        String target = args.length > 0 ? args[0] : "apple";

        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse("rewrite rules.txt")));
        kb.loadStructure(filename);

        System.out.println("Loaded structures across " + kb.getTopics().size() + " topics.\n");

        List<Structure> structures = kb.getStructuresForTopic(target);
        System.out.println("Structures about '" + target + "': " + structures.size());
        structures.forEach(s -> System.out.println("  " + s));

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources(target);
        System.out.println("\nRanked analogies for '" + target + "' (" + ranked.size() + " total):");
        ranked.stream().limit(10).forEach(source ->
                System.out.printf("  %-20s quality = %.4f%n", source, ranker.quality(source, target))
        );
    }
}
