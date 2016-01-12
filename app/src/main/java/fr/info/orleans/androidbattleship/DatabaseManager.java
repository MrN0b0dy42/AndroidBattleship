package fr.info.orleans.androidbattleship;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import fr.info.orleans.androidbattleship.model.Player;

/**
 * Created by Ludo on 09/12/2015.
 */
public class DatabaseManager extends SQLiteOpenHelper{

    // database version
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "AndroidBattleship";

    //PLAYER TABLE
    public static final String PLAYER_KEY = "idPlayer";
    public static final String PLAYER_FIRSTNAME = "firstname";
    public static final String PLAYER_LASTNAME = "lastname";
    public static final String PLAYER_LOGIN = "login";
    public static final String PLAYER_PASSWORD = "password";
    public static final String PLAYER_TABLE_NAME = "Player";
    public static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + PLAYER_TABLE_NAME + " (" +
                    PLAYER_KEY + " INTEGER PRIMARY KEY  NOT NULL, " +
                    PLAYER_FIRSTNAME + " TEXT NOT NULL CHECK(length(firstname) > 0), " +
                    PLAYER_LASTNAME + " TEXT NOT NULL CHECK(length(lastname) > 0), " +
                    PLAYER_LOGIN + " TEXT NOT NULL CHECK(length(login) > 0), "+
                    PLAYER_PASSWORD + " TEXT NOT NULL CHECK(length(password) > 0) );";

    public static final String PLAYER_TABLE_DROP = "DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME + ";";

    private static final String[] COLUMNS_PLAYER = { PLAYER_KEY, PLAYER_FIRSTNAME, PLAYER_LASTNAME , PLAYER_LOGIN , PLAYER_PASSWORD };




    //VERSUS TABLE
    public static final String VERSUS_ID_WINNER = "idWinner";
    public static final String VERSUS_ID_LOOSER = "idLooser";
    public static final String VERSUS_DATE = "gameDate";
    public static final String VERSUS_TABLE_NAME = "Versus";
    public static final String VERSUS_TABLE_CREATE =
            "CREATE TABLE " + VERSUS_TABLE_NAME + " (" +
                    VERSUS_ID_WINNER + " INTEGER NOT NULL, " +
                    VERSUS_ID_LOOSER + " INTEGER NOT NULL, " +
                    VERSUS_DATE + " DATETIME NOT NULL , " +
                    "PRIMARY KEY ("+ VERSUS_ID_WINNER +","+ VERSUS_ID_LOOSER +") );";

    public static final String VERSUS_TABLE_DROP = "DROP TABLE IF EXISTS " + VERSUS_TABLE_NAME + ";";

    private static final String[] COLUMNS_VERSUS = { VERSUS_ID_WINNER, VERSUS_ID_LOOSER, VERSUS_DATE};

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("LOGCAT","DB Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create player table
        db.execSQL(PLAYER_TABLE_CREATE);
        db.execSQL(VERSUS_TABLE_CREATE);
        Log.d("LOGCAT", "Table Player created");
        Log.d("LOGCAT", "Table Versus created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PLAYER_TABLE_DROP);
        db.execSQL(VERSUS_TABLE_DROP);
        this.onCreate(db);
    }

    public void insertPlayer(Player p) throws Exception{
        // get reference of the database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(PLAYER_FIRSTNAME,p.getFirstname());
        values.put(PLAYER_LASTNAME, p.getLastname());
        values.put(PLAYER_LOGIN,p.getLogin());
        values.put(PLAYER_PASSWORD, p.getPassword());

        // insert player
        p.setIdPlayer((int) db.insert(PLAYER_TABLE_NAME, null, values));
        if(p.getIdPlayer() == -1) throw new Exception();


        // close db transaction
        db.close();
    }

    public Player selectPlayerById(int id){
        // get reference of the database
        SQLiteDatabase db = this.getWritableDatabase();

        // get player query
        Cursor cursor = db.query(PLAYER_TABLE_NAME,
                COLUMNS_PLAYER, " idPlayer = ?", new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Player p = new Player();
        p.setIdPlayer(cursor.getInt(0));
        p.setFirstname(cursor.getString(1));
        p.setLastname(cursor.getString(2));
        p.setLogin(cursor.getString(3));
        p.setPassword(cursor.getString(4));

        return p;
    }

    public List getAllPlayers(){
        List players = new LinkedList();
        String query = "SELECT * FROM " + PLAYER_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results

        Player p = null;

        if (cursor.moveToFirst()) {
            do {
                p = new Player();
                p.setIdPlayer(cursor.getInt(0));
                p.setFirstname(cursor.getString(1));
                p.setLastname(cursor.getString(2));
                p.setLogin(cursor.getString(3));
                p.setPassword(cursor.getString(4));

                players.add(p);
            } while (cursor.moveToNext());
        }
        return players;
    }

    public int updatePlayer(Player p) {
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put("firstname", p.getFirstname());
        values.put("lastname", p.getLastname());
        values.put("login",p.getLogin());
        values.put("password",p.getPassword());

        // update
        int i = db.update(PLAYER_TABLE_NAME, values, PLAYER_KEY + " = ?", new String[] { String.valueOf(p.getIdPlayer()) });

        db.close();
        return i;
    }

    public void deletePlayer(Player p) {

        SQLiteDatabase db = this.getWritableDatabase();
        // delete player
        db.delete(PLAYER_TABLE_NAME, PLAYER_KEY + " = ?", new String[] { String.valueOf(p.getIdPlayer()) });
        db.close();
    }

    public List selectPlayerByQuery(String query){
        List players = new LinkedList();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results

        Player p = null;

        if (cursor.moveToFirst()) {
            do {
                p = new Player();
                p.setIdPlayer(cursor.getInt(0));
                p.setFirstname(cursor.getString(1));
                p.setLastname(cursor.getString(2));
                p.setLogin(cursor.getString(3));
                p.setPassword(cursor.getString(4));

                players.add(p);
            } while (cursor.moveToNext());
        }
        return players;
    }
}