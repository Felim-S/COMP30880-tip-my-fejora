package atlas;

import org.junit.Test;

import java.util.List;

import static atlas.StructureAbstractor.generateAbstraction;
import static org.junit.Assert.*;

public class StructureAbstractorTest {

    //helper to parse and abstract in 1 step
    private Structure parseAndAbstract(String input){
        Structure parsed = StructureParser.parse(input);
        return StructureAbstractor.generateAbstraction(parsed);
    }

    @Test
    public void testSymbolsReplacedWithNumbers(){
        Structure result = parseAndAbstract("(work scientist laboratory)");

        List<Element> elements = result.getElements();
        assertEquals("work", elements.get(0).toString()); //should be unchanged because predicate
        assertEquals("0", elements.get(1).toString());
        assertEquals("1", elements.get(2).toString());
        //scientist and laboratory should both be replaced with respective numbers
    }

    @Test
    public void testSymbolsReplacedWithNumbers2(){
        Structure result = parseAndAbstract("(serve priest (some congregation (that (perform worship))))");

        List<Element> elements = result.getElements();
        assertEquals("serve", elements.get(0).toString());
        assertEquals("0", elements.get(1).toString());

        Structure someStructure = (Structure) elements.get(2);
        List<Element> someElements = someStructure.getElements();
        assertEquals("some",  someElements.get(0).toString());
        assertEquals("1",   someElements.get(1).toString());

        Structure thatStructure = (Structure) someElements.get(2);
        List<Element> thatElements = thatStructure.getElements();
        assertEquals("that", thatElements.get(0).toString());

        Structure performStructure = (Structure) thatElements.get(1);
        List<Element> performElements = performStructure.getElements();
        assertEquals("perform", performElements.get(0).toString());
        assertEquals("2",   performElements.get(1).toString());

    }

    @Test
    public void testAbstractionString(){
        Structure result = parseAndAbstract("(serve priest (some congregation (that (perform worship))))");

        assertEquals("(serve 0 (some 1 (that (perform 2))))", result.toString());
    }

    @Test
    public void testAbstractionString2(){
        Structure result = parseAndAbstract("(serve priest congregation perform)");

        assertEquals("(serve 0 1 2)", result.toString());
    }

}
