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


                boolean hasAsterisk = value.startsWith("*");
                //temporarily remove asterisk
                String stripped = hasAsterisk ? value.substring(1) : value;

                if(!map.containsKey(stripped)) {
                    map.put(stripped, counter[0]++);
                }


                String number = String.valueOf(map.get(stripped));
                //if originally had asterisk add it back
                String result = hasAsterisk ? "*" + number : number;
                abstraction.addElement(new Symbol(result));
            }
        }
        return abstraction;
    }
}
