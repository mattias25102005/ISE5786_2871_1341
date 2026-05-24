package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Base class for ray tracers with intersection caching infrastructure.
 */
public abstract class RayTracerBase {
    /** Scene to trace */
    protected Scene _scene;

    /**
     * Constructs a ray tracer for the given scene.
     * @param scene the scene to render
     */
    protected RayTracerBase(Scene scene) {
        this._scene = scene;
    }

    /**
     * Remplit le cache géométrique lié à l'intersection.
     * @param intersection l'intersection à prétraiter
     * @param ray le rayon qui a produit l'intersection
     * @return true si le prétraitement a réussi et que l'intersection est utilisable
     */
    protected boolean preprocessIntersection(Intersection intersection, Ray ray) {
        // 1. On stocke la direction du rayon
        intersection.v = ray.getDirection();

        // 2. On calcule et stocke la normale de la géométrie à ce point
        intersection.normal = intersection.geometry.getNormal(intersection.point);

        // 3. On calcule le produit scalaire entre v et la normale
        intersection.vNormal = intersection.normal.dotProduct(intersection.v);

        return true;
    }

    /**
     * Remplit le cache géométrique spécifique lié à une source lumineuse donnée.
     * @param intersection l'intersection dont on met à jour le cache
     * @param light la source lumineuse courante
     * @return true si le prétraitement pour cette source a réussi
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        // 1. On enregistre la source lumineuse courante dans le cache
        intersection.light = light;

        // 2. On récupère le vecteur directionnel allant de la lumière vers le point (normalisé)
        intersection.l = light.getL(intersection.point);

        // 3. On calcule le produit scalaire entre l et la normale
        intersection.lNormal = intersection.normal.dotProduct(intersection.l);

        return true;
    }

    /**
     * Trace un rayon et retourne la couleur calculée.
     * @param ray le rayon à tracer
     * @return la couleur résultante pour ce rayon
     */
    public abstract Color traceRay(Ray ray);
}