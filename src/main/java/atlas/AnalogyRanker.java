package atlas;

import java.util.List;

public class AnalogyRanker {
    // TODO : User Story 4.4

    private static final int DEFAULT_BETA = 3;

    private KnowledgeBase kb;
    private AnalogyRetriever retriever;

    public AnalogyRanker(KnowledgeBase kb, AnalogyRetriever retriever) {}

    public static double richness(Structure structure) { return 0; }

    public double quality(String source, String target) { return 0; }

    public double quality(String source, String target, int beta) { return 0; }

    public List<String> rankSources(String target) { return null; }

    public List<String> rankSources(String target, int beta) { return null; }
}
