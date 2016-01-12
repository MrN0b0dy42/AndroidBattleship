package fr.info.orleans.androidbattleship.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.info.orleans.androidbattleship.AndroidBattleship;
import fr.info.orleans.androidbattleship.DatabaseManager;
import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Player;

public class ManageAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSaveChanges;
    private EditText editTextFirstname , editTextLastname, editTextLogin, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        buttonSaveChanges = (Button) findViewById(R.id.buttonSaveChanges);
        editTextFirstname = (EditText) findViewById(R.id.editText_firstname);
        editTextLastname = (EditText) findViewById(R.id.editText_lastname);
        editTextLogin = (EditText) findViewById(R.id.editText_login);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        buttonSaveChanges.setOnClickListener(this);
        Player connectedPlayer = ((AndroidBattleship) this.getApplication()).getConnectedPlayer();

        editTextFirstname.setText(connectedPlayer.getFirstname());
        editTextLastname.setText(connectedPlayer.getLastname());
        editTextLogin.setText(connectedPlayer.getLogin());
        editTextPassword.setText(connectedPlayer.getPassword());
    }

    @Override
    public void onClick(View v) {
        Player connectedPlayer = ((AndroidBattleship) this.getApplication()).getConnectedPlayer();
        connectedPlayer.setFirstname(editTextFirstname.getText().toString());
        connectedPlayer.setLastname(editTextLastname.getText().toString());
        connectedPlayer.setLogin(editTextLogin.getText().toString());
        connectedPlayer.setPassword(editTextPassword.getText().toString());
        DatabaseManager db = new DatabaseManager(this);
        db.getReadableDatabase();
        try{
            db.updatePlayer(connectedPlayer);
            Toast.makeText(ManageAccountActivity.this, "Account updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(ManageAccountActivity.this, "Update error", Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }

    }
}
