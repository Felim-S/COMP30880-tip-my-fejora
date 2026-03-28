package atlas;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StructureTest {

    Structure structure = new Structure();

    @Before
    public void setUp() {
        structure.addElement(new Predicate("serve"));
        structure.addElement(new Symbol("priest"));

        Structure structure2 = new Structure();

        structure2.addElement(new Predicate("some"));
        structure2.addElement(new Symbol("congregation"));

        Structure structure3 = new Structure();

        structure3.addElement(new Predicate("that"));

        structure2.addElement(structure3);

        Structure structure4 = new Structure();

        structure4.addElement(new Predicate("perform"));
        structure4.addElement(new Symbol("worship"));

        structure3.addElement(structure4);

        structure.addElement(structure2);
    }

    @Test
    public void testToString() {
        assertEquals("(serve priest (some congregation (that (perform worship))))", structure.toString());
    }

    @Test
    public void testToIndentedString() {
        assertEquals("(serve priest \n" + "\t(some congregation \n" + "\t\t(that \n" + "\t\t\t(perform worship))))", structure.toIndentedString());
    }

    @Test
    public void testAbstraction() {
        Structure abstracted = StructureAbstractor.generateAbstraction(structure);
        assertEquals("(serve 0 (some 1 (that (perform 2))))", abstracted.toString());
    }
}
