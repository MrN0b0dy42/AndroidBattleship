package fr.info.orleans.androidbattleship.activities;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Player;

public class SignInActivity extends Activity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextLastname, editTextFirstname, editTextLogin, editTextPassword, editTextPasswordConfirmation;
    DatabaseManager db = new DatabaseManager(this);
    List players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        // drop this database if already exists
        db.onUpgrade(db.getWritableDatabase(), 1, 2);

        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        editTextFirstname = (EditText) findViewById(R.id.editText_firstname);
        editTextLastname = (EditText) findViewById(R.id.editText_lastname);
        editTextLogin = (EditText) findViewById(R.id.editText_login);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextPasswordConfirmation = (EditText) findViewById(R.id.editText_password_confirmation);
        buttonSignIn.setOnClickListener(this);

        //test
        db.createPlayer(new Player(1,"toto","titi","toto77","toto77"));
        db.createPlayer(new Player(2,"tutu","tyty","tutu77","toto77"));
        db.createPlayer(new Player(3,"Jessica","Réaume","Shrek","toto77"));
        db.createPlayer(new Player(4,"Guillaume","Rénault","JollyJumper","toto77"));
        db.createPlayer(new Player(5,"Mélanie","Garnier","melgar","toto77"));

        editTextLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Player p = null;
                if(!hasFocus){
                    players = db.getAllPlayers();
                    for(int i = 0 ; i < players.size() ; i++){
                        p = (Player) players.get(i);
                        if(p.getLogin().equals(editTextLogin.getText().toString())){
                            Log.d("DEBUG","login already used !");
                        }
                    }
                }
            }
        });



    }

    @Override
    public void onClick(View v) {


    }
}
