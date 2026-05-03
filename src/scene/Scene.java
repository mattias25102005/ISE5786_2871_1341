
package scene;

import geometries.impl.Geometries;
import primitives.Color;
import lighting.AmbientLight;
import geometries.api.Geometry;

/**
 * Scene represents a collection of geometries, background and lighting settings.
 */
public class Scene {
    /** The scene name (identifier). */
    public String name;

    /** Background color used when no geometry is hit. */
    public Color background = Color.BLACK;

    /** Ambient light that illuminates the scene uniformly. */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /** Collection of geometries contained in the scene. */
    public Geometries geometries = new Geometries();

    /**
     * Create a new scene with the given name. Other properties may be set
     * using fluent setters.
     * @param name scene identifier
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Set the scene background color.
     * @param background background color
     * @return this Scene for chaining
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Set the ambient light for the scene.
     * @param ambientLight ambient light intensity and color
     * @return this Scene for chaining
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Replace the geometries collection of the scene.
     * @param geometries collection of geometry objects
     * @return this Scene for chaining
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}