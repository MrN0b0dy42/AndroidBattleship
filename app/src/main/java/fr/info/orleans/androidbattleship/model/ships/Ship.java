package fr.info.orleans.androidbattleship.model.ships;

import java.io.Serializable;
import java.util.Random;

import fr.info.orleans.androidbattleship.model.Coordinate;

public abstract class Ship implements Serializable {

    public static final int LENGTH_AIRCRAFT_CARRIER = 5; // Porte-avions
    public static final int LENGTH_CRUISER = 4; // Croiseur
    public static final int LENGTH_DESTROYER = 3; // Contre-torpilleur
    public static final int LENGTH_SUBMARINE = 3; // Sous-marin
    public static final int LENGTH_TORPEDO_BOAT = 2; // Torpilleur

    public enum Orientation { HORIZONTAL, VERTICAL }

    protected int length;
    protected Orientation orientation;
    protected Coordinate[] coordinates;

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Coordinate[] getCoordinates() {
        return coordinates;
    }

    protected void allocCoordinates() {
        coordinates = new Coordinate[length];
        for (int i = 0; i < length; i++)
            coordinates[i] = new Coordinate();
    }

    public void generateRandomOrientation() {
        if (new Random().nextInt(2) == 0)
            orientation = Orientation.HORIZONTAL;
        else
            orientation = Orientation.VERTICAL;
    }

}
