package atlas;

import java.util.HashMap;
import java.util.Map;

public class StructureAbstractor {
    public static Structure generateAbstraction(Structure structure) {
        Map<String, Integer> map = new HashMap<>();
        int[] counter = {0};
        return abstractStructure(structure, map, counter);
    }

    private static Structure abstractStructure(Structure structure, Map<String, Integer> map, int[] counter) {

        Structure abstraction = new Structure();

        for(Element element : structure.getElements()) {
            if(element instanceof atlas.Predicate) {
                abstraction.addElement(new Predicate(((Predicate) element).getValue()));
            }

            else if(element instanceof atlas.Symbol) {
                String value =  ((Symbol) element).getValue();

                if(!map.containsKey(value)) {
                    map.put(value, counter[0]++);
                }

                int number = map.get(value);
                abstraction.addElement(new Symbol(String.valueOf(number)));
            }

            else if(element instanceof atlas.Structure) {
                Structure child =  (Structure) element;
                Structure abstractChild = abstractStructure(child, map, counter);
                abstraction.addElement(abstractChild);
            }
        }


        return abstraction;
    }
}
