package audio.rabid.dev.wallpapersetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MusicPlaybackBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MusicPlaybackBroadcastReceiver.class.getSimpleName();

    private static final String PREF_LAST_KNOWN_PLAY = "PREF_LAST_KNOWN_PLAY";
    private static final String PREF_LAST_PLAYING = "PREF_LAST_PLAYING";

    @Override
    public void onReceive(Context context, Intent intent) {

        logMusicIntent(intent);

        boolean playing = intent.getBooleanExtra("playing", false);
        String artist = intent.getStringExtra("artist");
        String album = intent.getStringExtra("album");

        String albumId = artist+"-"+album;

        if(artist==null || album==null) return;

        SharedPreferences preferences = context.getSharedPreferences(MusicPlaybackBroadcastReceiver.class.getSimpleName(), Context.MODE_PRIVATE);

        boolean lastPlaying = preferences.getBoolean(PREF_LAST_PLAYING, false);
        String lastKnownPlay = preferences.getString(PREF_LAST_KNOWN_PLAY, "");

        preferences.edit()
                .putBoolean(PREF_LAST_PLAYING, playing)
                .putString(PREF_LAST_KNOWN_PLAY, albumId)
                .apply();

        if(playing){ //playing now?
            if(lastPlaying){ //was playing last time?
                if(lastKnownPlay.equals(albumId)){ //same song?
                    Log.d(TAG, "Current play is same album as last time. No update.");
                }else{
                    Log.d(TAG, "Current play different than last time. Updating");
                    WallpaperSetService.changeArt(context, artist, album);
                }
            }else{
                Log.d(TAG, "Playing now and wasn't before. Updating");
                WallpaperSetService.changeArt(context, artist, album);
            }
        }else {
            if(lastPlaying){
                Log.d(TAG, "Playing has stopped. Restoring");
                WallpaperSetService.restoreBackground(context);
            }else{
                Log.d(TAG, "Wasn't playing and still isn't. No update.");
            }
        }
    }

    private static void logMusicIntent(Intent intent){
        Log.d("MusicIntent", "Caught intent: " + intent.toString());
        String action = intent.getAction();
        String type = action.substring(action.lastIndexOf('.') + 1);
        String sender = action.substring(0, action.lastIndexOf('.'));
        Log.d("MusicIntent", "type: "+type +" from app: "+sender);
        Bundle b = intent.getExtras();
        for(String key : b.keySet()) Log.d("MusicIntent", "EXTRA: "+key+ " : "+b.get(key));
        Log.d("MusicIntent", "-----");
    }
}

//com.spotify.music
//fm.last.android
//com.htc.music
//com.android.music
// playbackstatechanged, metachanged

/*
09-11 19:21:57.416    8357-8357/audio.rabid.dev.wallpapersetter I/Timeline﹕ Timeline: Activity_idle id: android.os.BinderProxy@27285a3e time:8062824
09-11 19:22:20.520    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4721
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Plan
09-11 19:22:20.525    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:22:21.004    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:22:21.004    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:22:21.004    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:22:21.005    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4721
09-11 19:22:21.005    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:22:21.005    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Plan
09-11 19:22:21.005    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:22:21.081    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:22:21.081    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:22:21.081    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:22:21.081    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4721
09-11 19:22:21.081    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:22:21.082    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Plan
09-11 19:22:21.082    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:22:21.181    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:22:21.181    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:22:21.182    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:22:21.182    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4721
09-11 19:22:21.182    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:22:21.182    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Plan
09-11 19:22:21.182    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:22:58.765    8357-8367/audio.rabid.dev.wallpapersetter W/art﹕ Suspending all threads took: 9.508ms
09-11 19:23:08.781    8357-8367/audio.rabid.dev.wallpapersetter W/art﹕ Suspending all threads took: 6.499ms
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4722
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : Center of the Universe
09-11 19:23:35.112    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:23:45.798    8357-8367/audio.rabid.dev.wallpapersetter W/art﹕ Suspending all threads took: 14.684ms
09-11 19:23:59.109    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.android.music.metachanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:23:59.109    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Built to Spill
09-11 19:23:59.110    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:23:59.110    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : 4722
09-11 19:23:59.110    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Keep It Like a Secret
09-11 19:23:59.110    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : Center of the Universe
09-11 19:23:59.110    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:24:22.283    8357-8371/audio.rabid.dev.wallpapersetter I/art﹕ WaitForGcToComplete blocked for 22.252ms for cause HomogeneousSpaceCompact
09-11 19:24:28.037    8357-8372/audio.rabid.dev.wallpapersetter W/art﹕ Suspending all threads took: 24.881ms
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013868174
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 198438
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : false
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Felix Jaehn
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 198438
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:3rq6UmctkAq33Jik9FqtyU
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 59227
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:2wZFAsXLnIdo7fba55EQq1
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Book Of Love
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : Book Of Love
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 59227
09-11 19:24:28.200    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:25.187    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:25.187    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013925128
09-11 19:25:25.187    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 198438
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : false
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : Felix Jaehn
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 198438
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:3rq6UmctkAq33Jik9FqtyU
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : false
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 58732
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:2wZFAsXLnIdo7fba55EQq1
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : Book Of Love
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : Book Of Love
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 58732
09-11 19:25:25.188    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:25.486    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013925384
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 162000
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : true
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : The Magnetic Fields
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 162000
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:2js3lkzAjWpD656NK7ZaJX
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 0
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:6UGpcXcENaUqQKPc6oqOe4
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : 69 Love Songs
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Book Of Love
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 0
09-11 19:25:25.611    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:26.062    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:26.062    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013925802
09-11 19:25:26.062    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 162413
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : true
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : The Magnetic Fields
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 162413
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:2js3lkzAjWpD656NK7ZaJX
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 743
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:6UGpcXcENaUqQKPc6oqOe4
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : 69 Love Songs
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Book Of Love
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 743
09-11 19:25:26.063    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:26.072    8357-8367/audio.rabid.dev.wallpapersetter W/art﹕ Suspending all threads took: 17.443ms
09-11 19:25:26.189    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013925926
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 162413
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : true
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : The Magnetic Fields
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 162413
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:2js3lkzAjWpD656NK7ZaJX
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 591
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:6UGpcXcENaUqQKPc6oqOe4
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : 69 Love Songs
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Book Of Love
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 591
09-11 19:25:26.202    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:27.113    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:27.121    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013926931
09-11 19:25:27.122    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 162413
09-11 19:25:27.122    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : true
09-11 19:25:27.122    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : The Magnetic Fields
09-11 19:25:27.122    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 162413
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:2js3lkzAjWpD656NK7ZaJX
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 1730
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:6UGpcXcENaUqQKPc6oqOe4
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : 69 Love Songs
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Book Of Love
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 1730
09-11 19:25:27.123    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
09-11 19:25:27.396    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ Caught intent: Intent { act=com.spotify.music.playbackstatechanged flg=0x10 cmp=audio.rabid.dev.wallpapersetter/.MusicPlaybackBroadcastReceiver (has extras) }
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: timeSent : 1442013927371
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: duration : 162413
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playstate : true
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: artist : The Magnetic Fields
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: length : 162413
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: albumId : spotify:album:2js3lkzAjWpD656NK7ZaJX
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playing : true
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: playbackPosition : 2097
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: id : spotify:track:6UGpcXcENaUqQKPc6oqOe4
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: album : 69 Love Songs
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: track : The Book Of Love
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ EXTRA: position : 2097
09-11 19:25:27.397    8357-8357/audio.rabid.dev.wallpapersetter D/zz﹕ -----
*/
