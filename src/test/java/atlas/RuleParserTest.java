package atlas;

import org.junit.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class RuleParserTest {

    private String testFile;

    @Before
    public void setUp() throws IOException {

        testFile = "rules.txt";
        FileWriter fw = new FileWriter(testFile);

        fw.write("Predicate\tAbstraction\n");
        fw.write("exercise\tperform_of:exercise*&exercising\n");
        fw.write("flex\tdisplay_with:effort&flexing\n");
        fw.write("dislike\t!respect_as:friend&disliking\n");
        fw.write("lose_control_over\t<!respect_as:leader&rejecting_control\n");
        fw.write("flunk\t^reject_for:teacher&flunking\n");
        fw.write("undermine\t!support_as:leader&undermining, !respect_as:leader&undermining\n");
        fw.write("disappoint\t!respect_for:competence, imbue_with:disappointment");
        fw.close();
    }

    @Test
    public void testRuleParser() {
        Map<String, List<Rule>> rules = RuleParser.parse(testFile);
        Rule rule = rules.get("exercise").getFirst();

        assertEquals("exercise", rule.getOriginalPredicate());
        assertEquals("perform", rule.getNewVerb());
        assertEquals("of", rule.getPreposition());
        assertEquals("exercise", rule.getColonElement());
        assertTrue(rule.isColonElementPushed());
        assertEquals("exercising", rule.getGerund());
    }

    @Test
    public void negationFlagTest() {
        Map<String, List<Rule>> rules = RuleParser.parse(testFile);
        Rule rule = rules.get("dislike").getFirst();
        assertTrue(rule.isNegated());
    }

    @Test
    public void switchArgsFlagTest() {
        Map<String, List<Rule>> rules = RuleParser.parse(testFile);
        Rule rule = rules.get("lose_control_over").getFirst();
        assertTrue(rule.isSwitchArgs());
    }

    @Test
    public void newAgentFlagTest() {
        Map<String, List<Rule>> rules = RuleParser.parse(testFile);
        Rule rule = rules.get("flunk").getFirst();
        assertTrue(rule.isNewAgent());
        assertEquals("teacher", rule.getColonElement());
    }

    @Test
    public void multipleRulesTest() {
        Map<String, List<Rule>>  rules = RuleParser.parse(testFile);
        List<Rule> list = rules.get("undermine");
        assertEquals(2,  list.size());
    }

    @Test
    public void noGerundTest() {
        Map<String, List<Rule>> rules = RuleParser.parse(testFile);
        Rule rule = rules.get("disappoint").getFirst();
        assertNull(rule.getGerund());
    }
}