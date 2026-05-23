package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

/**
 * Simple ray tracer implementation incorporating Phong Reflection Model.
 */
public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) {
            return _scene.background;
        }

        var closestIntersection = ray.findClosestIntersection(intersections);
        return calcColor(closestIntersection, ray);
    }

    /**
     * Calcule la couleur globale combinant l'ambiant, l'émission et les effets locaux de Phong.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return _scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(intersection.geometry.getEmission())
                .add(calcColorLocalEffects(intersection, ray));
    }

    /**
     * Calcule et accumule les effets d'éclairage locaux de toutes les sources lumineuses.
     */
    private Color calcColorLocalEffects(Intersection intersection, Ray ray) {
        // Étape 1 : Initialiser le cache primaire lié à la géométrie de l'intersection
        preprocessIntersection(intersection, ray);

        Color color = Color.BLACK;
        Vector v = intersection.v;
        Vector n = intersection.n;

        // Étape 2 : Boucler sur l'ensemble des sources lumineuses de la scène
        for (LightSource lightSource : _scene.lights) {
            // Initialiser le cache secondaire lié à la source de lumière actuelle
            preprocessLightSource(intersection, lightSource);
            double nDotL = intersection.nDotL;
            double nDotV = n.dotProduct(v);

            // Condition du modèle de Phong : la lumière et la caméra doivent être du même côté de la surface
            if (nDotL * nDotV > 0) {
                Color lightIntensity = lightSource.getIntensity(intersection.point);

                // Accumulation de l'apport Diffuse + Spéculaire
                color = color.add(
                        lightIntensity.scale(calcDiffuse(intersection)),
                        lightIntensity.scale(calcSpecular(intersection))
                );
            }
        }
        return color;
    }

    /**
     * Calcule le coefficient de réflexion diffuse en utilisant les données du cache.
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double factor = Math.abs(intersection.nDotL);
        return intersection.material.kD.scale(factor);
    }

    /**
     * Calcule le coefficient de réflexion spéculaire en utilisant les données du cache.
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector l = intersection.l;
        Vector n = intersection.n;
        Vector v = intersection.v;
        double nDotL = intersection.nDotL;

        // r = l - 2 * (l . n) * n (Vecteur de réflexion parfait)
        Vector r = l.subtract(n.scale(2 * nDotL)).normalize();

        // Le reflet dépend de l'alignement entre le vecteur de vue (v) et le reflet (r)
        double minusVR = -v.dotProduct(r);

        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        double factor = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }
}