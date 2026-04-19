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

        fw.write("food\t(eat *food apple)\t(eat * 0)\t1\n");
        fw.write("food\t(give *food *drink book)\t(give * 0 1)\t2\n");
        fw.write("adam\t(help *Adam Eve)\t(help * 0)\t3\n");
        fw.write("priest\t(perform *priest worship)\t(perform * 0)\t4\n");
        fw.write("deep\t(outer (inner *deep value))\t(outer (inner * 0))\t5\n");

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
