package atlas;

import java.util.List;
import java.util.Map;

public class KnowledgeBase {
    // TODO : User Story 4.1

    public KnowledgeBase() {}

    /**
     * Load all structures from a text file (one structure string per line).
     * Each line is parsed via StructureParser.parse(), abstracted via
     * StructureAbstractor.generateAbstraction() to produce a hash key,
     * and indexed by both hash and topic.
     */
    public void load(String filename) throws Exception {}

    /**
     * Add a single structure to the KB, indexing it under its
     * abstract hash (from StructureAbstractor) and under the given topic.
     * A topic is identified by a *-prefixed Symbol, e.g. *priest.
     */
    public void addStructure(String topic, Structure structure) {}

    /**
     * Return all structures indexed under a given abstract hash string.
     * The hash is the toString() of StructureAbstractor.generateAbstraction().
     */
    public List<Structure> getStructuresByHash(String hash) { return null; }

    /**
     * Return the full hash-to-structures index.
     * Used by AnalogyRetriever to find candidate sources.
     */
    public Map<String, List<Structure>> getHashIndex() { return null; }

    /**
     * Return all topic names (concepts) stored in the KB.
     * Topics are the bare names without the * prefix.
     */
    public List<String> getTopics() { return null; }

    // TODO : User Story 4.2

    public List<Structure> getStructuresForTopic(String topic) { return null; }
}
