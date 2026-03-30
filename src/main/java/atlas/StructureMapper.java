package atlas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureMapper {

    public static Boolean isMappable(Structure A, Structure B){
        Map<String, String> map = new HashMap<>();
        return checkMapping(A, B, map);
    }

    private static boolean checkMapping(Structure A, Structure B, Map<String, String> map) {
        List<Element> elementsA = A.getElements();
        List<Element> elementsB = B.getElements();

        if(elementsA.size() != elementsB.size()){
            return false;
        }

        for(int i = 0; i < elementsA.size(); i++){
            Element elementA = elementsA.get(i);
            Element elementB = elementsB.get(i);

            if(elementA instanceof Structure && elementB instanceof Structure){
                if(!checkMapping((Structure) elementA, (Structure) elementB, map)){
                    return false;
                }
                continue;
            }

            //if one is a structure and other one isnt then shapes dont match
            if(elementA instanceof Structure || elementB instanceof Structure){
                return false;
            }

            //predicates must match exactly
            if (elementA instanceof Predicate && elementB instanceof Predicate){
                if(!elementA.toString().equals(elementB.toString())){
                    return false;
                }
                continue;
            }

            if(elementA instanceof Predicate || elementB instanceof Predicate){
                return false;
            }

            //both are symbols, check asterisk rule and 1-1 consistency
            String valA =  elementA.toString();
            String valB = elementB.toString();

            if(valA.startsWith("*") != valB.startsWith("*")){
                return false;
            }

            if(map.containsKey(valA)){
                if(!map.get(valA).equals(valB)){
                    return false;
                }
            } else {
                if(map.containsValue(valB)){
                    return false;
                }
                map.put(valA, valB);
            }
        }
        return true;
    }

    public static Map<String, String> generateMapping(Structure A, Structure B){
        if(!isMappable(A,B)){ return null; }

        Map<String, String> map = new HashMap<>();

        List<Element> elementsA = A.getElements();
        List<Element> elementsB = B.getElements();

        for(int i = 0; i < elementsA.size(); i++){
            Element a = elementsA.get(i);
            Element b = elementsB.get(i);

            if(a.isStructure() && b.isStructure()){
                Map<String, String> subMap = generateMapping((Structure) a, (Structure) b);
                if(subMap == null){
                    return  null;
                }
                map.putAll(subMap);
            } else if(!a.isStructure() && !b.isStructure()){
                if(!(a instanceof Predicate) && !(b instanceof Predicate)){
                    boolean aIsTopic = a.toString().startsWith("*");
                    boolean bIsTopic = b.toString().startsWith("*");
                    if(aIsTopic != bIsTopic){
                        return null;
                    }
                    map.put(a.toString(), b.toString());
                }
            }
        }

        return map;
    }
}
