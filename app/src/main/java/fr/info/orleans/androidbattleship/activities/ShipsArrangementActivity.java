package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Grid;
import fr.info.orleans.androidbattleship.model.ships.Ship;

public class ShipsArrangementActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ASCII_CODE_A = 65;
    private static final int NB_AC = 1;
    private static final int NB_CR = 2;
    private static final int NB_DE = 1;
    private static final int NB_SU = 1;
    private static final int NB_TO = 2;

    private static final int HORIZONTAL_PSA = 0;
    private static final int VERTICAL_PSA = 1;
    private static final boolean SHIP_LENGTH_ONE = false;
    private static final int MAX_LENGTH_SHIP = 5;
    private static final int LENGTH_BOARD = 10;

    private int nb_ac, nb_cr, nb_de, nb_su, nb_to, lengthShipTaken;
    private String difficulty;
    private Button buttonRandomGeneration, buttonPlay, changeOrientation, ship5, ship4, ship3_1, ship3_2, ship2, suppression;
    private GridLayout gridLayoutShipsArrangement;
    private ImageView[][] imageViews;
    private Grid playerGrid;
    private Grid[][] placementShipAutorized;
    private boolean HORIZONTAL, takeShip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ships_arrangement);
        difficulty = getIntent().getExtras().getString("difficulty");
        buttonRandomGeneration = (Button) findViewById(R.id.button_random_generation);
        buttonPlay = (Button) findViewById(R.id.button_play);
        changeOrientation = (Button) findViewById(R.id.button_change_orientation);
        changeOrientation.setText("Orientation : HORIZONTAL");
        ship5 = (Button) findViewById(R.id.button_ship5);
        ship4 = (Button) findViewById(R.id.button_ship4);
        ship3_1 = (Button) findViewById(R.id.button_ship3_1);
        ship3_2 = (Button) findViewById(R.id.button_ship3_2);
        ship2 = (Button) findViewById(R.id.button_ship2);
        suppression = (Button) findViewById(R.id.button_suppression);
        gridLayoutShipsArrangement = (GridLayout) findViewById(R.id.gridlayout_ships_arrangement);
        buttonRandomGeneration.setOnClickListener(this);
        buttonPlay.setOnClickListener(this);
        changeOrientation.setOnClickListener(this);
        ship5.setOnClickListener(this);
        ship4.setOnClickListener(this);
        ship3_1.setOnClickListener(this);
        ship3_2.setOnClickListener(this);
        ship2.setOnClickListener(this);
        suppression.setOnClickListener(this);
        HORIZONTAL = true;
        takeShip = false;
        nb_ac = 0;
        nb_cr = 0;
        nb_de = 0;
        nb_su = 0;
        nb_to = 0;
        Toast.makeText(this, getText(R.string.toast_drag_and_drop_text), Toast.LENGTH_SHORT).show();
        allocImageViews();
        placementShipAutorized = new Grid[2][MAX_LENGTH_SHIP];
        initGridPlacementShipAutorized();
        playerGrid = new Grid();
        playerGrid.arrangeShipsRandomly();
        drawGrid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_random_generation:
                playerGrid.resetGrid();
                playerGrid.arrangeShipsRandomly();
                updateGrid();
                break;
            case R.id.button_suppression:
                playerGrid.resetGrid();
                updateGrid();
                break;
            case R.id.button_play:
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("playerGrid", playerGrid);
                startActivity(intent);
                break;
            case R.id.button_change_orientation:
                changeOrientation.setText("Orientation : " + ((!HORIZONTAL) ? "HORIZONTAL" : "VERTICAL"));
                HORIZONTAL = (!HORIZONTAL) ? true : false;
                break;
            case R.id.button_ship5:
                if(takeShip && lengthShipTaken == 5) {
                    takeShip = false;
                    lengthShipTaken = -1;
                    ship5.setBackgroundResource(R.drawable.blue_light);
                } else {
                    if(nb_ac < NB_AC) {
                        takeShip = true;
                        lengthShipTaken = 5;
                        ship5.setBackgroundResource(R.drawable.blue_dark);
                        ship4.setBackgroundResource(R.drawable.blue_light);
                        ship3_1.setBackgroundResource(R.drawable.blue_light);
                        ship3_2.setBackgroundResource(R.drawable.blue_light);
                        ship2.setBackgroundResource(R.drawable.blue_light);
                    }
                }
                break;
            case R.id.button_ship4:
                if(takeShip && lengthShipTaken == 4) {
                    takeShip = false;
                    lengthShipTaken = -1;
                    ship4.setBackgroundResource(R.drawable.blue_light);
                } else {
                    if(nb_ac < NB_AC) {
                        takeShip = true;
                        lengthShipTaken = 4;
                        ship5.setBackgroundResource(R.drawable.blue_light);
                        ship4.setBackgroundResource(R.drawable.blue_dark);
                        ship3_1.setBackgroundResource(R.drawable.blue_light);
                        ship3_2.setBackgroundResource(R.drawable.blue_light);
                        ship2.setBackgroundResource(R.drawable.blue_light);
                    }
                }
                break;
            case R.id.button_ship3_1:
                if(takeShip && lengthShipTaken == 3) {
                    takeShip = false;
                    lengthShipTaken = -1;
                    ship3_1.setBackgroundResource(R.drawable.blue_light);
                } else {
                    if(nb_ac < NB_AC) {
                        takeShip = true;
                        lengthShipTaken = 3;
                        ship5.setBackgroundResource(R.drawable.blue_light);
                        ship4.setBackgroundResource(R.drawable.blue_light);
                        ship3_1.setBackgroundResource(R.drawable.blue_dark);
                        ship3_2.setBackgroundResource(R.drawable.blue_light);
                        ship2.setBackgroundResource(R.drawable.blue_light);
                    }
                }
                break;
            case R.id.button_ship3_2:
                if(takeShip && lengthShipTaken == 3) {
                    takeShip = false;
                    lengthShipTaken = -1;
                    ship3_2.setBackgroundResource(R.drawable.blue_light);
                } else {
                    if(nb_ac < NB_AC) {
                        takeShip = true;
                        lengthShipTaken = 3;
                        ship5.setBackgroundResource(R.drawable.blue_light);
                        ship4.setBackgroundResource(R.drawable.blue_light);
                        ship3_1.setBackgroundResource(R.drawable.blue_light);
                        ship3_2.setBackgroundResource(R.drawable.blue_dark);
                        ship2.setBackgroundResource(R.drawable.blue_light);
                    }
                }
                break;
            case R.id.button_ship2:
                if(takeShip && lengthShipTaken == 2) {
                    takeShip = false;
                    lengthShipTaken = -1;
                    ship2.setBackgroundResource(R.drawable.blue_light);
                } else {
                    if(nb_ac < NB_AC) {
                        takeShip = true;
                        lengthShipTaken = 2;
                        ship5.setBackgroundResource(R.drawable.blue_light);
                        ship4.setBackgroundResource(R.drawable.blue_light);
                        ship3_1.setBackgroundResource(R.drawable.blue_light);
                        ship3_2.setBackgroundResource(R.drawable.blue_light);
                        ship2.setBackgroundResource(R.drawable.blue_dark);
                    }
                }
                break;
        }
    }

    private void allocImageViews() {
        imageViews = new ImageView[Grid.SIZE][Grid.SIZE];
        for (int i = 0; i < Grid.SIZE; i++)
            for (int j = 0; j < Grid.SIZE; j++)
                imageViews[i][j] = new ImageView(this);
    }

    private void drawGrid() {
        gridLayoutShipsArrangement.post(new Runnable() {
            @Override
            public void run() {
                GridLayout.LayoutParams params;
                int idRes = 0;
                TextView[] rowLetters = new TextView[Grid.SIZE];
                TextView[] columnNumbers = new TextView[Grid.SIZE + 1];
                for (int i = 0; i <Grid.SIZE + 1; i++) {
                    params = new GridLayout.LayoutParams();
                    params.width = gridLayoutShipsArrangement.getWidth() / (Grid.SIZE + 1);
                    params.height = gridLayoutShipsArrangement.getHeight() / ( Grid.SIZE + 1);
                    columnNumbers[i] = new TextView(ShipsArrangementActivity.this);
                    columnNumbers[i].setLayoutParams(params);
                    columnNumbers[i].setText(getNumber(i));
                    columnNumbers[i].setTypeface(null, Typeface.BOLD);
                    columnNumbers[i].setGravity(Gravity.CENTER);
                    gridLayoutShipsArrangement.addView(columnNumbers[i]);
                }
                for (int i = 0; i < Grid.SIZE; i++) {
                    params = new GridLayout.LayoutParams();
                    params.width = gridLayoutShipsArrangement.getWidth() / (Grid.SIZE + 1);
                    params.height = gridLayoutShipsArrangement.getHeight() / ( Grid.SIZE + 1);
                    rowLetters[i] = new TextView(ShipsArrangementActivity.this);
                    rowLetters[i].setLayoutParams(params);
                    rowLetters[i].setText(getLetter(i));
                    rowLetters[i].setTypeface(null, Typeface.BOLD);
                    rowLetters[i].setGravity(Gravity.CENTER);
                    gridLayoutShipsArrangement.addView(rowLetters[i]);
                    for (int j= 0; j < Grid.SIZE; j++) {
                        params = new GridLayout.LayoutParams();
                        params.width = gridLayoutShipsArrangement.getWidth() / (Grid.SIZE + 1);
                        params.height = gridLayoutShipsArrangement.getHeight() / ( Grid.SIZE + 1);
                        imageViews[i][j].setLayoutParams(params);
                        switch (playerGrid.getCells()[i][j]) {
                            case EMPTY:
                                idRes = R.drawable.water;
                                break;
                            case SHIP:
                                idRes = R.drawable.ship;
                                break;
                        }
                        imageViews[i][j].setBackgroundResource(idRes);
                        gridLayoutShipsArrangement.addView(imageViews[i][j]);
                    }
                }
            }
        });
    }

    private void updateGrid() {
        gridLayoutShipsArrangement.post(new Runnable() {
            @Override
            public void run() {
                int idRes = 0;
                for (int i = 0; i < Grid.SIZE; i++) {
                    for (int j = 0; j < Grid.SIZE; j++) {
                        switch (playerGrid.getCells()[i][j]) {
                            case EMPTY:
                                idRes = R.drawable.water;
                                break;
                            case SHIP:
                                idRes = R.drawable.ship;
                                break;
                        }
                        imageViews[i][j].setBackgroundResource(idRes);
                    }
                }
            }
        });
    }

    private String getLetter(int i) {
        return Character.toString(Character.toChars(ASCII_CODE_A + i)[0]);
    }

    private String getNumber(int i) {
        if (i == 0)
            return " ";
        return Integer.toString(i);
    }

    private void initGridPlacementShipAutorized(){
        int i, j;
        for(i = 0; i < MAX_LENGTH_SHIP; i++) {
            placementShipAutorized[HORIZONTAL_PSA][i] = new Grid();
            placementShipAutorized[VERTICAL_PSA][i] = new Grid();
        }

        for(i = LENGTH_BOARD - MAX_LENGTH_SHIP + 1; i < LENGTH_BOARD; i++){
            for(j = 0; j < LENGTH_BOARD; j++){
                updateCellPSA(i, j, LENGTH_BOARD - i, HORIZONTAL_PSA);
                placementShipAutorized[HORIZONTAL_PSA][0].setCellAt(i, j, (SHIP_LENGTH_ONE) ? Grid.Cell.EMPTY : Grid.Cell.DESTROY);
                updateCellPSA(j, i, LENGTH_BOARD - i, VERTICAL_PSA);
                placementShipAutorized[VERTICAL_PSA][0].setCellAt(j, i, (SHIP_LENGTH_ONE) ? Grid.Cell.EMPTY : Grid.Cell.DESTROY);
            }
        }

    }

    private void updateAllGrid(Ship ship){

        //placement du bateau sur toutes les vues
        for(int i = 0; i < ship.getLength(); i++){
            playerGrid.setCellAt(ship.getCoordinates()[i].getX(), ship.getCoordinates()[i].getY(), Grid.Cell.SHIP);
            for(int j = (SHIP_LENGTH_ONE) ? 0 : 1; j < MAX_LENGTH_SHIP; j++){
                placementShipAutorized[HORIZONTAL_PSA][j].setCellAt(ship.getCoordinates()[i].getX(), ship.getCoordinates()[i].getY(), Grid.Cell.SHIP);
                placementShipAutorized[VERTICAL_PSA][j].setCellAt(ship.getCoordinates()[i].getX(), ship.getCoordinates()[i].getY(), Grid.Cell.SHIP);
            }
        }

        //placement around
        int[][] around = ship.getAround(ship.getCoordinates()[0].getX(), ship.getCoordinates()[1].getY(), LENGTH_BOARD);
        for(int i = 0; i < around.length; i++){
            if(around[0][i] != -1 && around[1][i] != -1) {
                playerGrid.setCellAt(around[HORIZONTAL_PSA][i], around[VERTICAL_PSA][i], Grid.Cell.MISS);
                for (int j = (SHIP_LENGTH_ONE) ? 0 : 1; j < MAX_LENGTH_SHIP; j++) {
                    placementShipAutorized[HORIZONTAL_PSA][j].setCellAt(around[HORIZONTAL_PSA][i], around[VERTICAL_PSA][i], Grid.Cell.MISS);
                    placementShipAutorized[VERTICAL_PSA][j].setCellAt(around[HORIZONTAL_PSA][i], around[VERTICAL_PSA][i], Grid.Cell.MISS);
                }
            }
        }

        updateGridPlacementShipAutorized(ship);
    }

    public void updateGridPlacementShipAutorized(Ship ship){
        int shipX = ship.getCoordinates()[0].getX();
        int shipY = ship.getCoordinates()[0].getY();
        boolean horizontal = (2 * shipX == shipX + ship.getCoordinates()[1].getX()) ? true : false;
        int startX, startY;

        if(horizontal){
            startY = shipY - 2;
            if(startY >= 0){
                for(int j = startY; j >= 0 && startY - j + 1 < MAX_LENGTH_SHIP; j--){
                    updateCellPSA(shipX, j, startY - j + 1, HORIZONTAL_PSA);
                }
            }

            startX = shipX - 2;
            if(startX >= 0){
                for(int j = shipY; j < ship.getLength(); j++){
                    for(int i = startX; i >= 0 && startX - i + 1 < MAX_LENGTH_SHIP; i--){
                        updateCellPSA(i, j, startX - i + 1, VERTICAL_PSA);
                    }
                }
            }
        }
        else{//Vertical
            startY = shipY - 2;
            if(startY >= 0){
                for(int i = shipX; i < ship.getLength(); i++){
                    for(int j = startY; j >= 0 && startY - j + 1 < MAX_LENGTH_SHIP; j--){
                        updateCellPSA(i, j, startY - i + 1, HORIZONTAL_PSA);
                    }
                }
            }

            startX = shipX - 2;
            if(startX >= 0){
                for(int i = startX; i >= 0 && startX - i + 1 < MAX_LENGTH_SHIP; i--){
                    updateCellPSA(i, shipY, startX - i + 1, VERTICAL_PSA);
                }
            }
        }


    }

    private void resetGridPlacementShipAutorized(){
        initGridPlacementShipAutorized();
        for(int i = 0; i < LENGTH_BOARD; i++){
            for(int j = 0; j < LENGTH_BOARD; j++){
                if(playerGrid.getCells()[i][j] == Grid.Cell.SHIP) {
                    for(int k = (SHIP_LENGTH_ONE) ? 0 : 1; k < MAX_LENGTH_SHIP; k++){
                        placementShipAutorized[HORIZONTAL_PSA][k].setCellAt(i, j, Grid.Cell.SHIP);
                        placementShipAutorized[VERTICAL_PSA][k].setCellAt(i, j, Grid.Cell.SHIP);
                    }
                }
            }
        }

        //pour i le nombre de bateau déjà en jeu
        //updateGridPlacementShipAutorized(ship[i]);
    }

    private void updateCellPSA(int x, int y, int endWater, int orientation) {
        System.out.println("ucpsa -> " + x + " | " + y + " > " + endWater);

        if(endWater < MAX_LENGTH_SHIP) {
            for (int i = (endWater < 1) ? 1 : endWater; i < MAX_LENGTH_SHIP; i++) {
                System.out.println("\tucpsa DESTROY -> " + x + " | " + y + " -> " + i);
                if(placementShipAutorized[orientation][i].getCells()[x][y] == Grid.Cell.EMPTY)
                    placementShipAutorized[orientation][i].setCellAt(x, y, Grid.Cell.DESTROY);
            }
        }
    }


}
