package primitives;

/**
 * Class representing a vector in 3D Cartesian coordinates.
 * A vector cannot be the zero vector.
 */
public class Vector extends Point {

    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
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

    public Vector add(final Vector other) {
        return new Vector(this.xyz.add(other.xyz));
    }

    public Vector scale(final double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    public double dotProduct(final Vector other) {
        return alignZero(
                this.xyz._d1() * other.xyz._d1() +
                        this.xyz._d2() * other.xyz._d2() +
                        this.xyz._d3() * other.xyz._d3()
        );
    }

    public Vector crossProduct(final Vector other) { // a verif
        final double x = this.xyz._d2() * other.xyz._d3() - this.xyz._d3() * other.xyz._d2();
        final double y = this.xyz._d3() * other.xyz._d1() - this.xyz._d1() * other.xyz._d3();
        final double z = this.xyz._d1() * other.xyz._d2() - this.xyz._d2() * other.xyz._d1();
        return new Vector(x, y, z);
    }

    public double lengthSquared() {
        return dotProduct(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }



    public Vector normalize() {
        final double len = length();
        return new Vector(
                alignZero(this.xyz._d1() / len),
                alignZero(this.xyz._d2() / len),
                alignZero(this.xyz._d3() / len)
        );
    }

    @Override
    public boolean equals(final Object obj) { // a faire
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Vector" + xyz;
    }

    private static double alignZero(final double number) {
        return Util.alignZero(number);
    }
    public Vector subtract(final Vector other) {
        return new Vector(this.xyz.subtract(other.xyz));
    }
}
