package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class AnalogyRetrieverTest{

    private KnowledgeBase kb;
    private AnalogyRetriever retriever;

    @Before
    public void setUp(){
        Map<String, List<Rule>> rules = new java.util.HashMap<>();
        StructureRewriter rewriter = new StructureRewriter(rules);
        kb = new KnowledgeBase(rewriter);

        kb.addStructure(StructureParser.parse("(serve *priest (some congregation (that (perform worship))))"));
        kb.addStructure(StructureParser.parse("(serve *soldier (some army (that (perform conquest))))"));
        kb.addStructure(StructureParser.parse("(serve *scientist (some laboratory (that (perform experiment))))"));
        kb.addStructure(StructureParser.parse("(teach *teacher student)"));

        retriever = new AnalogyRetriever(kb);
    }

    //getCandidateSources

    @Test
    public void testGetCandidateSourcesNotNull(){
        Set<String> candidates = retriever.getCandidateSources("priest");
        assertNotNull(candidates);
    }

    @Test
    public void testSoldierIsCandidateForPriest(){
        //soldier has same shaped structure as priest so should be a candidate
        Set<String> candidates = retriever.getCandidateSources("priest");
        assertTrue(candidates.contains("soldier"));
    }

    @Test
    public void testScientistIsCandidateForPriest(){
        Set<String> candidates = retriever.getCandidateSources("priest");
        assertTrue(candidates.contains("scientist"));
    }

    @Test
    public void testTeacherNotCandidateForPriest(){
        //teacher has different shape so shouldnt be a candidate
        Set<String> candidates = retriever.getCandidateSources("priest");
        assertFalse(candidates.contains("teacher"));
    }

    @Test
    public void testUnknownTopicReturnsEmptySet(){
        Set<String> candidates = retriever.getCandidateSources("squirrel");
        assertNotNull(candidates);
        assertTrue(candidates.isEmpty());
    }


    //getAlignableStructures

    @Test
    public void testAlignableStructuresNotNull(){
        List<Structure[]> alignable = retriever.getAlignableStructures("soldier", "priest");
        assertNotNull(alignable);
    }

    @Test
    public void testAlignableStructuresCorrectSize(){
        List<Structure[]> alignable = retriever.getAlignableStructures("soldier", "priest");
        assertEquals(1, alignable.size());
    }

    @Test
    public void testAlignableSourceStructureContainsSource(){
        List<Structure[]> alignable = retriever.getAlignableStructures("soldier", "priest");
        String sourceString = alignable.get(0)[0].toString();
        assertTrue(sourceString.contains("*soldier"));
    }

    @Test
    public void testUnknownTargetHasNoAlignableStructures(){
        List<Structure[]> alignable = retriever.getAlignableStructures("priest", "devil");
        assertTrue(alignable.isEmpty());
    }

}