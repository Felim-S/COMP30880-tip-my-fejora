package atlas;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    }
}
