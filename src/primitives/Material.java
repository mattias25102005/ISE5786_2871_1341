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

    /**
     * Définit le coefficient d'atténuation ambiante (Double3).
     * @param kA coefficient ambiant (Double3)
     * @return this pour chaînage
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Définit le coefficient d'atténuation ambiante (valeur scalaire).
     * @param kA coefficient ambiant (double)
     * @return this pour chaînage
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    // --- Configuration Diffuse (kD) ---
    /**
     * Définit le coefficient de réflexion diffuse (Double3).
     * @param kD coefficient diffuse (Double3)
     * @return this pour chaînage
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Définit le coefficient de réflexion diffuse (valeur scalaire).
     * @param kD coefficient diffuse (double)
     * @return this pour chaînage
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    // --- Configuration Spéculaire (kS) ---
    /**
     * Définit le coefficient de réflexion spéculaire (Double3).
     * @param kS coefficient spéculaire (Double3)
     * @return this pour chaînage
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Définit le coefficient de réflexion spéculaire (valeur scalaire).
     * @param kS coefficient spéculaire (double)
     * @return this pour chaînage
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    // --- Configuration Brillance (nShininess) ---
    /**
     * Définit l'exposant de brillance (shininess) utilisé pour le modèle spéculaire.
     * @param nShininess exposant de brillance
     * @return this pour chaînage
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}