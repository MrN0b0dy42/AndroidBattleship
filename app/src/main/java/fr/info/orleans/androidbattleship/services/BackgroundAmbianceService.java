package fr.info.orleans.androidbattleship.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import fr.info.orleans.androidbattleship.R;

public class BackgroundAmbianceService extends Service {

    private static final int MAX_VOLUME = 100;
    private static final int VOLUME = 75;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.sea_ambiance);
        mediaPlayer.setLooping(true);
        final float volume = (float) (1 - (Math.log(MAX_VOLUME - VOLUME) / Math.log(MAX_VOLUME)));
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
