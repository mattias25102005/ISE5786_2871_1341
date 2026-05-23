package scene;

import geometries.impl.Geometries;
import primitives.Color;
import lighting.AmbientLight;
import lighting.LightSource;
import java.util.LinkedList;
import java.util.List;

/**
 * Scene représente une collection de geometries, background et lighting settings.
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

    /** Liste des sources de lumière externes de la scène (Partie B - Point 4) */
    public List<LightSource> lights = new LinkedList<>();

    /**
     * Create a new scene with the given name.
     * @param name scene identifier
     */
    public Scene(String name) {
        this.name = name;
    }

    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    /**
     * Configure la liste complète des sources de lumière de la scène (Fluent API).
     * @param lights liste des sources lumineuses
     * @return la scène courante
     */
    public Scene setLights(List<LightSource> lights) {
        this.lights = lights;
        return this;
    }
}