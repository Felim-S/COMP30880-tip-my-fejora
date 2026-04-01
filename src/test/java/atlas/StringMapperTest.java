package atlas;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;


public class StringMapperTest {

    @Test
    public void testBasicMapping(){
        Map<String, String> mapping = StringMapper.generateMapping("(serve *priest (some congregation (that (perform worship))))",
                                                                    "(serve *soldier (some army (that (perform conquest))))");
        assertEquals("*soldier", mapping.get("*priest"));
        assertEquals("army", mapping.get("congregation"));
        assertEquals("conquest", mapping.get("worship"));
    }

    @Test
    public void testPredicatesNotInMapping(){
        Map<String, String> mapping = StringMapper.generateMapping("(serve *priest (some congregation (that (perform worship))))",
                                                                    "(serve *soldier (some army (that (perform conquest))))");
        assertFalse(mapping.containsKey("serve"));
        assertFalse(mapping.containsKey("some"));
        assertFalse(mapping.containsKey("that"));
        assertFalse(mapping.containsKey("perform"));
    }

    @Test
    public void testSimpleFlatMapping(){
        Map<String, String> mapping = StringMapper.generateMapping("(work *scientist laboratory)", "(work *priest congregation)");

        assertEquals("*priest", mapping.get("*scientist"));
        assertEquals("congregation", mapping.get("laboratory"));
    }

    @Test
    public void testIdenticalMapping(){
        Map<String, String> mapping = StringMapper.generateMapping("(serve *priest congregation)", "(serve *priest congregation)");

        assertEquals("*priest", mapping.get("*priest"));
        assertEquals("congregation", mapping.get("congregation"));
        //predicate should not be in mapping
        assertFalse(mapping.containsKey("serve"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsteriskMismatchThrows(){
        StringMapper.generateMapping("(serve *priest congregation)", "(serve soldier *army)");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testTwoSameSymbolsThrows(){
        //priest and congregation both mapping to soldier should throw
        StringMapper.generateMapping("(serve priest congregation)", "(serve soldier soldier)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentLengthThrows(){
        StringMapper.generateMapping("(serve *priest congregation)", "(serve *soldier (some army (that (perform conquest))))");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentPredicatesThrows(){
        StringMapper.generateMapping("(serve *priest congregation)", "(work *soldier army)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMismatchBracketThrows(){
        StringMapper.generateMapping("(serve *priest (some congregation (that (perform worship))))", "(serve *soldier some army (that (perform conquest)))");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInputThrows(){
        StringMapper.generateMapping(null, "(serve *priest congregation)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyInputThrows(){
        StringMapper.generateMapping("", "(serve *priest congregation)");
    }

    @Test
    public void testDeepNestedMapping(){
        Map<String, String> mapping = StringMapper.generateMapping("(serve *priest (some congregation (that (perform (for (some god)) (some worship)))))",
                "(serve *soldier (some army (that (perform (for (some leader)) (some conquest)))))");

        assertEquals("*soldier", mapping.get("*priest"));
        assertEquals("army", mapping.get("congregation"));
        assertEquals("leader", mapping.get("god"));
        assertEquals("conquest", mapping.get("worship"));
    }
}
