package fr.info.orleans.androidbattleship;

import android.app.Application;

import fr.info.orleans.androidbattleship.model.Player;

/**
 * Created by Ludo on 03/01/2016.
 */
public class AndroidBattleship extends Application {
    private Player connectedPlayer = null;

    public Player getConnectedPlayer() {
        return connectedPlayer;
    }

    public void setConnectedPlayer(Player connectedPlayer) {
        this.connectedPlayer = connectedPlayer;
    }
}
