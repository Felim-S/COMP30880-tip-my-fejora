package atlas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RuleParser {

    private static final Logger logger = AtlasLogger.getLogger(RuleParser.class);

    public static Map<String, List<Rule>> parse(String filename) {
        Map<String, List<Rule>> ruleMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and the header line
                if (line.isEmpty() || line.startsWith("Predicate")) continue;

                // Split on tab to get predicate and its rewrite
                String[] parts = line.split("\t");
                if (parts.length < 2){
                    logger.warning("Malformed rule (missing rewrite column) at line: \"" + line + "\"");
                    continue;
                }

                String predicate = parts[0].trim();
                String rewriteSection = parts[1].trim();

                String[] rewrites = rewriteSection.split(",");

                List<Rule> rules = new ArrayList<>();
                for (String rewrite : rewrites) {
                    Rule rule = parseRule(predicate, rewrite.trim());
                    if (rule != null) {
                        rules.add(rule);
                    } else{
                        logger.warning("Empty rewrite for predicate \"" + predicate + "\" skipping");
                    }
                }

                ruleMap.computeIfAbsent(predicate, k -> new ArrayList<>()).addAll(rules);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read rules file: " + filename, e);
        }

        return ruleMap;
    }

    private static Rule parseRule(String predicate, String rewrite) {
        if (rewrite.isEmpty()) return null;

        //check for flags at start
        boolean negated = false;
        boolean switchArgs = false;
        boolean newAgent = false;

        int i = 0;
        while (i < rewrite.length()) {
            char c = rewrite.charAt(i);
            if (c == '!') { negated = true; i++; }
            else if (c == '<') { switchArgs = true; i++; }
            else if (c == '^') { newAgent = true; i++; }
            else break;
        }
        rewrite = rewrite.substring(i);

        //split off gerund
        // eg "respect_as:friend&disliking" -> gerund = "disliking"
        String gerund = null;
        if (rewrite.contains("&")) {
            String[] ampParts = rewrite.split("&");
            rewrite = ampParts[0];
            gerund = ampParts[1];
        }

        //split off colon element
        // eg "respect_as:friend" -> colonElement = "friend"
        String colonElement = null;
        boolean colonElemPushed = false;
        if (rewrite.contains(":")) {
            String[] colonParts = rewrite.split(":");
            rewrite = colonParts[0];
            colonElement = colonParts[1];
            // check for * after colon element
            if (colonElement.endsWith("*")) {
                colonElemPushed = true;
                colonElement = colonElement.substring(0, colonElement.length() - 1);
            }
        }

        String newVerb;
        String preposition = null;
        if (rewrite.contains("_")) {
            String[] verbParts = rewrite.split("_", 2);
            newVerb = verbParts[0];
            preposition = verbParts[1];
        } else {
            newVerb = rewrite;
        }

        return new Rule(predicate, negated, switchArgs, newAgent,
                newVerb, preposition, colonElement, colonElemPushed, gerund);
    }
}