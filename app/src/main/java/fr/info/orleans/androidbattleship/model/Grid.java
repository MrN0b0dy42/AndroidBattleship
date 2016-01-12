package fr.info.orleans.androidbattleship.model;

import java.io.Serializable;
import java.util.Random;

import fr.info.orleans.androidbattleship.model.ships.AircraftCarrier;
import fr.info.orleans.androidbattleship.model.ships.Cruiser;
import fr.info.orleans.androidbattleship.model.ships.Destroyer;
import fr.info.orleans.androidbattleship.model.ships.Ship;
import fr.info.orleans.androidbattleship.model.ships.Submarine;
import fr.info.orleans.androidbattleship.model.ships.TorpedoBoat;

import static fr.info.orleans.androidbattleship.model.Grid.Cell.EMPTY;
import static fr.info.orleans.androidbattleship.model.Grid.Cell.SHIP;
import static fr.info.orleans.androidbattleship.model.ships.Ship.Orientation.HORIZONTAL;

public class Grid implements Serializable {

    public static final int SIZE = 10;
    public static final int NB_SHIPS = 7;
    public static final int NB_AIRCRAFT_CARRIERS = 1;
    public static final int NB_CRUISERS = 2;
    public static final int NB_DESTROYERS = 1;
    public static final int NB_SUBMARINES = 1;
    public static final int NB_TORPEDO_BOATS = 2;

    public enum Cell { EMPTY, SHIP, MISS, HIT, DESTROY }

    private Cell[][] cells;
    private Ship[] ships;

    public Grid() {
        allocCells();
        allocShips();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCellAt(int x, int y, Cell cell) {
        cells[x][y] = cell;
    }

    private void allocCells() {
        cells = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                cells[i][j] = EMPTY;
    }

    private void allocShips() {
        ships = new Ship[NB_SHIPS];
        int n = 0;
        for (int i = 0; i < NB_AIRCRAFT_CARRIERS; i++) {
            ships[n] = new AircraftCarrier();
            n++;
        }
        for (int i = 0; i < NB_CRUISERS; i++) {
            ships[n] = new Cruiser();
            n++;
        }
        for (int i = 0; i < NB_DESTROYERS; i++) {
            ships[n] = new Destroyer();
            n++;
        }
        for (int i = 0; i <NB_SUBMARINES; i++) {
            ships[n] = new Submarine();
            n++;
        }
        for (int i = 0; i < NB_TORPEDO_BOATS; i++) {
            ships[n] = new TorpedoBoat();
            n++;
        }
    }

    public void resetGrid() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                cells[i][j] = EMPTY;
    }

    public boolean isShipDestroyed(int x, int y) {
        int xTemp, yTemp;
        Ship ship = getShipByCoordinate(x, y);
        for (int i = 0; i < ship.getLength(); i++) {
            xTemp = ship.getCoordinates()[i].getX();
            yTemp = ship.getCoordinates()[i].getY();
            if (cells[xTemp][yTemp] == SHIP)
                return false;
        }
        return true;
    }

    public boolean allShipsAreDestroyed() {
        Ship ship;
        int x, y;
        for (int i = 0; i < NB_SHIPS; i++) {
            ship = ships[i];
            for (int j = 0; j < ship.getLength(); j++) {
                x = ship.getCoordinates()[j].getX();
                y = ship.getCoordinates()[j].getY();
                if (cells[x][y] == SHIP)
                    return false;
            }
        }
        return true;
    }

    private Ship getShipByCoordinate(int x, int y) {
        Ship ship;
        System.out.println("nombre de bateau : " + NB_SHIPS + "\nx | y : " + x + " | " + y);
        for (int i = 0; i < NB_SHIPS; i++) {
            ship = ships[i];
            for (int j = 0; j < ship.getLength(); j++) {
                //System.out.println("bateau n°" + i + " morecau n°" + j + " x | y : " + x + " | " + y);
                if (ship.getCoordinates()[j].getX() == x && ship.getCoordinates()[j].getY() == y)
                    return ship;
            }
        }
        return null;
    }

    public int[][] shipFlow(int x, int y) {
        Ship ship = getShipByCoordinate(x, y);
        if(ship == null){
            System.out.println("Envoie d'un null : " + x + " | " + y);
            return null;
        }
        int[][] coordShip = new int[2][ship.getLength()];
        for (int i = 0; i < ship.getLength(); i++) {
            coordShip[0][i] = ship.getCoordinates()[i].getX();
            coordShip[1][i] = ship.getCoordinates()[i].getY();
        }
        return coordShip;
    }

    public int[][] shipFlowAround(int x, int y, int gridLength) {
        Ship ship = getShipByCoordinate(x, y);
        int[][] coordShipAround = ship.getAround(ship.getCoordinates()[0].getX(), ship.getCoordinates()[0].getY(), gridLength);
        return coordShipAround;
    }

    private int generateRandomDigit() {
        return new Random().nextInt(SIZE);
    }

    public void arrangeShipsRandomly() {
        boolean shipArranged, headArranged;
        boolean[][] validCoordinates = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                validCoordinates[i][j] = true;
        for (int i = 0; i < NB_SHIPS; i++) {
            shipArranged = false;
            while (!shipArranged) {
                headArranged = false;
                while (!headArranged) {
                    ships[i].generateRandomOrientation();
                    headArranged = arrangeHead(ships[i], validCoordinates);
                }
                shipArranged = arrangeShip(ships[i], validCoordinates);
            }
        }
    }

    private boolean arrangeHead(Ship ship, boolean[][] validCoordinates) {
        int x = generateRandomDigit();
        int y = generateRandomDigit();
        if (isValidCoordinate(x, y, validCoordinates)) {
            ship.getCoordinates()[0].setX(x);
            ship.getCoordinates()[0].setY(y);
            return true;
        }
        return false;
    }

    private boolean isValidCoordinate(int x, int y, boolean[][] validCoordinates) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE)
            return false;
        return validCoordinates[x][y];
    }

    private boolean arrangeShip(Ship ship, boolean[][] validCoordinates) {
        int x, y;
        int xHead = ship.getCoordinates()[0].getX();
        int yHead = ship.getCoordinates()[0].getY();
        if (ship.getOrientation() == HORIZONTAL) {
            for (int i = 1; i < ship.getLength(); i++) {
                x = xHead;
                y = yHead + i;
                if (isValidCoordinate(x, y, validCoordinates)) {
                    ship.getCoordinates()[i].setX(x);
                    ship.getCoordinates()[i].setY(y);
                } else
                    return false;
            }
            for (int i = 0; i < ship.getLength(); i++) {
                x = ship.getCoordinates()[i].getX();
                y = ship.getCoordinates()[i].getY();
                cells[x][y] = SHIP;
            }
        } else {
            for (int i = 1; i < ship.getLength(); i++) {
                x = xHead + i;
                y = yHead;
                if (isValidCoordinate(x, y, validCoordinates)) {
                    ship.getCoordinates()[i].setX(x);
                    ship.getCoordinates()[i].setY(y);
                } else
                    return false;
            }
            for (int i = 0; i < ship.getLength(); i++) {
                x = ship.getCoordinates()[i].getX();
                y = ship.getCoordinates()[i].getY();
                cells[x][y] = SHIP;
            }
        }
        setInvalidCoordinates(ship, validCoordinates);
        return true;
    }

    private void setInvalidCoordinates(Ship ship, boolean[][] validCoordinates) {
        int x, y;
        for (int i = 0; i < ship.getLength(); i++) {
            x = ship.getCoordinates()[i].getX();
            y = ship.getCoordinates()[i].getY();
            validCoordinates[x][y] = false;
            if (ship.getOrientation() == HORIZONTAL) {
                if (i == 0) {
                    if (x - 1 >= 0 && y - 1 >= 0) validCoordinates[x - 1][y - 1] = false;
                    if (x + 1 <= 9 && y - 1 >= 0) validCoordinates[x + 1][y - 1] = false;
                }
                if (i == ship.getLength() - 1) {
                    if (x - 1 >= 0 && y + 1 <= 9) validCoordinates[x-1][y + 1] = false;
                    if (x + 1 <= 9 && y + 1 <= 9) validCoordinates[x+1][y + 1] = false;
                }
            } else {
                if (i == 0) {
                    if (x - 1 >= 0 && y - 1 >= 0) validCoordinates[x - 1][y - 1] = false;
                    if (x - 1 >= 0 && y + 1 <= 9) validCoordinates[x - 1][y + 1] = false;
                }
                if (i == ship.getLength() - 1) {
                    if (x + 1 <= 9 && y - 1 >= 0) validCoordinates[x + 1][y - 1] = false;
                    if (x + 1 <= 9 && y + 1 <= 9) validCoordinates[x + 1][y + 1] = false;
                }
            }
            if (x - 1 >= 0) validCoordinates[x - 1][y] = false;
            if (x + 1 <= 9) validCoordinates[x + 1][y] = false;
            if (y - 1 >= 0) validCoordinates[x][y - 1] = false;
            if (y + 1 <= 9) validCoordinates[x][y + 1] = false;
        }
    }


}
