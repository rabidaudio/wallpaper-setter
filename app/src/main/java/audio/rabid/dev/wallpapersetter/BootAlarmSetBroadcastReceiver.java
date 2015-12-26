package audio.rabid.dev.wallpapersetter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class BootAlarmSetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(context, WallpaperSetService.class);
            i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);

            // Set the alarm to start at approximately 8:00 a.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(context, 0, i, 0));

            Log.i("Wallpaper", "Setting wallpaper alarm");
            if(BuildConfig.DEBUG){
                Toast.makeText(context, "Setting wallpaper alarm", Toast.LENGTH_LONG).show();
            }
        }
    }
}
