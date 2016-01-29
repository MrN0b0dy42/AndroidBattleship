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

    public enum Orientation { HORIZONTAL, VERTICAL, DIAGONAL }

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
        if (new Random().nextInt(3) == 0)
            orientation = Orientation.HORIZONTAL;
        else
        {
            if(new Random().nextInt(2) == 0)
                orientation = Orientation.VERTICAL;
            else
                orientation = Orientation.DIAGONAL;
        }
    }

    public int[][] getAround(int x, int y, int gridLength){
        int[][] around = new int[2][4+(2*length)];
        if(orientation == Orientation.VERTICAL)
        {
            if(x == 0) // test si positionné à l'extrèmité haute
                for(int i = 0; i < 2; i++)
                {
                    around[0][i] = -1;
                    around[1][i] = -1;
                }
            else
            {
                for(int i = 0; i < 2; i++)
                {
                    around[0][i] = x - 1;
                }
                around[1][0] = y;
                around[1][1] = y + 1;
            }
            if(x + y + length == gridLength) // test si positionné à l'extrèmité basse
                for(int i = 0; i < 2; i++)
                {
                    around[0][i+2] = -1;
                    around[1][i+2] = -1;
                }
            else
            {
                around[0][2] = x + length;
                around[1][2] = y;
                around[0][3] = x + length - 1;
                around[1][3] = y + 1;
            }
            if(y == 0) // test si positionné à l'extrèmité gauche
                for(int i = 0; i < length + 1; i++)
                {
                    around[0][i+4] = -1;
                    around[1][i+4] = -1;
                }
            else
            {
                for(int i = 0; i < length + 1; i++)
                {
                    around[0][i+4] = x + i;
                    around[1][i+4] = y - 1;
                }
            }
            for(int i = 0; i < length - 1; i++) // existent toujours
            {
                around[0][i+length+5] = x + i;
                around[1][i+length+5] = y + 1;
            }
        }
        else
        {
            if(orientation == Orientation.HORIZONTAL)
            {
                if(y == 0) // test si positionné à l'extrèmité gauche
                    for(int i = 0; i < 2; i++)
                    {
                        around[0][i] = -1;
                        around[1][i] = -1;
                    }
                else
                {
                    around[0][0] = x;
                    around[0][1] = x + 1;
                    for(int i = 0; i < 2; i++)
                    {
                        around[1][i] = y - 1;
                    }
                }
                if(x + y + length == gridLength) // test si positionné à l'extrèmité basse
                    for(int i = 0; i < 2; i++)
                    {
                        around[0][i+2] = -1;
                        around[1][i+2] = -1;
                    }
                else
                {
                    around[0][2] = x;
                    around[1][2] = y + length;
                    around[0][3] = x + 1;
                    around[1][3] = y + length - 1;
                }
                if(y == 0) // test si positionné à l'extrèmité haute
                    for(int i = 0; i < length + 1; i++)
                    {
                        around[0][i+4] = -1;
                        around[1][i+4] = -1;
                    }
                else
                {
                    for(int i = 0; i < length + 1; i++)
                    {
                        around[0][i+4] = x - 1;
                        around[1][i+4] = y + i;
                    }
                }
                for(int i = 0; i < length - 1; i++) // existent toujours
                {
                    around[0][i+length+5] = x + 1;
                    around[1][i+length+5] = y + i;
                }
            }
            else // DIAGONAL
            {
                if(y == 0) // test si positionné à l'extrèmité gauche
                    for(int i = 0; i < 2; i++)
                    {
                        around[0][i] = -1;
                        around[1][i] = -1;
                    }
                else
                {
                    around[0][0] = x;
                    around[0][1] = x + 1;
                    for(int i = 0; i < 2; i++)
                    {
                        around[1][i] = y - 1;
                    }
                }
                if(x == length - 1) // test si positionné à l'extrèmité haute
                    for(int i = 0; i < 2; i++)
                    {
                        around[0][i+2] = -1;
                        around[1][i+2] = -1;
                    }
                {
                    around[0][2] = x + length - 1;
                    around[1][2] = y - length;
                    around[0][3] = x + length;
                    around[1][3] = y - length;
                }
                if(x + y == gridLength - 1) // test si positionné à l'extrèmité basse
                {
                    for(int i = 0; i < length + 1; i++)
                    {
                        around[0][i+4] = -1;
                        around[1][i+4] = -1;
                    }
                }
                else
                {
                    for(int i = 0; i < length + 1; i++)
                    {
                        around[0][i+4] = x + 1 - i;
                        around[1][i+4] = y + i;
                    }
                }
                for(int i = 0; i < length - 1; i++) // existent toujours
                {
                    around[0][i+length+5] = x - 1 - i;
                    around[1][i+length+5] = y + i;
                }
            }
        }
        return around;
    }

}
