package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CandidateInferenceGeneratorTest {

    KnowledgeBase kb;
    CandidateInferenceGenerator candidateInferenceGenerator;

    @Before
    public void setUp() throws Exception {
        kb = new KnowledgeBase(new StructureRewriter(new HashMap<>()));
        kb.loadStructure("test structures.txt");
        candidateInferenceGenerator = new CandidateInferenceGenerator(kb);
    }

    @Test
    public void testBasicGenerateCandidateInferences() {
        String S = "inferSource";
        String T = "inferTarget";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(S, T, kb);

        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, S, T);

        assertEquals(1, inferences.size());
        assertEquals("(enjoy *inferTarget congregation)", inferences.getFirst().toString());
    }

    @Test
    public void testInferencesContainTargetSymbols() {
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping("inferSource", "inferTarget", kb);
        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, "inferSource", "inferTarget");
        for (Structure inf : inferences) {
            assertTrue(inf.toString().contains("*inferTarget"));
            assertFalse(inf.toString().contains("*inferSource"));
        }
    }

    @Test
    public void testEmptyGenerateCandidateInferences() {
        String S = "inferEmptySource";
        String T = "inferEmptyTarget";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(S, T, kb);

        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, S, T);

        assertEquals(0, inferences.size());
    }

    @Test
    public void testMultipleGenerateCandidateInferences() {
        String S = "inferMultiSource";
        String T = "inferMultiTarget";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(S, T, kb);

        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, S, T);

        assertEquals(3, inferences.size());
        assertEquals("(enjoy *inferMultiTarget service)", inferences.get(0).toString());
        assertEquals("(enjoy *inferMultiTarget congregation)", inferences.get(1).toString());
        assertEquals("(enjoy *inferMultiTarget worship)", inferences.get(2).toString());
    }

    @Test
    public void testNoDuplicateInferences() {
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping("inferDupSource", "inferDupTarget", kb);
        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, "inferDupSource", "inferDupTarget");
        assertEquals(1, inferences.size());
    }

    @Test
    public void testPredicatesPreserved() {
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping("inferSource", "inferTarget", kb);
        List<Structure> inferences = candidateInferenceGenerator.generateCandidateInference(mapping, "inferSource", "inferTarget");

        assertEquals(1, inferences.size());
        // "enjoy" is the predicate — it should survive translation unchanged
        assertTrue(inferences.getFirst().toString().contains("enjoy"));
    }
}
