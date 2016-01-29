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

    public void setOrientation(boolean horizontal) {
        orientation = (horizontal) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
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

    public int[][] getAround(int x, int y, int gridLength){
        int[][] around = new int[2][6+(2*length)];
        if(orientation == Orientation.VERTICAL){
            //trois premier -> test bateau collé en haut
            if(x == 0){
                for(int i = 0; i < 3; i++){
                    around[0][i] = -1;
                    around[1][i] = -1;
                }
            }
            else{
                for(int i = 0; i < 3; i++){
                    around[0][i] = x - 1;
                }
                around[1][0] = (y == 0) ? -1 : y - 1;
                around[1][1] = y;
                around[1][2] = (y == gridLength - 1) ? -1 : y + 1;
            }

            //length*2 suivant -> test si bateau collé gauche ou droite
            for (int i = 0; i < length; i++) {
                around[0][3 + i] = x + i;
                around[1][3 + i] = (y == 0) ? -1 : y - 1;
                around[0][3 + i + length] = x + i;
                around[1][3 + i + length] = (y == gridLength - 1) ? -1 : y + 1;
            }

            //les trois derniers -> test si x collé en bas
            if(x + length - 1 == gridLength - 1){
                for(int i = 0; i < 3; i++){
                    around[0][3+(2*length)+i] = -1;
                    around[1][3+(2*length)+i] = -1;
                }
            }
            else{
                for(int i = 0; i < 3; i++){
                    around[0][3+(2*length)+i] = x + length;
                }
                around[1][3+(2*length)] = (y == 0) ? -1 : y - 1;
                around[1][3+(2*length)+1] = y;
                around[1][3+(2*length)+2] = (y == gridLength - 1) ? -1 : y + 1;
            }
        }
        else{//HORIZONTAL
            //trois premier -> test bateau collé à gauche
            if(y == 0){
                for(int i = 0; i < 3; i++){
                    around[0][i] = -1;
                    around[1][i] = -1;
                }
            }
            else{
                for(int i = 0; i < 3; i++){
                    around[1][i] = y - 1;
                }
                around[0][0] = (x == 0) ? -1 : x - 1;
                around[0][1] = x;
                around[0][2] = (x == gridLength - 1) ? -1 : x + 1;
            }

            //length*2 suivant -> test si bateau colléen haut ou en bas
            for (int i = 0; i < length; i++) {
                around[0][3 + i] = (x == 0) ? -1 : x - 1;
                around[1][3 + i] = y + i;
                around[0][3 + i + length] = (x == gridLength - 1) ? -1 : x + 1;
                around[1][3 + i + length] = y + i;
            }

            //les trois derniers -> test si x collé à droite
            if(y + length - 1 == gridLength - 1){
                for(int i = 0; i < 3; i++){
                    around[0][3+(2*length)+i] = -1;
                    around[1][3+(2*length)+i] = -1;
                }
            }
            else{
                for(int i = 0; i < 3; i++){
                    around[1][3+(2*length)+i] = y + length;
                }
                around[0][3+(2*length)] = (x == 0) ? -1 : x - 1;
                around[0][3+(2*length)+1] = x;
                around[0][3+(2*length)+2] = (x == gridLength - 1) ? -1 : x + 1;
            }
        }
        return around;
    }

}
