    package fr.info.orleans.androidbattleship.activities;

    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.graphics.Typeface;
    import android.media.MediaPlayer;
    import android.support.annotation.ArrayRes;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.Gravity;
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.inputmethod.CorrectionInfo;
    import android.widget.Button;
    import android.widget.GridLayout;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Random;

    import fr.info.orleans.androidbattleship.R;
    import fr.info.orleans.androidbattleship.model.Coordinate;
    import fr.info.orleans.androidbattleship.model.Grid;
    import fr.info.orleans.androidbattleship.InternalStorageManager;
    import fr.info.orleans.androidbattleship.model.computer_ia.IA;
    import fr.info.orleans.androidbattleship.model.computer_ia.IADifficile;
    import fr.info.orleans.androidbattleship.model.computer_ia.IAFacile;

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
        private static final int LENGTH_BOARD = 10;
        private static final int HORIZONTAL = 0;
        private static final int VERTICAL = 1;


        public static final String KEY_PLAYER_GRID = "PLAYER_GRID";
        public static final String KEY_ENEMY_GRID = "ENEMY_GRID";
        public static final String KEY_DIFFICULTY = "DIFFICULTY";
        public static final String IA_EASY = "easy";
        public static final String IA_HARD = "hard";

        private int xtarget;
        private int ytarget;
        private String difficulty;
        private TextView textViewHintTurn;
        private GridLayout gridLayoutPlayer, gridLayoutEnemy;
        private Button buttonSave;
        private ImageView[][] imageViewsPlayer, imageViewsEnemy;
        private Grid playerGrid, enemyGrid;
        private IA computer;
        private ArrayList<MediaPlayer> mediaPlayers;
        private boolean playerTurn, gameOver, gameLoaded, firstLengthLayout;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game);
            difficulty = getIntent().getExtras().getString("difficulty");
            if(difficulty.equals(IA_EASY))
                computer = new IAFacile();
            else if(difficulty.equals(IA_HARD))
                computer = new IADifficile();
            else
                System.out.println("erreur difficulté");

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
            firstLengthLayout = true;
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
                        computerPlay();
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
                            if(firstLengthLayout) saveLengthLayout(params.width, params.height);
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
                                        case DESTROY:
                                            idRes = R.drawable.destroy;
                                        case MISS:
                                            idRes = R.drawable.miss;
                                            break;
                                    }
                                }
                                imageViews[i][j].setSoundEffectsEnabled(false);
                                onTouchListener(imageViews, i, j, grid);
                                onClickListener(imageViews, i, j, grid);
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
                            imageViews[i][j].setTag(idRes);
                            gridLayout.addView(imageViews[i][j]);
                        }
                    }
                }
            });
        }

        private void saveLengthLayout(int width, int height) {
            System.out.println(width + " | " + height);
            xtarget = width;
            ytarget = height;
            firstLengthLayout = false;
        }

        private void onTouchListener(final ImageView[][] imageViews, int i, int j, final Grid grid){
            final int finalI = i;
            final int finalJ = j;
            imageViews[i][j].setOnTouchListener(new View.OnTouchListener() {
                int fI = finalI;
                int fJ = finalJ;
                int[][] cellCross;
                float[] coordsCurrent = new float[2];

                boolean xTravelM = false;
                boolean xTravelP = false;
                boolean yTravelM = false;
                boolean yTravelP = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (playerTurn && !gameOver) {
                        Grid.Cell cell = grid.getCells()[finalI][finalJ];
                        if (cell == Grid.Cell.EMPTY || cell == Grid.Cell.SHIP) {
                            cellCross = touchCross(fI, fJ);
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    for (int k = 0; k < cellCross[0].length; k++) {
                                        if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_miss);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_hit);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_destroy);
                                        }
                                    }
                                    coordsCurrent[0] = event.getX();
                                    coordsCurrent[1] = event.getY();

                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    if (fJ == 0 && event.getX() < coordsCurrent[0]) {
                                        float diff = genereDiff(event.getX() % xtarget, HORIZONTAL);
                                        float diffCurrent = genereDiff(coordsCurrent[0] % xtarget, HORIZONTAL);
                                        if (diff < diffCurrent) {
                                            coordsCurrent[0] = event.getX();
                                        }
                                    } else if (fJ > 0 && event.getX() < coordsCurrent[0]) {
                                        float diff = genereDiff(event.getX() % xtarget, HORIZONTAL);
                                        float diffCurrent = genereDiff(coordsCurrent[0] % xtarget, HORIZONTAL);
                                        if (diff < diffCurrent) {
                                            xTravelM = true;
                                            xTravelP = false;
                                        } else {
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.water);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.destroy);
                                                }
                                            }
                                            if (xTravelM) {
                                                fJ--;
                                                xTravelM = false;
                                                xTravelP = false;
                                            }
                                            cellCross = touchCross(fI, fJ);
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_destroy);
                                                }
                                            }
                                        }
                                        coordsCurrent[0] = event.getX();
                                    } else if (fJ < LENGTH_BOARD - 1 && event.getX() > coordsCurrent[0]) {
                                        float diff = genereDiff(event.getX() % xtarget, HORIZONTAL);
                                        float diffCurrent = genereDiff(coordsCurrent[0] % xtarget, HORIZONTAL);
                                        if (diff > diffCurrent) {
                                            xTravelP = true;
                                            xTravelM = false;
                                        } else {
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.water);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.destroy);
                                                }
                                            }
                                            if (xTravelP) {
                                                fJ++;
                                                xTravelM = false;
                                                xTravelM = false;
                                            }
                                            cellCross = touchCross(fI, fJ);
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_destroy);
                                                }
                                            }
                                        }
                                        coordsCurrent[0] = event.getX();
                                    } else if (fJ == LENGTH_BOARD - 1 && event.getX() > coordsCurrent[0]) {
                                        float diff = genereDiff(event.getX() % xtarget, HORIZONTAL);
                                        float diffCurrent = genereDiff(coordsCurrent[0] % xtarget, HORIZONTAL);
                                        if (diff > diffCurrent) {
                                            coordsCurrent[0] = event.getX();
                                        }
                                    }


                                    if (fI == 0 && event.getY() < coordsCurrent[1]) {
                                        float diff = genereDiff(event.getY() % ytarget, VERTICAL);
                                        float diffCurrent = genereDiff(coordsCurrent[1] % ytarget, VERTICAL);
                                        if (diff < diffCurrent) {
                                            coordsCurrent[1] = event.getY();
                                        }
                                    } else if (fI > 0 && event.getY() < coordsCurrent[1]) {
                                        float diff = genereDiff(event.getY() % ytarget, VERTICAL);
                                        float diffCurrent = genereDiff(coordsCurrent[1] % ytarget, VERTICAL);
                                        if (diff < diffCurrent) {
                                            yTravelM = true;
                                            yTravelP = false;
                                        } else {
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.water);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.destroy);
                                                }
                                            }
                                            if (yTravelM) {
                                                fI--;
                                                yTravelM = false;
                                                yTravelP = false;
                                            }
                                            cellCross = touchCross(fI, fJ);
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_destroy);
                                                }
                                            }
                                        }
                                        coordsCurrent[1] = event.getY();
                                    } else if (fI < LENGTH_BOARD - 1 && event.getY() > coordsCurrent[1]) {
                                        float diff = genereDiff(event.getY() % ytarget, VERTICAL);
                                        float diffCurrent = genereDiff(coordsCurrent[1] % ytarget, VERTICAL);
                                        if (diff > diffCurrent) {
                                            yTravelP = true;
                                            yTravelM = false;
                                        } else {
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.water);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.destroy);
                                                }
                                            }
                                            if (yTravelP) {
                                                fI++;
                                                yTravelM = false;
                                                yTravelM = false;
                                            }
                                            cellCross = touchCross(fI, fJ);
                                            for (int k = 0; k < cellCross[0].length; k++) {
                                                if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                        grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_miss);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_hit);
                                                } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                                    imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.target_destroy);
                                                }
                                            }
                                        }
                                        coordsCurrent[1] = event.getY();
                                    } else if (fI == LENGTH_BOARD - 1 && event.getY() > coordsCurrent[1]) {
                                        float diff = genereDiff(event.getY() % ytarget, VERTICAL);
                                        float diffCurrent = genereDiff(coordsCurrent[1] % ytarget, VERTICAL);
                                        if (diff > diffCurrent) {
                                            coordsCurrent[1] = event.getY();
                                        }
                                    }
                                    break;

                                case MotionEvent.ACTION_UP:
                                    for (int k = 0; k < cellCross[0].length; k++) {
                                        if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.EMPTY ||
                                                grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.SHIP) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.water);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.MISS) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.miss);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.HIT) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.hit);
                                        } else if (grid.getCells()[cellCross[0][k]][cellCross[1][k]] == Grid.Cell.DESTROY) {
                                            imageViews[cellCross[0][k]][cellCross[1][k]].setBackgroundResource(R.drawable.destroy);
                                        }
                                    }
                                    if (grid.getCells()[fI][fJ] == Grid.Cell.EMPTY || grid.getCells()[fI][fJ] == Grid.Cell.SHIP) {
                                        modifGrille(imageViews, fI, fJ, grid);
                                    }
                                    return true;
                            }//end switch
                        }//end cell empty/ship
                    }
                    return false;
                }
            });
        }

        private float genereDiff(float v, int orientation) {
            while(v < 0){
                v += (orientation == HORIZONTAL) ? xtarget : ytarget;
            }
            return v;
        }

        private int[][] touchCross(int x, int y) {
            int[][] cellCross = new int[2][(2 * LENGTH_BOARD) - 1];
            int cpt = 0;

            for(int i = 0; i < LENGTH_BOARD; i++){
                cellCross[0][cpt] = i;
                cellCross[1][cpt] = y;
                cpt++;
            }

            for(int j = 0; j < y; j++){
                cellCross[0][cpt] = x;
                cellCross[1][cpt] = j;
                cpt++;
            }

            for(int j = y + 1; j < LENGTH_BOARD; j++){
                cellCross[0][cpt] = x;
                cellCross[1][cpt] = j;
                cpt++;
            }
            return cellCross;
        }


        private void onClickListener(final ImageView[][] imageViews, int i, int j, final Grid grid){
            final int finalI = i;
            final int finalJ = j;
            imageViews[i][j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerTurn && !gameOver) {
                        modifGrille(imageViews, finalI, finalJ, grid);
                    }
                }
            });
        }

        private void modifGrille(ImageView[][] imageViews, final int finalI, final int finalJ, Grid grid) {
            Grid.Cell cell = grid.getCells()[finalI][finalJ];
            switch (cell) {
                case EMPTY:
                    playerTurn = false;
                    imageViews[finalI][finalJ].setBackgroundResource(R.drawable.miss);
                    imageViews[finalI][finalJ].setTag(R.drawable.miss);
                    grid.setCellAt(finalI, finalJ, Grid.Cell.MISS);
                    playSound(SOUND_INDEX_MISS);
                    break;
                case SHIP:
                    imageViews[finalI][finalJ].setBackgroundResource(R.drawable.hit);
                    imageViews[finalI][finalJ].setTag(R.drawable.hit);
                    grid.setCellAt(finalI, finalJ, Grid.Cell.HIT);
                    if (enemyGrid.isShipDestroyed(finalI, finalJ)){
                        playSound(SOUND_INDEX_DESTROY);
                        changeShipFlowHuman(imageViews, grid, finalI, finalJ);

                        if(difficulty.equals(IA_EASY) && !gameOver) {
                            changeAroundShipFlowHuman(imageViews, grid, finalI, finalJ, 10);
                        }
                    }
                    else
                        playSound(SOUND_INDEX_HIT);
                    break;
                default:
                    break;
            }
        }

        private void changeShipFlowHuman(View[][] imageViews, Grid grid, int x, int y) {
            int[][] shipFlow = enemyGrid.shipFlow(x, y);
            if(shipFlow != null) {
                for (int sf = 0; sf < shipFlow[0].length; sf++) {
                    imageViews[shipFlow[0][sf]][shipFlow[1][sf]].setBackgroundResource(R.drawable.destroy);
                    imageViews[shipFlow[0][sf]][shipFlow[1][sf]].setTag(R.drawable.destroy);
                    grid.setCellAt(shipFlow[0][sf], shipFlow[1][sf], Grid.Cell.DESTROY);
                }
            }
        }

        private void changeShipFlowComputer(int x, int y) {
            int[][] shipFlow = playerGrid.shipFlow(x, y);
            if(shipFlow != null) {
                for (int sf = 0; sf < shipFlow[0].length; sf++) {
                    imageViewsPlayer[shipFlow[0][sf]][shipFlow[1][sf]].setBackgroundResource(R.drawable.destroy);
                    imageViewsPlayer[shipFlow[0][sf]][shipFlow[1][sf]].setTag(R.drawable.destroy);
                    enemyGrid.setCellAt(shipFlow[0][sf], shipFlow[1][sf], Grid.Cell.DESTROY);
                }
            }
        }

        private void changeAroundShipFlowHuman(View[][] imageViews, Grid grid, int x, int y, int gridLength){
            int[][] shipFlowAround = enemyGrid.shipFlowAround(x, y, gridLength);
            if(shipFlowAround != null) {
                for (int sfa = 0; sfa < shipFlowAround[0].length; sfa++) {
                    if (shipFlowAround[0][sfa] != -1 && shipFlowAround[1][sfa] != -1) {
                        Grid.Cell cellTest = grid.getCells()[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]];
                        switch (cellTest) {
                            case EMPTY:
                                imageViews[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]].setBackgroundResource(R.drawable.miss);
                                imageViews[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]].setTag(R.drawable.miss);
                                grid.setCellAt(shipFlowAround[0][sfa], shipFlowAround[1][sfa], Grid.Cell.MISS);
                                break;
                            default:
                        }
                    }
                }
            }
        }

        private void changeAroundShipFlowComputer(int x, int y, int gridLength){
            int[][] shipFlowAround = playerGrid.shipFlowAround(x, y, gridLength);
            if(shipFlowAround != null) {
                for (int sfa = 0; sfa < shipFlowAround[0].length; sfa++) {
                     if (shipFlowAround[0][sfa] != -1 && shipFlowAround[1][sfa] != -1) {
                        Grid.Cell cellTest = playerGrid.getCells()[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]];
                        switch (cellTest) {
                            case EMPTY:
                                imageViewsPlayer[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]].setBackgroundResource(R.drawable.miss);
                                imageViewsPlayer[shipFlowAround[0][sfa]][shipFlowAround[1][sfa]].setTag(R.drawable.miss);
                                playerGrid.setCellAt(shipFlowAround[0][sfa], shipFlowAround[1][sfa], Grid.Cell.MISS);
                                break;
                            default:
                        }
                    }
                }
            }
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

        private void computerPlay() {
            int[] coords = computer.computePlay(playerGrid);
            final int finalX = coords[0];
            final int finalY = coords[1];
            final int idRes  = coords[2];
            if(playerGrid.getCells()[finalX][finalY] == Grid.Cell.MISS)
                playerTurn = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageViewsPlayer[finalX][finalY].setBackgroundResource(idRes);
                    imageViewsPlayer[finalX][finalY].setTag(idRes);
                    switch (idRes) {
                        case R.drawable.miss:
                            playSound(SOUND_INDEX_MISS);
                            break;
                        case R.drawable.hit:
                            if (playerGrid.isShipDestroyed(finalX, finalY)){
                                changeShipFlowComputer(finalX, finalY);
                                if(difficulty.equals(IA_EASY))
                                    changeAroundShipFlowComputer(finalX, finalY, 10);
                                playSound(SOUND_INDEX_DESTROY);
                            }
                            else
                                playSound(SOUND_INDEX_HIT);
                            break;
                    }
                }
            });
        }
    }
