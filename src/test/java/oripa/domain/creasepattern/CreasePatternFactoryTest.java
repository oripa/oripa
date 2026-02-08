package oripa.domain.creasepattern;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import oripa.test.util.AssertionUtil;
import oripa.value.OriLine;

public class CreasePatternFactoryTest {

    @Test
    public void testCreateSquare() {
        var factory = new CreasePatternFactory();
        double size = 100;

        var cp = factory.createSquareCreasePattern(size);
        assertEquals(size, cp.getPaperWidth());
        assertEquals(size, cp.getPaperHeight());
    }

    @Test
    public void testCreateRectangle() {
        var factory = new CreasePatternFactory();
        double size = 100;

        var cp = factory.createCreasePattern(size, size * 2);
        assertEquals(size, cp.getPaperWidth());
        assertEquals(size * 2, cp.getPaperHeight());
    }

    @Test
    public void testCreate_lines() {
        var factory = new CreasePatternFactory();
        double size = 100;

        OriLine cut0 = new OriLine(0, 0, 0, size, OriLine.Type.CUT);
        OriLine cut1 = new OriLine(0, size, size, size, OriLine.Type.CUT);
        OriLine cut2 = new OriLine(size, size, 0, 0, OriLine.Type.CUT);

        var lines = List.of(cut0, cut1, cut2);

        var cp = factory.createCreasePattern(lines);

        assertEquals(3, cp.size());
        assertEquals(size, cp.getPaperWidth());
        assertEquals(size, cp.getPaperHeight());

        AssertionUtil.assertAnyMatch(cut0, cp, (a, b) -> a.equals(b));
        AssertionUtil.assertAnyMatch(cut1, cp, (a, b) -> a.equals(b));
        AssertionUtil.assertAnyMatch(cut2, cp, (a, b) -> a.equals(b));
    }
}
