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
}
