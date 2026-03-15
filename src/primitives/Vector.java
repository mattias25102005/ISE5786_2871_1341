package primitives;

/**
 * Class representing a vector in 3D Cartesian coordinates.
 * A vector cannot be the zero vector.
 */
public class Vector extends Point {

    /**
     * Constructs a vector from three coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @throws IllegalArgumentException if the vector is zero
     */
    public Vector(final double x, final double y, final double z) {
        super(x, y, z);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
    }

    /**
     * Constructs a vector from a Double3 object.
     *
     * @param xyz coordinates container
     * @throws IllegalArgumentException if the vector is zero
     */
    public Vector(final Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other other vector
     * @return new vector representing the sum
     */
    public Vector add(final Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    /**
     * Scales this vector by a scalar.
     *
     * @param scalar scalar value
     * @return new scaled vector
     */
    public Vector scale(final double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /**
     * Calculates dot product with another vector.
     *
     * @param other other vector
     * @return dot product
     */
    public double dotProduct(final Vector other) {
        return alignZero(
                this.xyz._d1() * other.xyz._d1() +
                        this.xyz._d2() * other.xyz._d2() +
                        this.xyz._d3() * other.xyz._d3()
        );
    }

    /**
     * Calculates cross product with another vector.
     *
     * @param other other vector
     * @return cross product vector
     */
    public Vector crossProduct(final Vector other) {
        final double x = this.xyz._d2() * other.xyz._d3() - this.xyz._d3() * other.xyz._d2();
        final double y = this.xyz._d3() * other.xyz._d1() - this.xyz._d1() * other.xyz._d3();
        final double z = this.xyz._d1() * other.xyz._d2() - this.xyz._d2() * other.xyz._d1();
        return new Vector(x, y, z);
    }

    /**
     * Calculates squared length of the vector.
     *
     * @return squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates length of the vector.
     *
     * @return vector length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Calculates squared distance between this vector and another vector.
     *
     * @param other other vector
     * @return squared distance
     */
    @Override
    public double distanceSquared(final Point other) {
        return super.distanceSquared(other);
    }

    /**
     * Calculates distance between this vector and another vector.
     *
     * @param other other vector
     * @return distance
     */
    @Override
    public double distance(final Point other) {
        return super.distance(other);
    }

    /**
     * Returns the normalized vector.
     *
     * @return new normalized vector
     */
    public Vector normalize() {
        final double len = length();
        return new Vector(
                alignZero(this.xyz._d1() / len),
                alignZero(this.xyz._d2()/ len),
                alignZero(this.xyz._d3() / len)
        );
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Vector" + xyz;
    }

    /**
     * Shortcut to Util.alignZero.
     *
     * @param number value to align
     * @return aligned value
     */
    private static double alignZero(final double number) {
        return Util.alignZero(number);
    }
}