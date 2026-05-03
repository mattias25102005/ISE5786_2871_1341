package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometries.
 */
public abstract class Geometry extends Intersectable {

    /** Protected constructor for subclasses. */
    protected Geometry() {}

    /**
     * Returns the normal vector to the geometry at a given point.
     *
     * @param point point on the geometry
     * @return normal vector
     */
    public abstract Vector getNormal(Point point);
}