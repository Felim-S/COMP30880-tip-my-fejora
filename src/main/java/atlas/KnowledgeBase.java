package atlas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Logger;

public class KnowledgeBase {

    private static final Logger logger = AtlasLogger.getLogger(KnowledgeBase.class);

    private final StructureRewriter rewriter;

    private final Map<String, List<Structure>> hashIndex;
    private final Map<String, List<Structure>> topicIndex;

    public KnowledgeBase(StructureRewriter rewriter) {
        this.rewriter = rewriter;
        this.hashIndex = new HashMap<>();
        this.topicIndex = new HashMap<>();
    }

    /**
     * Load all structures from a text file (one structure string per line).
     * Each line is parsed via StructureParser.parse(), abstracted via
     * StructureAbstractor.generateAbstraction() to produce a hash key,
     * and indexed by both hash and topic.
     */

    public void loadStructure(String filename) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Format: TOPIC | structure | abstraction | hash | structure | abstraction | hash | ...
                // Column 0 is the topic name; concrete structures are at indices 1, 4, 7, ...
                String[] columns = line.split("\t");
                for (int i = 1; i < columns.length; i += 3) {
                    String structureString = columns[i].trim();
                    if (structureString.isEmpty()) continue;

                    try {
                        Structure structure = StructureParser.parse(structureString);
                        addStructure(structure);
                    } catch (Exception e) {
                        logger.warning("Failed to parse structure: " + structureString + ": " + e.getMessage());
                    }
                }
            }
        }
    }


    /**
     * Add a single structure to the KB, indexing it under its
     * abstract hash (from StructureAbstractor) and under the given topic.
     * A topic is identified by a *-prefixed Symbol, e.g. *priest.
     */

    public void addStructure(Structure structure) {

        // index by topic
        indexByTopic(structure, structure);

        // index by hash
        String hash = StructureAbstractor
                .generateAbstraction(structure)
                .toString()
                .intern();

        List<Structure> hashList = hashIndex.computeIfAbsent(hash, k -> new ArrayList<>());
        if(!hashList.contains(structure)) hashList.add(structure);
    }

    // recursive method for indexing structures by topic
    private void indexByTopic(Structure root, Structure current) {
        for (Element element : current.getElements()) {

            if (element instanceof Symbol symbol) {
                String value = (symbol.getValue());

                if (value.startsWith("*")) {
                    String topicName = value.substring(1).intern();

                    List<Structure> topicList = topicIndex.computeIfAbsent(topicName, k -> new ArrayList<>());
                    if (!topicList.contains(root)) topicList.add(root);
                }

            } else if (element instanceof Structure s) {
                indexByTopic(root, s);
            }
        }
    }

    /**
     * Return all structures indexed under a given abstract hash string.
     * The hash is the toString() of StructureAbstractor.generateAbstraction().
     */

    public List<Structure> getStructuresByHash(String hash) {
        hash = hash.intern();
        List<Structure> list = hashIndex.get(hash);
        return list != null ? list : new ArrayList<>();
    }

    /**
     * Return the full hash-to-structures index.
     * Used by AnalogyRetriever to find candidate sources.
     */
    public Map<String, List<Structure>> getHashIndex() {
        return hashIndex;
    }

    /**
     * Return all topic names (concepts) stored in the KB.
     * Topics are the bare names without the * prefix.
     */

    public List<String> getTopics() {
        return new ArrayList<>(topicIndex.keySet());
    }

    // user story 4.2
    public List<Structure> getStructuresForTopic(String topic) {
        topic = topic.intern();
        List<Structure> list = topicIndex.get(topic);
        return list != null ? list : new ArrayList<>();
    }
}
