package audio.rabid.dev.wallpapersetter;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import audio.rabid.dev.wallpapersetter.views.WallpaperPreferenceFragment;

public class WallpaperSetService extends IntentService implements WallpaperGetter.BitmapCallback {

    WallpaperGetter wallpaperGetter;
    WallpaperManager wallpaperManager;
    SharedPreferences preferences;

    protected static final String ACTION_CHANGE_FLICKR_WALLPAPER = "ACTION_CHANGE_FLICKR_WALLPAPER";
    protected static final String ACTION_RESTORE_CURRENT_FLICKR_WALLPAPER = "ACTION_RESTORE_CURRENT_FLICKR_WALLPAPER";
    protected static final String ACTION_RESTORE_PAST_FLICKR_WALLPAPER = "ACTION_RESTORE_PAST_FLICKR_WALLPAPER";
    protected static final String ACTION_SET_ALBUM_ART = "ACTION_SET_ALBUM_ART";
    protected static final String ACTION_SET_WALLPAPER_ALARM = "ACTION_SET_WALLPAPER_ALARM";


    protected static final String EXTRA_ARTIST = "EXTRA_ARTIST";
    protected static final String EXTRA_ALBUM = "EXTRA_ALBUM";
    protected static final String EXTRA_KEY = "EXTRA_KEY";

    public WallpaperSetService() {
        super("WallpaperSetService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //initialize things with context
        wallpaperGetter = new WallpaperGetter(this);
        wallpaperManager = WallpaperManager.getInstance(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static void changeArt(Context context, String artist, String album){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(ACTION_SET_ALBUM_ART);
        i.putExtra(EXTRA_ARTIST, artist);
        i.putExtra(EXTRA_ALBUM, album);
        context.startService(i);
    }

    public static void restoreBackground(Context context){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_RESTORE_CURRENT_FLICKR_WALLPAPER);
        context.startService(i);
    }

    public static void setNewBackground(Context context){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);
        context.startService(i);
    }

    public static void setPastBackground(Context context, String key){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_RESTORE_PAST_FLICKR_WALLPAPER);
        i.putExtra(EXTRA_KEY, key);
        context.startService(i);
    }

    public static void setWallpaperAlarm(Context context){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_SET_WALLPAPER_ALARM);
        context.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action){

                case ACTION_CHANGE_FLICKR_WALLPAPER:
                    setNewFlickrWallpaper();
                    break;

                case ACTION_RESTORE_CURRENT_FLICKR_WALLPAPER:
                    restoreFlickrWallpaper();
                    break;

                case ACTION_RESTORE_PAST_FLICKR_WALLPAPER:
                    String key = intent.getStringExtra(EXTRA_KEY);
                    setPastFlickrWallpaper(key);
                    break;

                case ACTION_SET_ALBUM_ART:
                    String artist = intent.getStringExtra(EXTRA_ARTIST);
                    String album = intent.getStringExtra(EXTRA_ALBUM);
                    setAlbumArtWallpaper(artist, album);
                    break;

                case ACTION_SET_WALLPAPER_ALARM:
                    setWallpaperAlarm();
                    break;
            }
        }
    }

    private void setAlbumArtWallpaper(final String artist, final String album){
        if(!preferences.getBoolean(WallpaperPreferenceFragment.PREF_ALBUM_ART_ENABLED, true)){
            return;
        }
        Log.d("Wallpaper", "getting album wallpaper for "+artist+" - "+album);
        wallpaperGetter.getAlbumArt(artist, album, this);
    }

    private void setNewFlickrWallpaper(){
        if(!preferences.getBoolean(WallpaperPreferenceFragment.PREF_FLICKR_ENABLED, true)){
            return;
        }
        Log.d("Wallpaper", "getting new flickr wallpaper");
        wallpaperGetter.getRandomFickrImage(this);
    }

    private void restoreFlickrWallpaper(){
        try {
            Bitmap b = wallpaperGetter.getLastFlickrWallpaper();
            if(b == null){
                setNewFlickrWallpaper();
            }else {
                Log.d("Wallpaper", "setting wallpaper");
                wallpaperManager.setBitmap(b);
            }
        }catch (Exception e){
            onException(e);
        }
    }

    private void setPastFlickrWallpaper(String key){
        try {
            Bitmap b = wallpaperGetter.getPastFlickrWallpaper(key);
            if(b != null){
                Log.d("Wallpaper", "setting wallpaper");
                wallpaperManager.setBitmap(b);
            }
        }catch (Exception e){
            onException(e);
        }
    }

    @Override
    public void onBitmap(Bitmap bitmap) {
        Log.d("Wallpaper", "setting wallpaper");
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (Exception e) {
            onException(e);
            restoreFlickrWallpaper();
        }
    }

    @Override
    public void onException(final Exception e){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.e("Wallpaper", "Problem setting wallpaper", e);
                Toast.makeText(WallpaperSetService.this, "Problem Setting Wallpaper", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setWallpaperAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(this, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        if(!preferences.getBoolean(WallpaperPreferenceFragment.PREF_FLICKR_ENABLED, true)){
            return;
        }

        String intervalString = preferences.getString(WallpaperPreferenceFragment.PREF_INTERVAL, "INTERVAL_DAY");
        long interval = intervalCode(intervalString);
        String[] startTime = preferences.getString(WallpaperPreferenceFragment.PREF_START, "08:00").split(":");
        int startHour = Integer.valueOf(startTime[0]);
        int startMinute = Integer.valueOf(startTime[1]);

        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        Log.d("Wallpaper", "current: "+currentTime+" setting: "+calendar.getTime().getTime() +" diff "+(calendar.getTime().getTime()-currentTime));

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);

        Log.i("Wallpaper", "Setting wallpaper alarm for "+startHour+":"+startMinute+" and every "+intervalString);
    }

    private static long intervalCode(String intervalString){
        switch (intervalString){
            case "INTERVAL_DAY":
                return AlarmManager.INTERVAL_DAY;
            case "INTERVAL_HALF_DAY":
                return AlarmManager.INTERVAL_HALF_DAY;
            case "INTERVAL_HOUR":
                return AlarmManager.INTERVAL_HOUR;
            case "INTERVAL_HALF_HOUR":
                return AlarmManager.INTERVAL_HALF_HOUR;
            case "INTERVAL_FIFTEEN_MINUTES":
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            default:
                return 0;
        }
    }
}
