package atlas;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AnalogyRankerTest {
    @Test
    public void testRichnessBasic() {
        Structure s = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        assertEquals(5.326, AnalogyRanker.richness(s), 0.01);
    }

    @Test
    public void testRichnessFlat() {
        Structure s = StructureParser.parse("(work_in scientist lab)");
        // count at depth 0: 3 (work_in, scientist, lab)
        // sum = 3, log10(3) = 0.477
        assertEquals(0.477, AnalogyRanker.richness(s), 0.01);
    }

    @Test
    public void testQualityBasic() {
        // One alignable pair — quality should be r(σ)^3
        Structure scientist = StructureParser.parse(
                "(by working (perform scientist (some work (for lab (that (conduct experiment))))))");
        Structure priest = StructureParser.parse(
                "(by serving (perform priest (some service (for congregation (that (conduct worship))))))");

        List<Structure[]> alignable = new ArrayList<>();
        alignable.add(new Structure[]{scientist, priest});

        double r = AnalogyRanker.richness(priest);
        double expected = Math.pow(r, 3);
        double q = AnalogyRanker.calculateQuality(alignable, 3);
        assertEquals(expected, q, 0.001);
    }

    @Test
    public void testQualityEmptyAlignableList() {
        // No alignable structures — quality should be 0
        List<Structure[]> alignable = new ArrayList<>();
        double q = AnalogyRanker.calculateQuality(alignable, 3);
        assertEquals(0.0, q, 0.001);
    }
}
