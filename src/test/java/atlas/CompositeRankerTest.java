package atlas;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import static org.junit.Assert.*;


public class CompositeRankerTest {

    @Test
    public void testMappingRichnessCountsNonIdentityPairsOnly() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*priest", "*scientist");
        mapping.put("church", "laboratory");
        mapping.put("god", "god");
        mapping.put("worship", "worship");
        mapping.put("parish", "university");

        int richness = CompositeRanker.mappingRichness(mapping);
        assertEquals(3, richness);
    }

    @Test
    public void testRankSourcesOrderedByRichness() throws Exception {

        KnowledgeBase kb = new KnowledgeBase(new StructureRewriter(new java.util.HashMap<>()));

        kb.addStructure(StructureParser.parse("(leads *scientist lab)"));
        kb.addStructure(StructureParser.parse("(uses *scientist microscope)"));

        kb.addStructure(StructureParser.parse("(leads *priest church)"));
        kb.addStructure(StructureParser.parse("(uses *priest bible)"));

        kb.addStructure(StructureParser.parse("(leads *soldier army)"));

        CompositeRanker ranker = new CompositeRanker(kb);
        List<String> ranked = ranker.rankSources("scientist");

        assertTrue(ranked.indexOf("priest") < ranked.indexOf("soldier"));
    }
}