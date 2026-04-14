package atlas;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.err.println("Usage: atlas <rules-file> <structure>");
//            System.exit(1);
//        }
//
//        String rulesFile = args[0];
//        String input = args[1];
//
//        Map<String, List<Rule>> rules = RuleParser.parse(rulesFile);
//        Structure structure = StructureParser.parse(input);
//        StructureRewriter rewriter = new StructureRewriter(rules);
//
//        List<Structure> results = rewriter.rewrite(structure);
//
//        System.out.println("Original:");
//        System.out.println(structure);
//        System.out.println("\nRewrites (" + (results.size() - 1) + "):");
//        for (int i = 1; i < results.size(); i++) {
//            System.out.println(results.get(i));
//        }
        Structure structure = StructureParser.parse("(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        structure = StructureParser.parse("(work_in scientist (some laboratory (that (conduct experiment))))");
        System.out.println("Structure: " + structure);
        double richness = AnalogyRanker.richness(structure);
        System.out.println("Richness: " + richness);

    }
}
