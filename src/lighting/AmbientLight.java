package lighting;

import primitives.Color;

/**
 * Classe représentant la lumière ambiante (Immuable)
 */
/**
 * Immutable ambient light used by the scene to provide a base illumination.
 * Ambient light contributes a uniform color/intensity to all surfaces.
 */
public class AmbientLight {
    /** The ambient intensity (color) provided to the scene. Immutable. */
    private final Color _intensity;

    /** Constant representing no ambient light (black/zero intensity). */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Create a new AmbientLight with the specified intensity color.
     * @param intensity ambient color/intensity
     */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Return the ambient intensity color.
     * @return ambient color/intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}