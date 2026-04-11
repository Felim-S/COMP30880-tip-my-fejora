package atlas;

import org.junit.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StructureRewriterTest {

    private StructureRewriter rewriter;
    @Before
    public void setup() {
        Map<String, List<Rule>> rules = new HashMap<>();

        rules.put("like", List.of(
                new Rule("like", false, false, false,
                "love", null, null, false, null)));

        // negation rule
        rules.put("hate", List.of(
                new Rule("hate", true, false, false,
                        "like", null, null, false, null)));

        // switch args rule
        rules.put("give", List.of(
                new Rule("give", false, true, false,
                        "receive", null, null, false, null)));

        // new agent rule
        rules.put("teach", List.of(
                new Rule("teach", false, false, true,
                "learn", null, "teacher", false, null)));

        // gerund test
        rules.put("help", List.of(
                new Rule("help", false, false, false,
                        "support", null, null, false, "helping")));

        // colon element & preposition test
        rules.put("respect", List.of(
                new Rule("respect", false, false, false,
                        "admire", "as", "leader", false, null)));

        // multiple rules test
        rules.put("multiple", List.of(
                new Rule("multiple", false, false, false,
                        "one", null, null, false, null),
                new Rule("multiple", false, false, false,
                        "two", null, null, false, null)));

        rewriter = new StructureRewriter(rules);
    }

    @Test
    public void basicRewriterTest() {
        Structure s = StructureParser.parse("(like cats dogs)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("love"));
        assertTrue(output.contains("(like cats dogs)"));
    }

    @Test
    public void negationTest() {
        Structure s = StructureParser.parse("(hate cats dogs)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("not"));
        assertTrue(output.contains("like"));
    }

    @Test
    public void switchArgsTest() {
        Structure s = StructureParser.parse("(give Adam apple)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("(receive apple Adam)"));
    }

    @Test
    public void newAgentTest() {
        Structure s = StructureParser.parse("(teach Adam Eve)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("teacher"));
    }

    @Test
    public void gerundTest() {
        Structure s = StructureParser.parse("(help Adam Eve)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("by"));
        assertTrue(output.contains("helping"));
    }

    @Test
    public void colonAndPrepositionTest() {
        Structure s = StructureParser.parse("(respect Adam Eve)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();

        assertTrue(output.contains("as"));
        assertTrue(output.contains("leader"));
    }

    @Test
    public void unknownRuleTest() {
        Structure s = StructureParser.parse("(unknown Adam Eve)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();
        assertEquals(1, results.size());
        assertTrue(output.contains("(unknown Adam Eve)"));
    }

    @Test
    public void multipleRulesTest() {
        Structure s = StructureParser.parse("(multiple Adam Eve)");

        List<Structure> results = rewriter.rewrite(s);

        String output = results.toString();
        assertTrue(output.contains("one"));
        assertTrue(output.contains("two"));
    }
}