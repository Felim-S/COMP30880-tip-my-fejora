package atlas;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

        System.out.println("Loading rules from " + rulesFile);
        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse(rulesFile)));

        System.out.println("Loading structures from " + filename);
        kb.loadStructure(filename);
        System.out.println("Loaded structures across " + kb.getTopics().size() + " topics.\n");

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        System.out.printf("Generated composite mapping from: %s -> %s\n", source, target);
        System.out.println("-----------------------------------------------------");
        int total = 0;
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(!(key.equals(value))){
                System.out.printf("%s \t\t -> \t\t %s \n", key, value);
                total++;
            }
        }
        System.out.println("-----------------------------------------------------");
        System.out.printf("Total unique mappings from source to target: %d", total);
    }
}
