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

    @Test
    public void testRankingSortedDescending() {
        List<Structure> group1 = new ArrayList<>();
        group1.add(StructureParser.parse("(work_in scientist lab)"));

        List<Structure> group2 = new ArrayList<>();
        group2.add(StructureParser.parse("(by working (perform scientist (some work (for lab (that (conduct experiment))))))"));

        List<List<Structure>> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        HashMap<String, String> mapping = new HashMap<>();

        List<Map.Entry<List<Structure>, Double>> ranked = ranker.ranking("priest", "scientist", groups, mapping);

        assertTrue(ranked.get(0).getValue() >= ranked.get(1).getValue());
    }

    @Test
    public void testRankingEmptyGroups() {
        List<List<Structure>> groups = new ArrayList<>();
        HashMap<String, String> mapping = new HashMap<>();

        List<Map.Entry<List<Structure>, Double>> ranked = ranker.ranking("priest", "scientist", groups, mapping);

        assertTrue(ranked.isEmpty());
    }

    @Test
    public void testCoherenceIncreasesScore() {
        Structure s1 = StructureParser.parse("(work_in scientist lab)");
        Structure s2 = StructureParser.parse("(use scientist equipment)");

        List<Structure> combined = List.of(s1, s2);
        List<Structure> separate1 = List.of(s1);
        List<Structure> separate2 = List.of(s2);

        List<List<Structure>> groups = List.of(combined, separate1, separate2);
        HashMap<String, String> mapping = new HashMap<>();

        List<Map.Entry<List<Structure>, Double>> ranked = ranker.ranking("priest", "scientist", groups, mapping);

        assertEquals(combined, ranked.get(0).getKey());
    }

    @Test
    public void testDifferentBetaChangesScore() {
        double score1 = ranker.augmentedQuality("priest", "scientist", 2);
        double score2 = ranker.augmentedQuality("priest", "scientist", 4);

        assertNotEquals(score1, score2);
    }

    @Test
    public void testSingleGroupRanking() {
        List<Structure> group = List.of(StructureParser.parse("(work_in scientist lab)"));

        List<List<Structure>> groups = List.of(group);
        HashMap<String, String> mapping = new HashMap<>();

        List<Map.Entry<List<Structure>, Double>> ranked = ranker.ranking("priest", "scientist", groups, mapping);

        assertEquals(1, ranked.size());
        assertEquals(group, ranked.get(0).getKey());
    }
}