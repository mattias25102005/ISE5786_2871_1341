package renderer;

import geometries.api.Intersectable.Intersection; // Import indispensable
import primitives.*;
import scene.Scene;

/**
 * Simple ray tracer implementation.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer for a scene.
     * @param scene the scene to render
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    /** Traces the ray and computes the visible color at the closest intersection */
    public Color traceRay(Ray ray) {
        // 1. Appel de la méthode NVI calcIntersections (IMAGE 1 - POINT 5.א)
        var intersections = _scene.geometries.calcIntersections(ray);

        if (intersections == null) {
            return _scene.background;
        }

        // 2. Recherche de l'intersection la plus proche (objet Intersection entier)
        var closestIntersection = ray.findClosestIntersection(intersections);

        // 3. On passe l'objet Intersection à calcColor pour avoir accès à la géométrie
        return calcColor(closestIntersection);
    }

    /**
     * Calcule la couleur au point d'intersection.
     * (IMAGE 1 - POINT 5.ב)
     * @param intersection l'intersection la plus proche
     * @return la couleur calculée (Ambient + Emission)
     */
    /**
     * Calcule la couleur au point d'intersection.
     * @param intersection l'intersection la plus proche
     * @return la couleur calculée (Ambient + Emission)
     */
    private Color calcColor(Intersection intersection) {
        // Formule : Couleur = (Lumière Ambiante) * ka + Émission de la géométrie
        return _scene.ambientLight.getIntensity().scale(intersection.material.kA).add(intersection.geometry.getEmission());
    }
}