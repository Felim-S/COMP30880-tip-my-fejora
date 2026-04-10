package atlas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StructureRewriter {

    private static final Logger logger = AtlasLogger.getLogger(StructureRewriter.class);

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
                        Structure newStructure = applyRule(structure, rule);
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

    private Structure applyRule(Structure structure, Rule rule) {
        List<Element> elements = structure.getElements();

        if (elements.size() < 3){
            logger.warning("Cannot apply rule: [" + rule.toString() + "] structure has less than 3 elements");
            return structure;
        }

        Element arg1 =  elements.get(1);
        Element arg2 = elements.get(2);

        // < rule : swap the two arguments
        if(rule.isSwitchArgs()){
            Element temp = arg1;
            arg1 = arg2;
            arg2 = temp;
        }

        // inner structure automatically handles ^ and : rules
        Structure updatedStructure = buildInner(rule, arg1, arg2);

        // ! rule : wrap in a (not ...)
        if(rule.isNegated()){
            updatedStructure = applyNegation(updatedStructure);
        }
        // gerund : wrap everything with the gerund argument (by doing ...)
        if(rule.getGerund() != null){
            updatedStructure = applyGerund(updatedStructure, rule.getGerund());
        }

        return updatedStructure;
    }

    private Structure buildInner(Rule rule, Element arg1, Element arg2) {
        if (rule.isNewAgent())            return buildNewAgentInner(rule, arg1, arg2);
        if (rule.getColonElement() != null) return buildColonInner(rule, arg1, arg2);
        return buildPlainInner(rule, arg1, arg2);
    }

    private Structure buildPlainInner(Rule rule, Element arg1, Element arg2) {
        Structure s = new Structure();
        s.addElement(new Predicate(rule.getNewVerb()));
        s.addElement(arg1);
        s.addElement(arg2);
        return s;
    }

    private Structure buildNewAgentInner(Rule rule, Element arg1, Element arg2) {
        Structure s = new Structure();
        s.addElement(new Predicate(rule.getNewVerb()));
        s.addElement(new Symbol(rule.getColonElement()));
        s.addElement(arg1);
        if(rule.getPreposition() != null){
            s.addElement(buildPreposition(rule.getPreposition(), arg2));
        }
        return s;
    }

    private Structure buildColonInner(Rule rule, Element arg1, Element arg2) {
        Structure s = new Structure();
        s.addElement(new Predicate(rule.getNewVerb()));
        s.addElement(arg1);
        if (rule.isColonElementPushed()) {
            s.addElement(new Symbol(rule.getColonElement()));
            if (rule.getPreposition() != null)
                s.addElement(buildPreposition(rule.getPreposition(), arg2));
        } else {
            s.addElement(arg2);
            if (rule.getPreposition() != null)
                s.addElement(buildPreposition(rule.getPreposition(), new Symbol(rule.getColonElement())));
        }
        return s;
    }

    private Structure buildPreposition(String preposition, Element arg) {
        Structure s = new Structure();
        s.addElement(new Predicate(preposition));
        s.addElement(arg);
        return s;
    }

    private Structure applyNegation(Structure structure){
        Structure s = new Structure();
        s.addElement(new Predicate("not"));
        s.addElement(structure);
        return s;
    }

    private Structure applyGerund(Structure structure, String gerund){
        Structure s = new Structure();
        s.addElement(new Predicate("by"));
        s.addElement(new Symbol(gerund));
        s.addElement(structure);
        return s;
    }
}
