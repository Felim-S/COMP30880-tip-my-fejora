package atlas;

public class Symbol extends Element {

    private final String value;

    public Symbol(String value) {
        this.value = value;
    }

    @Override
    public boolean isStructure() { return false; }
    public String getValue() {
        return value;
    }
    @Override
    public String toString() { return value; }
}
