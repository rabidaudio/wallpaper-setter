package audio.rabid.dev.wallpapersetter;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class WallpaperSetService extends IntentService {

    private static final String TAG = WallpaperSetService.class.getSimpleName();

    WallpaperGetter wallpaperGetter;
    WallpaperManager wallpaperManager;

    protected static final String ACTION_CHANGE_FLICKR_WALLPAPER = "ACTION_CHANGE_FLICKR_WALLPAPER";
    protected static final String ACTION_RESTORE_FLICKR_WALLPAPER = "ACTION_RESTORE_FLICKR_WALLPAPER";
    protected static final String ACTION_SET_ALBUM_ART = "ACTION_SET_ALBUM_ART";

    private static final String EXTRA_ARTIST = "EXTRA_ARTIST";
    private static final String EXTRA_ALBUM = "EXTRA_ALBUM";

    public WallpaperSetService() {
        super("WallpaperSetService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //initialize things with context
        wallpaperGetter = new WallpaperGetter(this);
        wallpaperManager = WallpaperManager.getInstance(this);
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
        i.setAction(WallpaperSetService.ACTION_RESTORE_FLICKR_WALLPAPER);
        context.startService(i);
    }

    public static void setNewBackground(Context context){
        Intent i = new Intent(context, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);
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

                case ACTION_RESTORE_FLICKR_WALLPAPER:
                    restoreFlickrWallpaper();
                    break;

                case ACTION_SET_ALBUM_ART:
                    String artist = intent.getStringExtra(EXTRA_ARTIST);
                    String album = intent.getStringExtra(EXTRA_ALBUM);
                    setAlbumArtWallpaper(artist, album);
                    break;

                //TODO music case
            }
        }
    }

    private void setAlbumArtWallpaper(final String artist, final String album){
        Log.d(TAG, "getting album wallpaper for "+artist+" - "+album);

        wallpaperGetter.getAlbumArt(artist, album, new WallpaperGetter.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                try{
                    Log.d(TAG, "setting wallpaper: " + bitmap.toString());
                    wallpaperManager.setBitmap(bitmap);
                }catch (Exception e){
                    onException(e);
                }
            }
        });
    }

    private void setNewFlickrWallpaper(){
        Log.d(TAG, "getting new flickr wallpaper");
        wallpaperGetter.getRandomFickrImage(new WallpaperGetter.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                Log.d(TAG, "setting wallpaper: " + bitmap.toString());
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (Exception e) {
                    onException(e);
                }
            }
        });
    }

    private void restoreFlickrWallpaper(){
        try {
            Bitmap b = wallpaperGetter.getLastFlickrWallpaper();
            Log.d(TAG, "setting wallpaper: " + b.toString());
            wallpaperManager.setBitmap(b);
        }catch (Exception e){
            onException(e);
        }
    }

    private void onException(final Exception e){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "Problem setting wallpaper", e);
                Toast.makeText(WallpaperSetService.this, "Problem Setting Wallpaper", Toast.LENGTH_LONG).show();
            }
        });
    }
}
