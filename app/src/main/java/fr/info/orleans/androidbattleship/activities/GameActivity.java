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

    private ArrayList<Coordinate>  grid2,grid3,grid4,grid5, hunt;
    private ArrayList<Integer> remainingBoat;
    private int state, changeState;
    private boolean initOk;


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
        initOk=false;
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
        else if (difficulty.equals("hard")){
            if(!initOk) {
                initIAHard();
            }
            ArrayList<Coordinate> gridCurrent;
            changeStatus();
            gridCurrent=selectGrid(this.state);
            int x;
            int y;
            Coordinate currentCoord=new Coordinate();
            do {
            switch (this.hunt.size()) {
                case 0:
                    currentCoord = randomCase(gridCurrent);
                    break;
                case 1:
                    currentCoord= selectCase(this.hunt.get(0));
                    break;
                case 2:case 3: case 4:
                    currentCoord=selectSpecificCase();
                    if(changeState!=0){
                        changeState--;
                    }
                    break;
            }
            x = currentCoord.getX();
            y = currentCoord.getY();
            } while (!notYetShot(x, y));
            removeOne(currentCoord);

            Grid.Cell cell = playerGrid.getCells()[x][y];
            final int idRes;

            if (cell == Grid.Cell.EMPTY) {
                idRes = R.drawable.miss;
                playerGrid.setCellAt(x, y, Grid.Cell.MISS);
                if(this.hunt.size()>1){
                    changeState=2;
                }
                playerTurn = true;
            } else {
                idRes = R.drawable.hit;
                this.hunt.add(currentCoord);
                playerGrid.setCellAt(x, y, Grid.Cell.HIT);
                if (playerGrid.isShipDestroyed(x, y)){
                    ArrayList<Integer> delete= minmax(hunt);
                    removeMany(delete.get(0),delete.get(1),delete.get(2),delete.get(3));
                    changeRemainingBoat(this.hunt);
                    changeState=0;
                    this.hunt=new ArrayList();
                }
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

    private void initIAHard(){
        grid2=new ArrayList<>();
        grid3=new ArrayList<>();
        grid4=new ArrayList<>();
        grid5=new ArrayList<>();

        for(int i=0; i<Grid.SIZE;i++){
                for(int j=0; j<Grid.SIZE; j++){
                    if ((i+j)%2==1){
                        Coordinate c = new Coordinate(i,j);
                        grid2.add(c);
                    }
                    if ((i+j)%3==1){
                        Coordinate c = new Coordinate(i,j);
                        grid3.add(c);
                    }
                    if ((i+j)%4==1) {
                        Coordinate c = new Coordinate(i, j);
                        grid4.add(c);
                    }
                    if ((i+j)%5==1){
                        Coordinate c = new Coordinate(i,j);
                        grid5.add(c);
                    }

                }
        }
        this.hunt=new ArrayList();
        this.state=2;
        this.remainingBoat=new ArrayList();
        initRemainingBoat();
        this.changeState = 0;
        initOk=true;

    }

    private Coordinate randomCase(ArrayList<Coordinate> coord){
        int size = coord.size();
        Random r = new Random();
        int i = (r.nextInt(size));
        return coord.get(i);
    }

    private void initRemainingBoat(){
        for(int i=0;i<4;i++){
            if(i==3){
                this.remainingBoat.add(1);
            }
            else this.remainingBoat.add(2);
        }

    }

    private void removeOne(Coordinate c){
        if(this.grid2.contains(c)){
            grid2.remove(c);
        }
        if(this.grid3.contains(c)){
            grid3.remove(c);
        }
        if(this.grid4.contains(c)){
            grid4.remove(c);
        }
        if(this.grid5.contains(c)){
            grid5.remove(c);
        }
    }

    private ArrayList<Integer> minmax (ArrayList<Coordinate> List){
        int minx=10;
        int maxx=-1;
        int miny = 10;
        int maxy=-1;
        ArrayList<Integer> sol = new ArrayList();
        for(int i=0; i<List.size();i++){
            Coordinate c = List.get(i);
            if (c.getX()<minx){
                minx=c.getX();
            }
            if (c.getX()>maxx){
                maxx=c.getX();
            }
            if (c.getY()<miny){
                miny=c.getY();
            }
            if (c.getY()<maxy){
                maxy=c.getY();
            }
        }
        sol.add(minx);
        sol.add(maxx);
        sol.add(miny);
        sol.add(maxy);
        return sol;
    }

    private void removeMany(int mini, int maxi, int minj, int maxj){
        if(mini==maxi){
            for(int i=mini-1 ;i<maxi+2;i++){
                for(int j= minj-1; j<maxj+2; j++){
                    Coordinate c = new Coordinate(i,j);
                    removeOne(c);
                }
            }
        }
    }

    private int difference(int x1,int x2){
         return x2-x1;
    }

    public void changeStatus(){
        boolean stop=false;
        int i=0;
        while(!stop){
            if(remainingBoat.get(i).equals(0)){
                i++;
            }else stop=true;
        }
        this.state=i+2;
    }

    public void changeRemainingBoat(ArrayList<Coordinate> array){
        int x = array.size();
        this.remainingBoat.set(x-2,this.remainingBoat.get(x-2)-1);
        this.hunt=new ArrayList();
    }

    private ArrayList<Coordinate> selectGrid(int state){
        switch (state) {
            case 2:
                return grid2;
            case 3:
                return grid3;
            case 4:
                return grid4;
            case 5:
                return grid5;
            default:
                return grid2;
        }
    }

    private Coordinate selectCase(Coordinate c) {
        Coordinate newCoord = new Coordinate();
        ArrayList<Integer> possibility = new ArrayList();
        if (c.getY()+1<Grid.SIZE && notYetShot(c.getX(), c.getY()+1)) {
            if(c.getY()<10){
                possibility.add(0);
            }
        }
        if (c.getX()+1<Grid.SIZE && notYetShot(c.getX()+1, c.getY())) {
            if(c.getX()<10) {
                possibility.add(1);
            }
        }
        if (c.getY()-1>=0 && notYetShot(c.getX(), c.getY()-1)) {
            if(c.getY()>=0) {
                possibility.add(2);
            }
        }
        if (c.getX()-1>=0 && notYetShot(c.getX()-1, c.getY())) {
            if(c.getX()>=0) {
                possibility.add(3);
            }
        }
        Random r = new Random();
        int i = (r.nextInt(possibility.size()));
        int x = possibility.get(i);
        switch(x) {
            case 0:
                newCoord = new Coordinate(c.getX(), c.getY()+1);
                break;
            case 1:
                newCoord = new Coordinate(c.getX()+1, c.getY());
                break;
            case 2:
                newCoord = new Coordinate(c.getX(), c.getY()-1);
                break;
            case 3:
                newCoord = new Coordinate(c.getX()-1, c.getY());
                break;
        }
        return newCoord;
    }

    private Coordinate selectSpecificCase(){
        Coordinate c=new Coordinate();
        if(changeState==0){
            int x=difference(this.hunt.get(hunt.size() - 2).getX(), this.hunt.get(hunt.size() - 1).getX());
            int y=difference(this.hunt.get(hunt.size()-2).getY(),this.hunt.get(hunt.size()-1).getY());
            c= new Coordinate(this.hunt.get(hunt.size() - 1).getX()+x,this.hunt.get(hunt.size()-1).getY()+y);
            if(!notYetShot(c.getX(), c.getY())){
                changeState=2;
                return selectSpecificCase();
            }
            else if(c.getX()<0 || c.getX()>9){
                changeState=2;
                return selectSpecificCase();
            }
            else if(c.getY()<0 || c.getY()>9){
                changeState=2;
                return selectSpecificCase();
            }
        }
        else if(changeState==1){
            int x=difference(this.hunt.get(hunt.size()-1).getX(), this.hunt.get(0).getX());
            int y = difference(this.hunt.get(hunt.size() - 1).getY(), this.hunt.get(0).getY());
            c= new Coordinate(this.hunt.get(hunt.size()-1).getX()-x,this.hunt.get(hunt.size()-1).getY()-y);
            if(!notYetShot(c.getX(), c.getY())){
                changeState=2;
            }
        }
        else if(changeState==2){
            int x=difference(this.hunt.get(0).getX(), this.hunt.get(1).getX());
            int y=difference(this.hunt.get(0).getY(),this.hunt.get(1).getY());
            c= new Coordinate(this.hunt.get(0).getX()-x,this.hunt.get(0).getY()-y);

        }
        return c;
    }



}
