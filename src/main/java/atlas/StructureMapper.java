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
