package primitives;

/**
 * Modes d'échantillonnage disponibles pour les surfaces floues et dépolies.
 */
public enum SamplingType {
    /** Grille régulière uniforme */
    GRID,
    /** Éparpillement purement aléatoire */
    RANDOM,
    /** Grille perturbée (mélange de grille et d'aléatoire pour éviter le crénelage) */
    JITTERED
}