package primitives;

/**
 * Class representing a point in 3D Cartesian coordinates.
 */
public class Point {

    /**
     * Coordinate data of the point.
     */
    protected final Double3 xyz;

    /**
     * The origin point (0,0,0).
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point from three coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point(final double x, final double y, final double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from a Double3 object.
     *
     * @param xyz coordinates container
     */
    public Point(final Double3 xyz) {
        this.xyz = xyz;
    }

    /**
     * Subtracts another point from this point.
     *
     * @param other other point
     * @return vector from other point to this point
     */
    public Vector subtract(final Point other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }

    /**
     * Adds a vector to this point.
     *
     * @param vector vector to add
     * @return new point resulting from the addition
     */
    public Point add(final Vector vector) {
        return new Point(this.xyz.add(vector.xyz));
    }

    /**
     * Calculates squared distance between this point and another point.
     *
     * @param other other point
     * @return squared distance
     */
    public double distanceSquared(final Point other) {
        final double dx = this.xyz._d1()- other.xyz._d1();
        final double dy = this.xyz._d2() - other.xyz._d2();
        final double dz = this.xyz._d3()- other.xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates distance between this point and another point.
     *
     * @param other other point
     * @return distance
     */
    public double distance(final Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point other)) return false;
        return xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return "Point" + xyz;
    }
}