package primitives;

/**
 * Class representing the material of a geometry (Modèle de Phong).
 */
public class Material {
    /** Coefficient d'atténuation ambiante */
    public Double3 kA = Double3.ZERO;

    /** Coefficient de réflexion diffuse (Partie B - Point 5.א) */
    public Double3 kD = Double3.ZERO;

    /** Coefficient de réflexion spéculaire (Partie B - Point 5.א) */
    public Double3 kS = Double3.ZERO;

    /** Exposant de brillance/polissage (Partie B - Point 5.א) */
    public int nShininess = 0;

    /** Constructeur par défaut */
    public Material() {}

    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    // --- Configuration Diffuse (kD) ---
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    // --- Configuration Spéculaire (kS) ---
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    // --- Configuration Brillance (nShininess) ---
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}