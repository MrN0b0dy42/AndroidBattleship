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

public class ShipsArrangementActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ASCII_CODE_A = 65;

    private String difficulty;
    private Button buttonRandomGeneration, buttonPlay;
    private GridLayout gridLayoutShipsArrangement;
    private ImageView[][] imageViews;
    private Grid playerGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ships_arrangement);
        difficulty = getIntent().getExtras().getString("difficulty");
        buttonRandomGeneration = (Button) findViewById(R.id.button_random_generation);
        buttonPlay = (Button) findViewById(R.id.button_play);
        gridLayoutShipsArrangement = (GridLayout) findViewById(R.id.gridlayout_ships_arrangement);
        buttonRandomGeneration.setOnClickListener(this);
        buttonPlay.setOnClickListener(this);
        Toast.makeText(this, getText(R.string.toast_drag_and_drop_text), Toast.LENGTH_SHORT).show();
        allocImageViews();
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
            case R.id.button_play:
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("playerGrid", playerGrid);
                startActivity(intent);
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

}
