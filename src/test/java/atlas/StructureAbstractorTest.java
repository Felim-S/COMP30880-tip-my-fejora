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

}
