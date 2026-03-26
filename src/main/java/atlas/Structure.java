package atlas;

import java.util.ArrayList;
import java.util.List;

public class Structure extends Element {

    private final List<Element> elements = new ArrayList<>();

    @Override
    public boolean isStructure() { return true; }
    @Override
    public String toString() {
        // TODO - User Story #2
        return "";
    }
    public List<Element> getElements() {
        return elements;
    }
    public void addElement(Element element) {
        elements.add(element);
    }
    public Predicate getHead(){ return (Predicate) elements.getFirst(); }
}
