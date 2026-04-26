package atlas;

import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class CompositeRetrieverTest {

    KnowledgeBase kb;
    CompositeRetriever retriever;

    @Before
    public void setUp() {
        kb = new KnowledgeBase(new StructureRewriter(new java.util.HashMap<>()));

        kb.addStructure(StructureParser.parse("(performs *priest worship)"));
        kb.addStructure(StructureParser.parse("(leads *priest congregation)"));

        kb.addStructure(StructureParser.parse("(performs *teacher lesson)"));
        kb.addStructure(StructureParser.parse("(leads *teacher class)"));

        kb.addStructure(StructureParser.parse("(performs *musician concert)"));

        retriever = new CompositeRetriever(kb);
    }

    @Test
    public void returnsNResultsTest() {
        Map<String, HashMap<String, String>> results = retriever.getTopCompositeAnalogies("priest", 1);

        assertTrue(results.size() <= 1);
    }

    @Test
    public void unknownTargetTest() {
        Map<String, HashMap<String, String>> results = retriever.getTopCompositeAnalogies("unknown", 3);

        assertEquals(0, results.size());
    }
}