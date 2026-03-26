package atlas;

import java.util.ArrayList;
import java.util.List;

public class Structure implements Element {

    private final List<Element> elements = new ArrayList<>();

    @Override
    public boolean isStructure() { return true; }
    public List<Element> getElements() {
        return elements;
    }
    public void addElement(Element element) {
        elements.add(element);
    }
}
