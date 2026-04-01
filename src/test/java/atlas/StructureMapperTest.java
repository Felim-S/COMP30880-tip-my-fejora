package atlas;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StructureMapperTest {

    Structure A;
    Structure B;

    @Before
    public void setUp() throws Exception {
        A = StructureParser.parse("(serve *priest (some congregation (that (perform (for (some god)) (some worship)))))");
        B = StructureParser.parse("(serve *soldier (some army (that (perform (for (some leader)) (some conquest)))))");
    }

    @Test
    public void testBasicMapping() {
        Map<String, String> map = StructureMapper.generateMapping(A, B);
        assertNotNull(map);
        assertEquals("*soldier", map.get("*priest"));
        assertEquals("army", map.get("congregation"));
        assertEquals("leader", map.get("god"));
        assertEquals("conquest", map.get("worship"));
        assertEquals(4, map.size());
    }

    @Test
    public void testIdenticalStructuresAreMappable(){
        Structure a = StructureParser.parse("(serve *priest (some congregation (that (perform worship))))");
        Structure b = StructureParser.parse("(serve *priest (some congregation (that (perform worship))))");

        assertTrue(StructureMapper.isMappable(a, b));
    }

    @Test
    public void testAnalogousStructuresAreMappable(){
        Structure a = StructureParser.parse("(serve *priest (some congregation (that (perform worship))))");
        Structure b = StructureParser.parse("(serve *soldier (some army (that (perform conquest))))");

        assertTrue(StructureMapper.isMappable(a, b));
    }

    @Test
    public void testDifferentLengthsNotMappable(){
        Structure a = StructureParser.parse("(serve *priest congregation)");
        Structure b = StructureParser.parse("(serve *soldier (some army (that (perform conquest))))");

        assertFalse(StructureMapper.isMappable(a, b));
    }

    @Test
    public void testDifferentPredicatesNotMappable(){
        Structure a = StructureParser.parse("(serve *priest congregation)");
        Structure b = StructureParser.parse("(fight *soldier conquest)");

        assertFalse(StructureMapper.isMappable(a, b));
    }

    @Test
    public void testAsteriskRule(){
        Structure a = StructureParser.parse("(serve *priest (some congregation (that (perform worship))))");
        Structure b = StructureParser.parse("(serve soldier (some army (that (perform conquest))))");

        assertFalse(StructureMapper.isMappable(a, b));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsteriskRuleGenerateMapping(){
        B = StructureParser.parse("(serve soldier (some army (that (perform (for (some leader)) (some conquest)))))");
        StructureMapper.generateMapping(A,B);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectOneToOneMapping(){
        Structure a = StructureParser.parse("(A B B)");
        Structure b = StructureParser.parse("(A C D)");
        StructureMapper.generateMapping(a, b);
    }

    @Test
    public void testIdentityMapping(){
        Map<String, String> map = StructureMapper.generateMapping(A, A);
        assertEquals("*priest", map.get("*priest"));
        assertEquals("congregation", map.get("congregation"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyInput(){
        A =  StructureParser.parse("");
        B =  StructureParser.parse("");
        StructureMapper.generateMapping(A, B);
    }

    @Test
    public void testEmptyMapping(){
        A =  StructureParser.parse("()");
        B =  StructureParser.parse("()");
        Map<String, String> map = StructureMapper.generateMapping(A, B);
        assertEquals(0, map.size());
    }
}
