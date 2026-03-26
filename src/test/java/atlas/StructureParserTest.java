package atlas;

import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void testNestedParseSymbol() {
        Structure structure = StructureParser.parse("(A B (C D))");
        assertFalse(structure.getElements().get(1).isStructure());
    }

    @Test
    public void testNestedParseStructure() {
        Structure structure = StructureParser.parse("(A B (C D))");
        assertTrue(structure.getElements().get(2).isStructure());
    }

    @Test
    public void testPredicate() {
        Structure structure = StructureParser.parse("(A B (C D))");
        assertTrue(structure.getHead() instanceof atlas.Predicate);
    }
}
