package atlas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureMapper {

    public static Boolean isMappable(Structure A, Structure B){
        String a = StructureAbstractor.generateAbstraction(A).toString();
        String b = StructureAbstractor.generateAbstraction(B).toString();
        return a.equals(b);
    }


    public static Map<String, String> generateMapping(Structure A, Structure B){
        if(!isMappable(A,B)){ throw new IllegalArgumentException("Structures are not mappable"); }

        Map<String, String> map = new HashMap<>();

        List<Element> elementsA = A.getElements();
        List<Element> elementsB = B.getElements();

        for(int i = 0; i < elementsA.size(); i++){
            Element a = elementsA.get(i);
            Element b = elementsB.get(i);

            if(a.isStructure() && b.isStructure()){
                Map<String, String> subMap = generateMapping((Structure) a, (Structure) b);
                map.putAll(subMap);
            } else if(!a.isStructure() && !b.isStructure()){
                if(!(a instanceof Predicate) && !(b instanceof Predicate)){
                    boolean aIsTopic = a.toString().startsWith("*");
                    boolean bIsTopic = b.toString().startsWith("*");

                    if(aIsTopic != bIsTopic){
                        throw new IllegalArgumentException("Asterisk mismatch in structure");
                    }

                    String keyA = a.toString();
                    String keyB = b.toString();

                    if(map.containsKey(keyA)){
                        if(!map.get(keyA).equals(keyB)){
                            throw new IllegalArgumentException("Mapping is not 1:1");
                        }
                    } else if(map.containsValue(keyB)){
                        throw new IllegalArgumentException("Mapping is not 1:1");
                    }

                    map.put(a.toString(), b.toString());
                }
            }
        }

        return map;
    }
}
