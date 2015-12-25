package fr.info.orleans.androidbattleship.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Grid;
import fr.info.orleans.androidbattleship.InternalStorageManager;

public class GameActivity extends AppCompatActivity implements Runnable, View.OnClickListener {

    private static final int SOUND_INDEX_START = 0;
    private static final int SOUND_INDEX_MISS = 1;
    private static final int SOUND_INDEX_HIT = 2;
    private static final int SOUND_INDEX_DESTROY = 3;
    private static final int SOUND_INDEX_WIN = 4;
    private static final int SOUND_INDEX_LOSE = 5;
    private static final int CODE_ASCII_A = 65;
    private static final int CODE_PLAYER_WIN = 0;
    private static final int CODE_ENEMY_WIN = -1;
    private static final int CODE_GAME_SAVED = 3;
    private static final long DELAY_TIME = 1250;
    public static final String KEY_PLAYER_GRID = "PLAYER_GRID";
    public static final String KEY_ENEMY_GRID = "ENEMY_GRID";
    public static final String KEY_DIFFICULTY = "DIFFICULTY";


    private String difficulty;
    private TextView textViewHintTurn;
    private GridLayout gridLayoutPlayer, gridLayoutEnemy;
    private Button buttonSave;
    private ImageView[][] imageViewsPlayer, imageViewsEnemy;
    private Grid playerGrid, enemyGrid;
    private ArrayList<MediaPlayer> mediaPlayers;
    private boolean playerTurn, gameOver, gameLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        difficulty = getIntent().getExtras().getString("difficulty");
        if (getIntent().getExtras().get("loadGame") != null) {
            gameLoaded = true;
            try {
                playerGrid = (Grid) InternalStorageManager.readObject(this, KEY_PLAYER_GRID);
                enemyGrid = (Grid) InternalStorageManager.readObject(this, KEY_ENEMY_GRID);
                difficulty = (String) InternalStorageManager.readObject(this, KEY_DIFFICULTY);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            gameLoaded = false;
            playerGrid = (Grid) getIntent().getExtras().get("playerGrid");
            enemyGrid = new Grid();
            enemyGrid.arrangeShipsRandomly();
        }
        textViewHintTurn = (TextView) findViewById(R.id.textview_hint_turn);
        gridLayoutPlayer = (GridLayout) findViewById(R.id.gridlayout_player);
        gridLayoutEnemy = (GridLayout) findViewById(R.id.gridlayout_enemy);
        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);
        imageViewsPlayer = allocImageViews();
        imageViewsEnemy = allocImageViews();
        drawGrid(playerGrid, gridLayoutPlayer, imageViewsPlayer, false);
        drawGrid(enemyGrid, gridLayoutEnemy, imageViewsEnemy, true);
        createMediaPlayers();
        playSound(SOUND_INDEX_START);
        playerTurn = true;
        gameOver = false;
        new Thread(this).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if (playerTurn) {
                    try {
                        InternalStorageManager.writeObject(this, KEY_PLAYER_GRID, playerGrid);
                        InternalStorageManager.writeObject(this, KEY_ENEMY_GRID, enemyGrid);
                        InternalStorageManager.writeObject(this, KEY_DIFFICULTY, difficulty);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createPopUp(CODE_GAME_SAVED);
                } else {
                    Toast.makeText(this, getText(R.string.toast_save_text), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void run() {
        while (!gameOver) {
            if (enemyGrid.allShipsAreDestroyed()) {
                gameOver = true;
                playSound(SOUND_INDEX_WIN);
                createPopUp(CODE_PLAYER_WIN);
            }
            if (!playerTurn) {
                setHintTurnText(R.string.textview_enemy_turn_text);
                if (!difficulty.equals(null)) {
                    try {
                        Thread.sleep(DELAY_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    computerPlay(difficulty);
                    if (playerGrid.allShipsAreDestroyed()) {
                        gameOver = true;
                        playSound(SOUND_INDEX_LOSE);
                        createPopUp(CODE_ENEMY_WIN);
                    }
                    setHintTurnText(R.string.textview_player_turn_text);
                }
            }
        }
    }

    private void createPopUp(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String title, message, button;
                switch (code) {
                    case CODE_PLAYER_WIN:
                        title = getResources().getString(R.string.popup_title_victory);
                        message = getResources().getString(R.string.popup_message_victory);
                        break;
                    case CODE_ENEMY_WIN:
                        title = getResources().getString(R.string.popup_title_defeat);
                        message = getResources().getString(R.string.popup_message_defeat);
                        break;
                    case CODE_GAME_SAVED:
                        title = getResources().getString(R.string.popup_title_game_saved);
                        message = getResources().getString(R.string.popup_message_game_saved);
                        break;
                    default:
                        return;
                }
                button = getResources().getString(R.string.popup_button_text);
                new AlertDialog.Builder(GameActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GameActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void setHintTurnText(final int idString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewHintTurn.setText(getResources().getString(idString));
            }
        });
    }

    private ImageView[][] allocImageViews() {
        ImageView[][] imageViews = new ImageView[Grid.SIZE][Grid.SIZE];
        for (int i = 0; i < Grid.SIZE; i++)
            for (int j = 0; j < Grid.SIZE; j++)
                imageViews[i][j] = new ImageView(this);
        return imageViews;
    }


    private void drawGrid(final Grid grid,
                          final GridLayout gridLayout,
                          final ImageView[][] imageViews,
                          final boolean isGridEnemy) {
        gridLayout.post(new Runnable() {
            @Override
            public void run() {
                GridLayout.LayoutParams params;
                int idRes = 0;
                TextView[] rowLetters = new TextView[Grid.SIZE];
                TextView[] columnNumbers = new TextView[Grid.SIZE + 1];
                for (int i = 0; i < Grid.SIZE + 1; i++) {
                    params = new GridLayout.LayoutParams();
                    params.width = gridLayout.getWidth() / (Grid.SIZE + 1);
                    params.height = gridLayout.getHeight() / (Grid.SIZE + 1);
                    columnNumbers[i] = new TextView(GameActivity.this);
                    columnNumbers[i].setLayoutParams(params);
                    columnNumbers[i].setText(getNumber(i));
                    columnNumbers[i].setTypeface(null, Typeface.BOLD);
                    columnNumbers[i].setGravity(Gravity.CENTER);
                    gridLayout.addView(columnNumbers[i]);
                }
                for (int i = 0; i < Grid.SIZE; i++) {
                    params = new GridLayout.LayoutParams();
                    params.width = gridLayout.getWidth() / (Grid.SIZE + 1);
                    params.height = gridLayout.getHeight() / (Grid.SIZE + 1);
                    rowLetters[i] = new TextView(GameActivity.this);
                    rowLetters[i].setLayoutParams(params);
                    rowLetters[i].setText(getLetter(i));
                    rowLetters[i].setTypeface(null, Typeface.BOLD);
                    rowLetters[i].setGravity(Gravity.CENTER);
                    gridLayout.addView(rowLetters[i]);
                    for (int j = 0; j < Grid.SIZE; j++) {
                        params = new GridLayout.LayoutParams();
                        params.width = gridLayout.getWidth() / (Grid.SIZE + 1);
                        params.height = gridLayout.getHeight() / (Grid.SIZE + 1);
                        imageViews[i][j].setLayoutParams(params);
                        if (isGridEnemy) {
                            if (!gameLoaded)
                                idRes = R.drawable.water;
                            else {
                                switch (grid.getCells()[i][j]) {
                                    case EMPTY:
                                        idRes = R.drawable.water;
                                        break;
                                    case SHIP:
                                        idRes = R.drawable.water;
                                        break;
                                    case HIT:
                                        idRes = R.drawable.hit;
                                        break;
                                    case MISS:
                                        idRes = R.drawable.miss;
                                        break;
                                }
                            }
                            imageViews[i][j].setSoundEffectsEnabled(false);
                            final int finalI = i;
                            final int finalJ = j;
                            imageViews[i][j].setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (playerTurn && !gameOver) {
                                        Grid.Cell cell = grid.getCells()[finalI][finalJ];
                                        if (cell == Grid.Cell.EMPTY || cell == Grid.Cell.SHIP) {
                                            switch (event.getAction()) {
                                                case MotionEvent.ACTION_DOWN:
                                                    imageViews[finalI][finalJ].setBackgroundResource(R.drawable.target);
                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    imageViews[finalI][finalJ].setBackgroundResource(R.drawable.water);
                                                    break;
                                            }
                                        }
                                    }
                                    return false;
                                }
                            });
                            imageViews[i][j].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (playerTurn && !gameOver) {
                                        Grid.Cell cell = grid.getCells()[finalI][finalJ];
                                        switch (cell) {
                                            case EMPTY:
                                                playerTurn = false;
                                                imageViews[finalI][finalJ].setBackgroundResource(R.drawable.miss);
                                                grid.setCellAt(finalI, finalJ, Grid.Cell.MISS);
                                                playSound(SOUND_INDEX_MISS);
                                                break;
                                            case SHIP:
                                                imageViews[finalI][finalJ].setBackgroundResource(R.drawable.hit);
                                                grid.setCellAt(finalI, finalJ, Grid.Cell.HIT);
                                                if (enemyGrid.isShipDestroyed(finalI, finalJ))
                                                    playSound(SOUND_INDEX_DESTROY);
                                                else
                                                    playSound(SOUND_INDEX_HIT);
                                                break;
                                            default:
                                                return;
                                        }
                                    }
                                }
                            });
                        } else {
                            switch (grid.getCells()[i][j]) {
                                case EMPTY:
                                    idRes = R.drawable.water;
                                    break;
                                case SHIP:
                                    idRes = R.drawable.ship;
                                    break;
                                case HIT:
                                    idRes = R.drawable.hit;
                                    break;
                                case MISS:
                                    idRes = R.drawable.miss;
                                    break;
                            }
                        }
                        imageViews[i][j].setBackgroundResource(idRes);
                        gridLayout.addView(imageViews[i][j]);
                    }
                }
            }
        });
    }

    private String getLetter(int i) {
        return Character.toString(Character.toChars(CODE_ASCII_A + i)[0]);
    }

    private String getNumber(int i) {
        if (i == 0)
            return " ";
        return Integer.toString(i);
    }


    private void createMediaPlayers() {
        mediaPlayers = new ArrayList<>();
        mediaPlayers.add(SOUND_INDEX_START, MediaPlayer.create(this, R.raw.start));
        mediaPlayers.add(SOUND_INDEX_MISS, MediaPlayer.create(this, R.raw.miss));
        mediaPlayers.add(SOUND_INDEX_HIT, MediaPlayer.create(this, R.raw.hit));
        mediaPlayers.add(SOUND_INDEX_DESTROY, MediaPlayer.create(this, R.raw.destroy));
        mediaPlayers.add(SOUND_INDEX_WIN, MediaPlayer.create(this, R.raw.win));
        mediaPlayers.add(SOUND_INDEX_LOSE, MediaPlayer.create(this, R.raw.lose));
    }

    private void playSound(int index) {
        if (mediaPlayers.get(index).isPlaying()) {
            mediaPlayers.get(index).pause();
            mediaPlayers.get(index).seekTo(0);
        }
        mediaPlayers.get(index).start();
    }

    private void computerPlay(String difficulty) {
        if (difficulty.equals("easy")) {
            int x, y;
            do {
                x = generateRandomDigit();
                y = generateRandomDigit();
            } while (!notYetShot(x, y));
            Grid.Cell cell = playerGrid.getCells()[x][y];
            final int idRes;
            if (cell == Grid.Cell.EMPTY) {
                idRes = R.drawable.miss;
                playerGrid.setCellAt(x, y, Grid.Cell.MISS);
                playerTurn = true;
            } else {
                idRes = R.drawable.hit;
                playerGrid.setCellAt(x, y, Grid.Cell.HIT);
            }
            final int finalX = x;
            final int finalY = y;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageViewsPlayer[finalX][finalY].setBackgroundResource(idRes);
                    switch (idRes) {
                        case R.drawable.miss:
                            playSound(SOUND_INDEX_MISS);
                            break;
                        case R.drawable.hit:
                            if (playerGrid.isShipDestroyed(finalX, finalY))
                                playSound(SOUND_INDEX_DESTROY);
                            else
                                playSound(SOUND_INDEX_HIT);
                            break;
                    }
                }
            });
        }
    }

    private boolean notYetShot(int x, int y) {
        Grid.Cell cell = playerGrid.getCells()[x][y];
        if (cell == Grid.Cell.HIT || cell == Grid.Cell.MISS)
            return false;
        return true;
    }

    private int generateRandomDigit() {
        return new Random().nextInt(Grid.SIZE);
    }

}
