
package scene;

import geometries.impl.Geometries;
import primitives.Color;
import lighting.AmbientLight;
import geometries.api.Geometry;

public class Scene {
    // Tous les champs doivent être publics (PDS)
    public String name;
    public Color background = Color.BLACK;
    public AmbientLight ambientLight = AmbientLight.NONE;
    public Geometries geometries = new Geometries();

    // Le constructeur reçoit uniquement le nom
    public Scene(String name) {
        this.name = name;
    }

    // Setters avec retour de 'this' pour le chaînage (Fluent Interface)
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
}