package fr.info.orleans.androidbattleship.model;

import java.io.Serializable;

/**
 * Created by Ludo on 09/12/2015.
 */
public class Player{
    private int idPlayer;
    private String firstname;
    private String lastname;
    private String login;
    private String password;

    public Player(){};

    public Player(String firstname, String lastname, String login, String password) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
        this.login = login;
        this.password = password;
    }

    public void setIdPlayer(int idPlayer){
        this.idPlayer = idPlayer;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override

    public String toString() {
        return "Player [idPlayer=" + idPlayer + ", firstname=" + firstname + ", lastname=" + lastname + ", login=" + login + ", password=" + password + "]";
    }

}