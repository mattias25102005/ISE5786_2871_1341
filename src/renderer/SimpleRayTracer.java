package renderer;

import primitives.*;
import scene.Scene;
import java.util.List;

public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null) return _scene.background;

        Point closestPoint = ray.findClosestPoint(intersections);
        return calcColor(closestPoint);
    }

    // Méthode d'aide demandée par l'énoncé
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }
}