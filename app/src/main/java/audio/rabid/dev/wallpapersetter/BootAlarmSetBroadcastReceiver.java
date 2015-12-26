package audio.rabid.dev.wallpapersetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootAlarmSetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                && preferences.getBoolean(WallpaperPreferenceFragment.PREF_FLICKR_ENABLED, true)){

            WallpaperSetService.setWallpaperAlarm(context);
        }
    }
}
