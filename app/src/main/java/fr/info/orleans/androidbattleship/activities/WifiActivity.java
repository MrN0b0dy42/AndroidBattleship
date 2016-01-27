package fr.info.orleans.androidbattleship.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import java.util.Collection;


import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.WiFiDirectBroadcastReceiver;

public class WifiActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;


    WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Toast.makeText(WifiActivity.this, "Discover peers successfully.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int reason) {
            Toast.makeText(WifiActivity.this, "Discover peers failed.", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), this);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        mManager.discoverPeers(mChannel,actionListener);

        WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Collection<WifiP2pDevice> wifiDevices = peers.getDeviceList();
                Integer size = Integer.valueOf(wifiDevices.size());
                Log.d("debug wifi direct", "there are " + size.toString() + " wifi devices");
            }
        };

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(actionListener)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
              mManager.requestPeers(mChannel, myPeerListListener);
            }
        }


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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}


