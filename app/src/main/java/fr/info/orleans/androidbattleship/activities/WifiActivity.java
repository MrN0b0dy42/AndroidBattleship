package fr.info.orleans.androidbattleship.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.WiFiDirectBroadcastReceiver;

public class WifiActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {
//    ArrayAdapter<String> listAdapter;
//    ListView listView;
//    Set<WifiP2pDevice> devicesArray;
//    ArrayList<String> pairedDevices;
//    ArrayList<WifiP2pDevice> devices;


    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), this);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        new MenuInflater(this).inflate(R.menu.menu, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.registerWfdReceiver:
                mReceiver = new WiFiDirectBroadcastReceiver(mManager,mChannel,this);
                //mReceiver.registerReceiver();
                break;
            case R.id.unregisterWfdReceiver:
                break;
        }


        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    private void onDiscover() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WifiActivity.this, "Discover peers successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiActivity.this, "Discover peers failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onChannelDisconnected() {
        Toast.makeText(WifiActivity.this, "WiFi Direct channel disconnected - Reinitializing", Toast.LENGTH_SHORT).show();
        reinitializeChannel();
    }

    private void reinitializeChannel() {
        mChannel = mManager.initialize(this,getMainLooper(),this);
        if(mChannel != null){
            Toast.makeText(WifiActivity.this, "WiFi Direct channel initialization: Success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(WifiActivity.this, "WiFi Direct channel initialization: Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
