package atlas;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StructureParserTest {
    @Test
    public void testBasicParse() {
        Structure structure = StructureParser.parse("(work in scientist (some lab (that (conduct experiment))))");
        assertEquals("work", structure.getHead().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedParse() {
        StructureParser.parse("(A B (C)");
    }
}
