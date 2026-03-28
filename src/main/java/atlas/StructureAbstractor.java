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
            if(element instanceof Predicate) {
                abstraction.addElement(new Predicate(((Predicate) element).getValue()));
            } else if (element instanceof Structure) {
                Structure abstractChild = abstractStructure((Structure) element, map, counter);
                abstraction.addElement(abstractChild);
            } else if (element instanceof Symbol) {
                String value = ((Symbol) element).getValue();
                if(!map.containsKey(value)) {
                    map.put(value, counter[0]++);
                }
                abstraction.addElement(new Symbol(String.valueOf(map.get(value))));
            }
        }
        return abstraction;
    }
}
