package audio.rabid.dev.wallpapersetter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fixdapp.lib.ByteArray;
import com.jakewharton.disklrucache.DiskLruCache;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import audio.rabid.dev.wallpapersetter.views.WallpaperPreferenceFragment;

/**
 * Created by Charles Julian Knight, julian@fixdapp.com on 9/11/15.
 */
public class WallpaperGetter {

    private static final String TAG = WallpaperGetter.class.getSimpleName();

    private static final Uri SERVER_URL = Uri.parse("https://random-flickr-image.herokuapp.com");

    protected static final String PREF_CURRENT_FLICKR_WALLPAPER_KEY = "PREF_CURRENT_FLICKR_WALLPAPER_KEY";
    protected static final String PREF_ALL_FLICKR_WALLPAPER_KEYS = "PREF_ALL_FLICKR_WALLPAPER_KEYS";

    private SharedPreferences preferences;

    DiskLruCache imageCache;

    private int opacity;
    private String artSize;

    public WallpaperGetter(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int cacheSize = (int) Double.parseDouble(preferences.getString(WallpaperPreferenceFragment.PREF_MAX_CACHE_SIZE, "50.0"))*1024*1024;

        opacity = preferences.getInt(WallpaperPreferenceFragment.PREF_OPACITY, 30);
        artSize = preferences.getString(WallpaperPreferenceFragment.PREF_RESOLUTION, null);

        try{
            imageCache = DiskLruCache.open(context.getCacheDir(), BuildConfig.VERSION_CODE, 1, cacheSize);
        }catch (IOException e){
            throw new RuntimeException("Unable to set up image cache", e);
        }
    }

    public void getRandomFickrImage(final BitmapCallback callback){
        Uri flickr = Uri.withAppendedPath(SERVER_URL, "").buildUpon()
                .appendQueryParameter("opacity", String.valueOf(opacity))
                .build();

        Log.d(TAG, "Downloading file "+flickr.toString());

        AsyncHttpClient.getDefaultInstance().executeByteBufferList(new AsyncHttpGet(flickr), new AsyncHttpClient.DownloadCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, ByteBufferList result) {
                if(e != null){
                    callback.onException(e);
                    return;
                }
                try {
                    String key = "flickr_"+String.valueOf(System.currentTimeMillis());
                    DiskLruCache.Editor editor = imageCache.edit(key);
                    OutputStream os = editor.newOutputStream(0);
                    while (result.hasRemaining()) {
                        os.write(result.getAllByteArray());
                    }
                    os.close();
                    editor.commit();
                    addFlickrKey(key);
                    callback.onBitmap(BitmapFactory.decodeStream(imageCache.get(key).getInputStream(0)));
                }catch (IOException e2){
                    callback.onException(e2);
                }
            }
        });
    }

    public void getAlbumArt(String artist, String album, final BitmapCallback callback){
        try {
            final String key = formatImageKey(String.format("%s%s%d%s", artist, album, opacity, artSize));
            DiskLruCache.Snapshot s = imageCache.get(key);

            if(s != null && s.getLength(0) > 0) {
                callback.onBitmap(BitmapFactory.decodeStream(s.getInputStream(0)));
            }else{

                Uri.Builder builder = Uri.withAppendedPath(SERVER_URL, "albumart").buildUpon()
                        .appendQueryParameter("opacity", String.valueOf(opacity))
                        .appendQueryParameter("artist", artist)
                        .appendQueryParameter("album", album);
                if(artSize != null && !"default".equals(artSize)){
                    builder.appendQueryParameter("size", artSize);
                }
                Uri albumArt = builder.build();

                Log.d(TAG, "Downloading file "+albumArt.toString());

                AsyncHttpClient.getDefaultInstance().executeByteBufferList(new AsyncHttpGet(albumArt), new AsyncHttpClient.DownloadCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, ByteBufferList result) {
                        if(e != null){
                            callback.onException(e);
                            return;
                        }
                        try {
                            DiskLruCache.Editor editor = imageCache.edit(key);
                            OutputStream os = editor.newOutputStream(0);
                            while (result.hasRemaining()) {
                                os.write(result.getAllByteArray());
                            }
                            os.close();
                            editor.commit();
                            callback.onBitmap(BitmapFactory.decodeStream(imageCache.get(key).getInputStream(0)));
                        }catch (IOException e2){
                            callback.onException(e2);
                        }
                    }
                });
            }

        }catch (IOException e){
            callback.onException(e);
        }
    }

    public Bitmap getLastFlickrWallpaper(){
        String key = preferences.getString(PREF_CURRENT_FLICKR_WALLPAPER_KEY, null);
        return getPastFlickrWallpaper(key);
    }

    public Bitmap getPastFlickrWallpaper(String key){
        try {
            if(key==null) return null;
            DiskLruCache.Snapshot s = imageCache.get(key);
            if(s == null) return null;
            return BitmapFactory.decodeStream(s.getInputStream(0));
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, DiskLruCache.Snapshot> getAllStoredFlickrImages(){
        Set<String> allKeys = preferences.getStringSet(PREF_ALL_FLICKR_WALLPAPER_KEYS, new HashSet<String>(0));
        Map<String, DiskLruCache.Snapshot> snapshots = new HashMap<>(allKeys.size());
        for(String key : allKeys){
            DiskLruCache.Snapshot s;
            try {
                s = imageCache.get(key);
            }catch (IOException e){
                s = null;
            }
            if(s != null && s.getLength(0) > 0){
                snapshots.put(key, s);
            }else{
                removeFlickrImageByKey(key);
            }
        }
        return snapshots;
    }

    private void addFlickrKey(String key){
        Set<String> allKeys = preferences.getStringSet(PREF_ALL_FLICKR_WALLPAPER_KEYS, new HashSet<String>(0));
        allKeys.add(key);
        preferences.edit()
                .putString(PREF_CURRENT_FLICKR_WALLPAPER_KEY, key)
                .putStringSet(PREF_ALL_FLICKR_WALLPAPER_KEYS, allKeys)
                .apply();
    }

    public void removeFlickrImageByKey(String key){
        try {
            DiskLruCache.Snapshot s = imageCache.get(key);
            if (s != null) {
                imageCache.remove(key);
            }
        }catch (IOException e){

        }
        Set<String> allKeys = preferences.getStringSet(PREF_ALL_FLICKR_WALLPAPER_KEYS, new HashSet<String>(0));
        allKeys.remove(key);
        preferences.edit()
                .putStringSet(PREF_ALL_FLICKR_WALLPAPER_KEYS, allKeys)
                .apply();
    }

    public void clearCache(){
        try {
            imageCache.delete();
        }catch (IOException e){

        }
    }

    private static String formatImageKey(String unformatted){
        String formatted =  base32Encode(unformatted.getBytes());
        return (formatted.length() > 64 ? formatted.substring(0, 64) : formatted);
    }

    public interface BitmapCallback {
        void onBitmap(Bitmap bitmap);
        void onException(Exception e);
    }

    private static final char[] BASE_32_CHARS = "abcdefghijklmnopqrstuvwxyz012345".toCharArray();

    private static String base32Encode(byte[] data){
        ByteArray ba = new ByteArray(data);
        StringBuilder sb = new StringBuilder();
        int totalBits = 8*ba.size();
        int position = 0;
        while (position < totalBits){
            int bitsToRead = Math.min(totalBits-position, 5);
            int val = ba.getBits(position, position+bitsToRead-1);
            sb.append(BASE_32_CHARS[val]);
            position += bitsToRead;
        }
        return sb.toString();
    }
}
