package atlas;

import java.util.*;

public class CandidateInferenceGenerator {

    private final KnowledgeBase kb;

    public CandidateInferenceGenerator(KnowledgeBase kb) {
        this.kb = kb;
    }

    public List<Structure> generateCandidateInference(Map<String, String> compositeMapping, String S, String T){
        List<Structure> sourceStructures = kb.getStructuresForTopic(S);
        List<Structure> targetStructures = kb.getStructuresForTopic(T);

        List<Structure> inferences = new ArrayList<>();
        Set<String> seenStructures = new HashSet<>();

        List<Structure> mappedSourceStructures = getMappedSourceStructures(compositeMapping, sourceStructures, targetStructures);

        for(Structure s : sourceStructures){
            if (mappedSourceStructures.contains(s)){
                continue;
            }

            List<Symbol> arguments = extractNonPredicateSymbols(s);

            if (allCovered(arguments, compositeMapping)) {
                Structure translated = translate(s, compositeMapping);
                if(seenStructures.add(translated.toString())){
                    inferences.add(translated);
                }
            }
        }

        return inferences;
    }

    private List<Structure> getMappedSourceStructures(Map<String, String> compositeMapping,
                                                      List<Structure> sourceStructures,
                                                      List<Structure> targetStructures){
        Set<String> targetHashes = new HashSet<>();
        for (Structure s : targetStructures) {
            targetHashes.add(StructureAbstractor.getAbstractionHash(s));
        }

        List<Structure> mappedSourceStructures = new ArrayList<>();
        for(Structure s : sourceStructures){
            if(targetHashes.contains(StructureAbstractor.getAbstractionHash(s))){
                mappedSourceStructures.add(s);
            }
        }

        return mappedSourceStructures;
    }

    private List<Symbol> extractNonPredicateSymbols(Structure s) {
        List<Symbol> symbols = new ArrayList<>();

        for (Element element : s.getElements()) {
            if (element instanceof Structure) {
                symbols.addAll(extractNonPredicateSymbols((Structure) element));
            } else if (element instanceof Symbol && !(element instanceof Predicate)) {
                symbols.add((Symbol) element);
            }
        }
        
        return symbols;
    }

    private boolean allCovered(List<Symbol> arguments, Map<String, String> compositeMapping){
        for(Symbol s : arguments){
            if (!compositeMapping.containsKey(s.toString())){
                return false;
            }
        }
        return true;
    }

    private Structure translate(Structure s, Map<String, String> compositeMapping){
        Structure translated = new Structure();

        for(Element element : s.getElements()){
            if(element instanceof Predicate){
                translated.addElement((Predicate) element);
            } else if(element instanceof Symbol){
                translated.addElement(new Symbol(compositeMapping.get(element.toString())));
            } else if(element instanceof Structure){
                translated.addElement(translate((Structure) element, compositeMapping));
            }
        }

        return translated;
    }
}
