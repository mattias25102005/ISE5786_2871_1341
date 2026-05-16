package primitives;

/**
 * Class representing the material of a geometry.
 */
public class Material {
    /** Diffuse reflection coefficient */
    public Double3 kA = Double3.ZERO; // Ou Double3.ONE selon ton choix de départ

    /**
     * Default constructor.
     */
    public Material() {}

    /**
     * Setter for kA using Double3.
     * @param kA coefficient
     * @return this material (Builder Pattern)
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for kA using double.
     * @param kA coefficient
     * @return this material (Builder Pattern)
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}