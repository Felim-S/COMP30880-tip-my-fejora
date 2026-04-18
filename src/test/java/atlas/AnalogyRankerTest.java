package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class AnalogyRankerTest {

    KnowledgeBase kb;

    @Before
    public void setUp() throws Exception {
        kb = new KnowledgeBase(new StructureRewriter(new HashMap<>()));
    }

    @Test
    public void testRichnessBasic() {
        Structure s = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        assertEquals(5.326, AnalogyRanker.richness(s), 0.001);
    }

    @Test
    public void testRichnessFlat() {
        Structure s = StructureParser.parse("(work_in scientist lab)");
        // count at depth 0: 3 (work_in, scientist, lab)
        // sum = 3, log10(3) = 0.477
        assertEquals(0.477, AnalogyRanker.richness(s), 0.001);
    }

    @Test
    public void testRichnessSingleArgument() {
        Structure s = StructureParser.parse("(that)");
        // count at depth 0: 1 (that)
        // sum = 1, log10(1) = 0
        assertEquals(0, AnalogyRanker.richness(s), 0.001);
    }

    @Test
    public void testRichnessHashIdenticalStructures() {
        Structure A = StructureParser.parse("(serve *priest (some congregation (that (perform (for (some god)) (some worship)))))");
        Structure B = StructureParser.parse("(serve *soldier (some army (that (perform (for (some leader)) (some conquest)))))");

        double r1 = AnalogyRanker.richness(A);
        double r2 = AnalogyRanker.richness(B);

        assertEquals(r1, r2, 0.001);
    }

    @Test
    public void testQualityBasic() {
        // One alignable pair: quality should be r(σ)^3
        Structure scientist = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform priest (some service (for congregation (that (conduct worship))))))");

        List<Structure[]> alignable = new ArrayList<>();
        alignable.add(new Structure[]{scientist, priest});

        double r = AnalogyRanker.richness(priest);
        double expected = Math.pow(r, 3);
        double q = AnalogyRanker.calculateQuality(alignable, 3);
        assertEquals(expected, q, 0.001);
    }

    @Test
    public void testQualityEmptyAlignableList() {
        // No alignable structures: quality should be 0
        List<Structure[]> alignable = new ArrayList<>();
        double q = AnalogyRanker.calculateQuality(alignable, 3);
        assertEquals(0.0, q, 0.001);
    }

    @Test
    public void testQualitySingleArgument() {
        // With a single argument: r = 0^3
        Structure A = StructureParser.parse("(that)");
        Structure B = StructureParser.parse("(this)");

        List<Structure[]> alignable = new ArrayList<>();
        alignable.add(new Structure[]{A, B});

        double q = AnalogyRanker.calculateQuality(alignable, 3);
        assertEquals(0, q, 0.001);
    }

    @Test
    public void testQualityMultipleAlignablePairs() {
        // Two pairs of alignable structures: quality should be r1^3 + r2^3
        Structure scientist = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform priest (some service (for congregation (that (conduct worship))))))");
        Structure simple = StructureParser.parse("(work_in scientist lab)");

        List<Structure[]> alignable = new ArrayList<>();
        alignable.add(new Structure[]{scientist, priest});
        alignable.add(new Structure[]{scientist, simple});

        double r1 = AnalogyRanker.richness(simple);
        double r2 = AnalogyRanker.richness(priest);
        double expected = Math.pow(r1, 3) + Math.pow(r2, 3);
        double q = AnalogyRanker.calculateQuality(alignable, 3);

        assertEquals(expected, q, 0.001);
    }

    @Test
    public void testQualityDifferentBetaValue() {
        // Beta = 4, so quality should be r(σ)^4
        Structure scientist = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform priest (some service (for congregation (that (conduct worship))))))");

        List<Structure[]> alignable = new ArrayList<>();
        alignable.add(new Structure[]{scientist, priest});

        double r = AnalogyRanker.richness(priest);
        double expected = Math.pow(r, 4);
        double q = AnalogyRanker.calculateQuality(alignable, 4);

        assertEquals(expected, q, 0.001);
    }

    @Test
    public void testQualityHigherRichness() {
        Structure scientist = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform priest (some service (for congregation (that (conduct worship))))))");
        Structure simple = StructureParser.parse("(work_in scientist lab)");

        List<Structure[]> richPair = new ArrayList<>();
        richPair.add(new Structure[]{scientist, priest});

        List<Structure[]> poorPair = new ArrayList<>();
        poorPair.add(new Structure[]{scientist, simple});

        double q1 = AnalogyRanker.calculateQuality(richPair, 3);
        double q2 = AnalogyRanker.calculateQuality(poorPair, 3);

        assertTrue(q1 > q2);
    }

    @Test
    public void testQualityWithAnalogyRetriever(){
        Structure scientist = StructureParser.parse(
                "(by working (perform *scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform *priest (some service (for congregation (that (conduct worship))))))");

        kb.addStructure(scientist);
        kb.addStructure(priest);

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        double r = AnalogyRanker.richness(priest);
        double expected = Math.pow(r, 3);

        double q = ranker.quality("scientist", "priest");

        assertEquals(expected, q, 0.001);
    }

    @Test
    public void testRankSourcesBasic() {
        // target: soldier - two structures
        kb.addStructure(StructureParser.parse("(serve *soldier army)"));
        kb.addStructure(StructureParser.parse("(fight *soldier battle)"));

        // priest aligns on both -> higher quality
        kb.addStructure(StructureParser.parse("(serve *priest army)"));
        kb.addStructure(StructureParser.parse("(fight *priest battle)"));

        // teacher aligns on one only -> lower quality
        kb.addStructure(StructureParser.parse("(serve *teacher army)"));

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources("soldier");

        assertEquals(2, ranked.size());
        assertEquals("priest", ranked.get(0));
        assertEquals("teacher", ranked.get(1));
    }

    @Test
    public void testRankSourcesEmpty() {
        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources("soldier");

        assertTrue(ranked.isEmpty());
    }

    @Test
    public void testRankSourcesSingle() {

        // soldier
        kb.addStructure(StructureParser.parse("(serve *soldier army)"));

        // priest
        kb.addStructure(StructureParser.parse("(serve *priest congregation)"));

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources("soldier");

        assertEquals(1, ranked.size());
    }

    @Test
    public void testRankSourcesExcludesTarget() {
        kb.addStructure(StructureParser.parse("(serve *soldier army)"));
        kb.addStructure(StructureParser.parse("(serve *priest army)"));

        AnalogyRetriever retriever = new AnalogyRetriever(kb);
        AnalogyRanker ranker = new AnalogyRanker(retriever);

        List<String> ranked = ranker.rankSources("soldier");

        assertFalse(ranked.contains("soldier"));
    }
}
