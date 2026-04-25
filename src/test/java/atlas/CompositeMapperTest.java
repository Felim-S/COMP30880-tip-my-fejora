package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompositeMapperTest {

    KnowledgeBase kb;

    @Before
    public void setup() throws Exception {
        kb = new KnowledgeBase(new StructureRewriter(RuleParser.parse("rewrite rules.txt")));
        kb.loadStructure("test structures.txt");
    }

    @Test
    public void testMappingBasic(){
        String source = "alpha";
        String target = "beta";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(9, mapping.size());
        assertEquals("serving", mapping.get("working"));
        assertEquals("offering", mapping.get("product"));
    }

    @Test
    public void testMappingSingleStructure(){
        String source = "gamma";
        String target = "delta";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(5, mapping.size());
        assertEquals("*delta", mapping.get("*gamma"));
        assertEquals("congregation", mapping.get("laboratory"));
    }

    @Test
    public void testEmptyMapping(){
        String source = "epsilon";
        String target = "zeta";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(0, mapping.size());
        assertNull(mapping.get("*epsilon"));
    }

    @Test
    public void testMappingWithConflict(){
        String source = "conflict1";
        String target = "conflict2";

        // there is a large rich structure and a smaller less
        // rich one, so only the larger, richer one should be generated

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(5, mapping.size());
        assertEquals("*conflict2", mapping.get("*conflict1"));
        // this should map to congregation (from the larger, richer structure)
        // and not to studio (from the smaller, less rich one)
        assertEquals("congregation", mapping.get("laboratory"));
    }

    @Test
    public void testMappingNonExistentTopic(){
        String source = "alpha";
        String target = "nonexistent";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(0, mapping.size());
    }

    @Test
    public void testMappingSymmetry(){
        HashMap<String, String> forward = CompositeMapper.generateCompositeMapping("alpha", "beta", kb);
        HashMap<String, String> reverse = CompositeMapper.generateCompositeMapping("beta", "alpha", kb);

        for (Map.Entry<String, String> entry : forward.entrySet()) {
            assertEquals(entry.getKey(), reverse.get(entry.getValue()));
        }
    }

    @Test
    public void testMappingBijection(){
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping("alpha", "beta", kb);
        Set<String> values = new HashSet<>(mapping.values());
        assertEquals(mapping.size(), values.size());
    }

    @Test
    public void testSelfMapping(){
        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping("alpha", "alpha", kb);
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void testMappingCrossKeys(){
        String source = "crossA";
        String target = "crossB";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(2, mapping.size());
        assertEquals("*crossB", mapping.get("*crossA"));
        // it correctly takes the first mapping studio -> medium
        assertEquals("medium", mapping.get("studio"));
        // and rejects (doesn't merge) the mapping orchestra -> studio
        // since it violates the 1-1 mapping
        assertNull(mapping.get("orchestra"));
    }

    @Test
    public void testMappingCrossValues(){
        String source = "crossC";
        String target = "crossD";

        HashMap<String, String> mapping = CompositeMapper.generateCompositeMapping(source, target, kb);

        assertEquals(2, mapping.size());
        assertEquals("*crossD", mapping.get("*crossC"));
        // it correctly takes the first mapping medium -> studio
        assertEquals("studio", mapping.get("medium"));
        // and rejects (doesn't merge) the mapping studio -> orchestra
        // since it violates the 1-1 mapping
        assertNull(mapping.get("studio"));
    }
}
