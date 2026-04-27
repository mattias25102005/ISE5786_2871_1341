package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTests {
    private final double DELTA = 0.000001;

    @Test
    void testSubtract() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);
        // TC01: Simple subtract
        assertEquals(new Vector(1, 1, 1), p2.subtract(p1), "Subtract() wrong result");
    }

    @Test
    void testDistance() {
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(0, 3, 0);
        // TC01: Simple distance
        assertEquals(3, p1.distance(p2), DELTA, "distance() wrong result");
    }
    @Test
    void testAdd() {
        Point p1 = new Point(1, 2, 3);
        // TC01: Test simple d'addition Point + Vector
        assertEquals(new Point(2, 4, 6), p1.add(new Vector(1, 2, 3)), "Point.add() ne fonctionne pas correctement");
    }

    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 5);
        // TC01: La distance au carré entre (1,2,3) et (1,2,5) est (2^2) = 4
        assertEquals(4, p1.distanceSquared(p2), 0.000001, "distanceSquared() ne donne pas le bon résultat");
    }

}