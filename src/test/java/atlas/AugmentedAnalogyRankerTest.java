package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AugmentedAnalogyRankerTest {

    private KnowledgeBase kb;
    private AugmentedAnalogyRanker ranker;

    @Before
    public void setUp() {
        kb = new KnowledgeBase(new StructureRewriter(new HashMap<>()));

        kb.addStructure(StructureParser.parse("(serve *priest (some congregation (that (perform worship))))"));
        kb.addStructure(StructureParser.parse("(serve *scientist (some laboratory (that (perform experiment))))"));
        kb.addStructure(StructureParser.parse("(teach *teacher student)"));

        ranker = new AugmentedAnalogyRanker(kb);
    }

    @Test
    public void testBasicAugmentedQuality() {
        double score = ranker.augmentedQuality("priest", "scientist");
        assertTrue(score > 0);
    }

    @Test
    public void testAugmentedQualityEmptyCase() {
        double score = ranker.augmentedQuality("", "scientist");
        assertEquals(0.0, score, 0.001);
    }
}
