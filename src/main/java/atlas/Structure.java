package atlas;

import java.util.ArrayList;
import java.util.List;

public class Structure extends Element {

    private final List<Element> elements = new ArrayList<>();

    private String abstractionHash = null;

    public String getCachedAbstractionHash() { return abstractionHash; }
    public void setCachedAbstractionHash(String hash) { this.abstractionHash = hash; }

    @Override
    public boolean isStructure() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).toString());

            if (i < elements.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String toIndentedString() {
        return toIndentedString(0);
    }

    private String toIndentedString(int indent) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }

        sb.append("(");

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element instanceof Structure) {
                sb.append("\n");
                sb.append(((Structure) element).toIndentedString(indent + 1));
            } else {
                sb.append(element.toString());
            }

            if (i < elements.size() - 1) {
                sb.append(" ");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    public List<Element> getElements() {
        return elements;
    }
    public void addElement(Element element) {
        elements.add(element);
    }
    public Predicate getHead(){ return (Predicate) elements.getFirst(); }
}
