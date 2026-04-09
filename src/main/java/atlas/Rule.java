package atlas;

public class Rule {

    private final String originalPredicate;
    private final boolean negated;
    private final boolean switchArgs;
    private final boolean newAgent;
    private final String newVerb;
    private final String preposition;
    private final String colonElement;
    private final boolean colonElementPushed;
    private final String gerund;

    public Rule(String originalPredicate, boolean negated, boolean switchArgs,
                boolean newAgent, String newVerb, String preposition, String colonElement,
                boolean colonElementPushed, String gerund){
        this.originalPredicate = originalPredicate;
        this.negated = negated;
        this.switchArgs = switchArgs;
        this.newAgent = newAgent;
        this.newVerb = newVerb;
        this.preposition = preposition;
        this.colonElement = colonElement;
        this.colonElementPushed = colonElementPushed;
        this.gerund = gerund;
    }

    public String getOriginalPredicate() { return originalPredicate; }
    public boolean isNegated() { return negated; }
    public boolean isSwitchArgs() { return switchArgs; }
    public boolean isNewAgent() { return newAgent; }
    public String getNewVerb() { return newVerb; }
    public String getPreposition() { return preposition; }
    public String getColonElement() { return colonElement; }
    public boolean isColonElementPushed() { return colonElementPushed; }
    public String getGerund() { return gerund; }

    @Override
    public String toString(){
        return "Rule{" +
                "original='" + originalPredicate + '\'' +
                ", negated=" + negated +
                ", switchArgs=" + switchArgs +
                ", newAgent=" + newAgent +
                ", newVerb='" + newVerb + '\'' +
                ", preposition='" + preposition + '\'' +
                ", colonElement='" + colonElement + '\'' +
                ", colonElementPushed=" + colonElementPushed +
                ", gerund='" + gerund + '\'' +
                '}';

    }
}
