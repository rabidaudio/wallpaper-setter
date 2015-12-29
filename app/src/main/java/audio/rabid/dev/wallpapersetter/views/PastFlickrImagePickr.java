package audio.rabid.dev.wallpapersetter.views;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import audio.rabid.dev.wallpapersetter.R;
import audio.rabid.dev.wallpapersetter.WallpaperGetter;
import audio.rabid.dev.wallpapersetter.WallpaperSetService;

public class PastFlickrImagePickr extends AppCompatActivity {

    WallpaperGetter wallpaperGetter;
    GridView imageHolder;

//    LruCache<String, Bitmap> bitmaps = new LruCache<>(50);

    Map<String, Bitmap> bitmaps = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_flickr_image_pickr);

        imageHolder = (GridView) findViewById(R.id.imageHolder);

        wallpaperGetter = new WallpaperGetter(this);

        updateHolder();
    }

    private void updateHolder(){
        Map<String, DiskLruCache.Snapshot> snapshots = wallpaperGetter.getAllStoredFlickrImages();
        List<String> includedKeys = new ArrayList<>(bitmaps.keySet());
        for(Map.Entry<String, DiskLruCache.Snapshot> s : snapshots.entrySet()){
            if(includedKeys.contains(s.getKey())){
                //we've already got this one
                includedKeys.remove(s.getKey());
            }else{
                //load this bitmap
                bitmaps.put(s.getKey(), BitmapFactory.decodeStream(s.getValue().getInputStream(0)));
            }
        }
        for(String key : includedKeys){
            //any keys that were in the old list but not the new list should be cleared from the map
            bitmaps.remove(key);
        }
        imageHolder.setAdapter(new ImageListAdapter(new ArrayList<>(bitmaps.keySet())));
    }

    private void setWallpaper(String key){
        WallpaperSetService.setPastBackground(this, key);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //avoid OOM errors by explicitly freeing bitmaps
        bitmaps.clear();
        imageHolder.setAdapter(null);
    }

    /*
    private Uri addToGallery(String name, InputStream is){
        // add to gallery
//                MediaStore.Images.Media.insertImage(getContentResolver(), result.getAbsolutePath(), result.getName(), null);
        ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "My Drone picture");
//                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "My Drone picture");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, copyToSDCard(name, is));
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private String copyToSDCard(String name, InputStream is) {
        File sdcard = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ File.separator +"FlickrWallpapers");
        if(sdcard.exists() || sdcard.mkdirs()){
            try {
                File sdcard1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "FlickrWallpapers", name);
                Utils.pipe(is, new FileOutputStream(sdcard1));
                return sdcard1.getAbsolutePath();
            }catch (IOException e){
                //oops
            }
        }
        return null;
    }
    */

    public class ImageListAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;

        public ImageListAdapter(List<String> keys) {
            super(PastFlickrImagePickr.this, R.layout.listview_item_image, keys);
            inflater = LayoutInflater.from(PastFlickrImagePickr.this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
                final String key = getItem(position);
                Bitmap b = bitmaps.get(key);
                if(b == null) return convertView;
                ((ImageView) convertView).setImageBitmap(b);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWallpaper(key);
                    }
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        new AlertDialog.Builder(getContext())
                                .setItems(new String[]{
                                        "Set Wallpaper",
                                        "Share",
                                        "Delete"
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case 0:
                                                setWallpaper(key);
                                                break;
                                            case 1:
                                                /*
//                                                Uri local = addToGallery(key, snapshots.get(key).getInputStream(0));
//                                                File f = new File(getCacheDir(),"temp.png");
                                                try {
                                                    Utils.pipe(snapshots.get(key).getInputStream(0), openFileOutput("share.png", MODE_PRIVATE));
                                                }catch (IOException e){
                                                    throw new RuntimeException(e);
                                                }
                                                Uri local = Uri.fromFile(getFileStreamPath("share.png"));
                                                Intent i = new Intent(Intent.ACTION_SEND);
                                                i.setType("image/jpeg");
                                                i.putExtra(Intent.EXTRA_STREAM, local);
                                                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(Intent.createChooser(i, "Share Image"));
                                                */

                                                break;
                                            case 2:
                                                wallpaperGetter.removeFlickrImageByKey(key);
                                                updateHolder();
                                                break;
                                        }
                                    }
                                })
                                .create()
                                .show();
                        return true;
                    }
                });
            }
            return convertView;
        }
    }
}
