package audio.rabid.dev.wallpapersetter;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class WallpaperSetService extends IntentService {

    WallpaperGetter wallpaperGetter = new WallpaperGetter(this);

    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

    public static final String ACTION_CHANGE_FLICKR_WALLPAPER = "ACTION_CHANGE_FLICKR_WALLPAPER";

    public WallpaperSetService() {
        super("WallpaperSetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action){
                case ACTION_CHANGE_FLICKR_WALLPAPER:
                    setNewFlickrWallpaper();
                    break;

                //TODO music case
            }
        }
    }

    private void setAlbumArtWallpaper(String artist, String album){
        wallpaperGetter.getAlbumArt(artist, album, new WallpaperGetter.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                try{
                    wallpaperManager.setBitmap(bitmap);
                }catch (Exception e){
                    onException(e);
                }
            }
        });
    }

    private void setNewFlickrWallpaper(){
        wallpaperGetter.getRandomFickrImage(new WallpaperGetter.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                try{
                    wallpaperManager.setBitmap(bitmap);
                }catch (Exception e){
                    onException(e);
                }
            }
        });
    }

    private void restoreFlickrWallpaper(){
        try {
            Bitmap b = wallpaperGetter.getLastFlickrWallpaper();
            wallpaperManager.setBitmap(b);
        }catch (Exception e){
            onException(e);
        }
    }

    private void onException(final Exception e){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.e(WallpaperSetService.class.getSimpleName(), "Problem setting wallpaper", e);
                Toast.makeText(WallpaperSetService.this, "Problem Setting Wallpaper", Toast.LENGTH_LONG).show();
            }
        });
    }
}
