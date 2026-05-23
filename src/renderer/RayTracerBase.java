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
     * Remplit le cache géométrique de base propre à l'intersection (Partie B - Point 6.ה).
     * @param intersection l'intersection concernée
     * @param ray le rayon incident
     * @return true après initialisation réussie
     */
    protected boolean preprocessIntersection(Intersection intersection, Ray ray) {
        intersection.v = ray.getDirection().normalize();
        intersection.n = intersection.geometry.getNormal(intersection.point);
        return true;
    }

    /**
     * Remplit le cache géométrique spécifique lié à une source lumineuse donnée (Partie B - Point 6.ה).
     * @param intersection l'intersection concernée
     * @param light la source lumineuse courante
     * @return true après initialisation réussie
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        intersection.l = light.getL(intersection.point);
        intersection.nDotL = intersection.n.dotProduct(intersection.l);
        return true;
    }

    /**
     * Traces a ray and returns the computed color.
     * @param ray the ray to trace
     * @return computed color
     */
    public abstract Color traceRay(Ray ray);
}