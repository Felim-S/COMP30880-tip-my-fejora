package atlas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureRewriter {

    private final Map<String, List<Rule>> rules;

    public StructureRewriter(Map<String, List<Rule>> rules) {
        this.rules = rules;
    }

    public List<Structure> rewrite(Structure structure) {
        List<Structure> structures = new ArrayList<>();

        structures.add(structure);

        for (Element element : structure.getElements()) {
            if (!element.isStructure()){
                List<Rule> elementRules = rules.get(element.toString());
                if (elementRules != null){
                    for (Rule rule : elementRules){
                        Structure newStructure = applyRule((Symbol) element, rule);
                        if (newStructure != null){
                            structures.add(newStructure);
                        }
                    }
                }
            } else{
                return rewrite((Structure) element);
            }
        }

        return structures;
    }

    private Structure applyRule(Symbol structure, Rule rule) {
        return null;
    }
}
