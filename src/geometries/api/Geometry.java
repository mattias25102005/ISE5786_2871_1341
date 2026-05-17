package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometries.
 */
public abstract class Geometry extends Intersectable {

    /**
     * Default constructor for Geometry.
     */
    protected Geometry() {}

    /** The emission color of the geometry - Initialized to Black by default */
    private Color _emission = Color.BLACK;

    /**
     * Returns the normal vector to the geometry at a given point.
     *
     * @param point point on the geometry
     * @return normal vector
     */
    public abstract Vector getNormal(Point point);

    /**
     * Getter for emission color.
     * @return the emission color
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Setter for emission color (Builder pattern).
     * @param emission the new emission color
     * @return the geometry object itself
     */
    public Geometry setEmission(Color emission) {
        this._emission = emission;
        return this;
    }
    /** Material of the geometry */
    private Material _material = new Material();

    /**
     * Getter for the material.
     * @return the material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Setter for the material - Builder Pattern.
     * @param material the material to set
     * @return the geometry object itself
     */
    public Geometry setMaterial(Material material) {
        this._material = material;
        return this;
    }
}