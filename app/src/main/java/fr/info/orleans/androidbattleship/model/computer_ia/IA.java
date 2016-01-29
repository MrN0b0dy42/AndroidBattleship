package fr.info.orleans.androidbattleship.model.computer_ia;

import java.util.Random;

import fr.info.orleans.androidbattleship.model.Grid;

/**
 * Created by Wilsigh on 29/01/2016.
 */
public abstract class IA {

    public abstract int[] computePlay(Grid playerGrid);

    protected int generateRandomDigit() {
        return new Random().nextInt(Grid.SIZE);
    }

    protected boolean notYetShot(Grid grid, int x, int y) {
        Grid.Cell cell = grid.getCells()[x][y];
        if (cell == Grid.Cell.HIT || cell == Grid.Cell.MISS)
            return false;
        return true;
    }

}
