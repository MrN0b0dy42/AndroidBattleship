package fr.info.orleans.androidbattleship.model.computer_ia;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Grid;

/**
 * Created by Wilsigh on 29/01/2016.
 */
public class IAFacile extends IA {

    public IAFacile(){

    }

    public int[] computePlay(Grid playerGrid){
        int x, y;
        do {
            x = generateRandomDigit();
            y = generateRandomDigit();
        } while (!notYetShot(playerGrid, x, y));
        Grid.Cell cell = playerGrid.getCells()[x][y];
        final int idRes;
        if (cell == Grid.Cell.EMPTY) {
            idRes = R.drawable.miss;
            playerGrid.setCellAt(x, y, Grid.Cell.MISS);
        } else {
            idRes = R.drawable.hit;
            playerGrid.setCellAt(x, y, Grid.Cell.HIT);
            if (playerGrid.isShipDestroyed(x, y)) {

            }
        }

        int[] coords = {x, y, idRes};
        return coords;
    }
}
