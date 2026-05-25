package land.pandaland.ui.layout;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PandaRectTest {
    @Test
    public void containsIncludesLeftAndTopEdges() {
        PandaRect rect = new PandaRect(10, 20, 100, 50);

        assertTrue(rect.contains(10, 20));
        assertTrue(rect.contains(109, 69));
        assertFalse(rect.contains(110, 70));
        assertFalse(rect.contains(9, 20));
    }

    @Test
    public void insetShrinksBounds() {
        PandaRect rect = new PandaRect(10, 20, 100, 50).inset(5);

        assertTrue(rect.contains(15, 25));
        assertFalse(rect.contains(10, 20));
    }

    @Test
    public void valueEqualityUsesAllFields() {
        PandaRect first = new PandaRect(10, 20, 100, 50);
        PandaRect second = new PandaRect(10, 20, 100, 50);
        PandaRect different = new PandaRect(10, 20, 100, 51);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertEquals("PandaRect{x=10, y=20, width=100, height=50}", first.toString());
    }

    @Test
    public void containsUsesLongArithmeticForRightAndBottomEdges() {
        PandaRect rect = new PandaRect(Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 2, 3, 3);

        assertTrue(rect.contains(Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 1));
        assertTrue(rect.contains(Integer.MAX_VALUE, Integer.MAX_VALUE));
        assertFalse(rect.contains(Integer.MAX_VALUE - 3, Integer.MAX_VALUE - 2));
    }

    @Test
    public void insetClampsOverflowingCoordinatesAndDimensions() {
        PandaRect rect = new PandaRect(Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2, 5, 4).inset(10);

        assertEquals(Integer.MAX_VALUE, rect.x);
        assertEquals(Integer.MAX_VALUE, rect.y);
        assertEquals(0, rect.width);
        assertEquals(0, rect.height);
    }
}
