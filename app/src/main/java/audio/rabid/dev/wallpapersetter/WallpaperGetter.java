package audio.rabid.dev.wallpapersetter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import java.io.File;

/**
 * Created by Charles Julian Knight, julian@fixdapp.com on 9/11/15.
 */
public class WallpaperGetter {

    private static final String TAG = WallpaperGetter.class.getSimpleName();

    private static final Uri SERVER_URL = Uri.parse("https://random-flickr-image.herokuapp.com");

    private static final int OPACITY = 30;

    private static final String ART_SIZE = "mega";

    private static final String PREF_WALLPAPER_LOCATION = WallpaperGetter.class.getCanonicalName()+".PREF_WALLPAPER_LOCATION";

    private SharedPreferences preferences;

    private File flickrFile, albumartFile;

    public WallpaperGetter(Context context){
        preferences = context.getSharedPreferences(WallpaperGetter.class.getSimpleName(), Context.MODE_PRIVATE);

        flickrFile = new File(context.getCacheDir(), "flickr.jpg");
        albumartFile = new File(context.getCacheDir(), "albumart.jpg");
    }

    public void getRandomFickrImage(final BitmapCallback callback){
        Uri flickr = Uri.withAppendedPath(SERVER_URL, "").buildUpon()
                .appendQueryParameter("opacity", String.valueOf(OPACITY))
                .build();

        Log.d(TAG, "Downloading file "+flickr.toString());

        AsyncHttpClient.getDefaultInstance().executeFile(new AsyncHttpGet(flickr), flickrFile.getAbsolutePath(), new AsyncHttpClient.FileCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, File result) {
                preferences.edit().putString(PREF_WALLPAPER_LOCATION, result.getAbsolutePath()).apply();
                callback.onBitmap(BitmapFactory.decodeFile(result.getAbsolutePath()));
            }
        });
    }

    public void getAlbumArt(String artist, String album, final BitmapCallback callback){
        Uri albumArt = Uri.withAppendedPath(SERVER_URL, "albumart").buildUpon()
                .appendQueryParameter("opacity", String.valueOf(OPACITY))
                .appendQueryParameter("artist", artist)
                .appendQueryParameter("album", album)
                .appendQueryParameter("size", ART_SIZE)
                .build();

        Log.d(TAG, "Downloading file "+albumArt.toString());

        AsyncHttpClient.getDefaultInstance().executeFile(new AsyncHttpGet(albumArt), albumartFile.getAbsolutePath(), new AsyncHttpClient.FileCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, File result) {
                callback.onBitmap(BitmapFactory.decodeFile(result.getAbsolutePath()));
            }
        });
    }

    public Bitmap getLastFlickrWallpaper(){
        String path = preferences.getString(PREF_WALLPAPER_LOCATION, null);
        if(path==null) return null;
        return BitmapFactory.decodeFile(path);
    }

    public interface BitmapCallback {
        void onBitmap(Bitmap bitmap);
    }
}
