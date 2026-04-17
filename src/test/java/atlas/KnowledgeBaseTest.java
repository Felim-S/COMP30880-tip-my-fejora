package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class KnowledgeBaseTest {

    private String testFile;
    private KnowledgeBase kb;

    @Before
    public void setUp() throws IOException {
        StructureRewriter rewriter = new StructureRewriter(new HashMap<>()) {
            @Override
            public List<Structure> rewrite(Structure structure) {
                return List.of(structure);
            }
        };

        kb = new KnowledgeBase(rewriter);

        testFile = "kb_test.txt";
        FileWriter fw = new FileWriter(testFile);

        fw.write("(eat *food apple)\n");
        fw.write("(give *food *drink book)\n");
        fw.write("(help *Adam Eve)\n");
        fw.write("(perform *priest worship)\n");
        fw.write("(outer (inner *deep value))\n");

        fw.close();
    }

    @Test
    public void testTopicIndexing() throws Exception {

        kb.loadStructure(testFile);

        List<Structure> food = kb.getStructuresForTopic("food");
        List<Structure> drink = kb.getStructuresForTopic("drink");
        List<Structure> Adam = kb.getStructuresForTopic("Adam");

        assertEquals(2, food.size());
        assertEquals(1, drink.size());
        assertEquals(1, Adam.size());
    }

    @Test
    public void nestedStructureIndexing() throws Exception {

        kb.loadStructure(testFile);

        List<Structure> result = kb.getStructuresForTopic("deep");
        assertEquals(1, result.size());
    }

    @Test
    public void testHashIndexing() throws Exception {

        kb.loadStructure(testFile);

        Structure s = new Structure();
        s.addElement(new Predicate("perform"));
        s.addElement(new Symbol("*priest"));
        s.addElement(new Symbol("worship"));

        String hash = StructureAbstractor.generateAbstraction(s).toString();
        List<Structure> result = kb.getStructuresByHash(hash);

        assertFalse(result.isEmpty());
    }

    @Test
    public void unknownTopicTest() throws Exception {

        kb.loadStructure(testFile);

        List<Structure> result = kb.getStructuresForTopic("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTopics() throws Exception {

        kb.loadStructure(testFile);

        List<String> topics = kb.getTopics();

        assertTrue(topics.contains("food"));
        assertTrue(topics.contains("drink"));
        assertTrue(topics.contains("Adam"));
    }

    @Test
    public void emptyFileTest() throws Exception {

        FileWriter fw = new FileWriter(testFile);

        fw.write("");
        fw.close();

        kb.loadStructure(testFile);

        assertTrue(kb.getTopics().isEmpty());
    }
}
