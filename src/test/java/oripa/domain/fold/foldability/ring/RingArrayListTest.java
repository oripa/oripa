package oripa.domain.fold.foldability.ring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class RingArrayListTest {

    @Test
    void test_1Element() {
        var ring = new RingArrayList<>(List.of(1));

        assertEquals(1, ring.size());

        assertElement(ring, 0, 1, 1, 1);

        ring.dropConnection(0);
        assertEquals(0, ring.size());
    }

    @Test
    void test_2Elements() {
        var ring = new RingArrayList<>(List.of(1, 2));

        assertEquals(2, ring.size());

        assertElement(ring, 0, 2, 1, 2);
        assertElement(ring, 1, 1, 2, 1);

        assertEquals(1, ring.head());
        assertEquals(2, ring.tail());

        ring.dropConnection(0);
        assertEquals(1, ring.size());

        assertElement(ring, 1, 2, 2, 2);

        assertEquals(2, ring.head());
        assertEquals(2, ring.tail());
    }

    @Test
    void test_3Elements() {
        var ring = new RingArrayList<>(List.of(1, 2, 3));

        assertEquals(3, ring.size());

        assertElement(ring, 0, 3, 1, 2);

        assertElement(ring, 1, 1, 2, 3);

        assertElement(ring, 2, 2, 3, 1);

        ring.dropConnection(2);
        assertFalse(ring.exists(2));
        assertEquals(2, ring.size());

        assertElement(ring, 1, 1, 2, 1);

    }

    private <Data> void assertElement(final RingArrayList<Data> ring,
            final int index, final Data prevous, final Data target, final Data next) {
        assertEquals(prevous, ring.getPrevious(index).getValue());
        assertEquals(target, ring.getElement(index).getValue());
        assertEquals(next, ring.getNext(index).getValue());
    }

}
