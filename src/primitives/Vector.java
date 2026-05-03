package primitives;

/**
 * Class representing a vector in 3D Cartesian coordinates.
 * A vector cannot be the zero vector.
 */
public class Vector extends Point {

    /** Unit vector along world X axis. */
    public static final Vector AXIS_X = new Vector(1, 0, 0);

    /** Unit vector along world Y axis. */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);

    /** Unit vector along world Z axis. */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

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
     * Component-wise addition of two vectors.
     * @param other vector to add
     * @return new Vector equal to this + other
     */
    public Vector add(final Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    /**
     * Scale the vector by a scalar (multiply each component).
     * @param scalar scale factor
     * @return new scaled Vector
     */
    public Vector scale(final double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /**
     * Dot product (scalar product) between this vector and another.
     * Result is aligned to zero to avoid tiny floating-point noise.
     * @param other other vector
     * @return scalar dot product
     */
    public double dotProduct(final Vector other) {
        return alignZero(
                this.xyz._d1() * other.xyz._d1() +
                        this.xyz._d2() * other.xyz._d2() +
                        this.xyz._d3() * other.xyz._d3()
        );
    }

    /**
     * Cross product producing a vector orthogonal to both operands.
     * @param other other vector
     * @return new Vector equal to this × other
     */
    public Vector crossProduct(final Vector other) {
        final double x = this.xyz._d2() * other.xyz._d3() - this.xyz._d3() * other.xyz._d2();
        final double y = this.xyz._d3() * other.xyz._d1() - this.xyz._d1() * other.xyz._d3();
        final double z = this.xyz._d1() * other.xyz._d2() - this.xyz._d2() * other.xyz._d1();
        return new Vector(x, y, z);
    }

    /**
     * Squared length (magnitude) of the vector. Uses dot product for efficiency.
     * @return squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Euclidean length (magnitude) of the vector.
     * @return length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }



    /**
     * Return a normalized (unit length) vector in the same direction.
     * Components are aligned to zero to avoid tiny rounding errors.
     * @return normalized vector
     */
    public Vector normalize() {
        final double len = length();
        return new Vector(
                alignZero(this.xyz._d1() / len),
                alignZero(this.xyz._d2() / len),
                alignZero(this.xyz._d3() / len)
        );
    }

    /**
     * Compare vectors for value equality using Point's Double3 equality.
     * @param obj other object
     * @return true if equal
     */
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    /**
     * String representation for debugging.
     * @return textual representation of the vector
     */
    @Override
    public String toString() {
        return "Vector" + xyz;
    }

    /**
     * Align a number to zero using the utility function (avoid tiny values).
     * @param number the value to align
     * @return 0.0 if the value is very close to zero, otherwise the original value
     */
    private static double alignZero(final double number) {
        return Util.alignZero(number);
    }

    /**
     * Subtract another vector from this vector component-wise.
     * @param other vector to subtract
     * @return new Vector equal to this - other
     */
    public Vector subtract(final Vector other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }
}
