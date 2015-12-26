package audio.rabid.dev.wallpapersetter.views;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import audio.rabid.dev.wallpapersetter.R;
import audio.rabid.dev.wallpapersetter.Utils;
import audio.rabid.dev.wallpapersetter.WallpaperGetter;
import audio.rabid.dev.wallpapersetter.WallpaperSetService;

public class PastFlickrImagePickr extends AppCompatActivity {

    WallpaperGetter wallpaperGetter;

    GridView imageHolder;

//    Map<String, DiskLruCache.Snapshot> snapshots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_flickr_image_pickr);

        imageHolder = (GridView) findViewById(R.id.imageHolder);

        wallpaperGetter = new WallpaperGetter(this);

        imageHolder.setAdapter(new ImageListAdapter(this, wallpaperGetter.getAllStoredFlickrImages()));

//        snapshots = ;
//        viewKeys = new HashMap<>(snapshots.size());

//        for(Map.Entry<String, DiskLruCache.Snapshot> snapshot : snapshots.entrySet()){
//            ImageView iv = new ImageView(this);
//            InputStream is = snapshot.getValue().getInputStream(0);
//            Bitmap bm = BitmapFactory.decodeStream(is);
//            iv.setImageBitmap(bm);
//            iv.setOnClickListener(this);
//            iv.setOnLongClickListener(this);
//            viewKeys.put(iv, snapshot.getKey());
//            imageHolder.addView(iv);
//        }
    }

    private void setWallpaper(String key){
        WallpaperSetService.setPastBackground(this, key);
    }

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


    public class ImageListAdapter extends ArrayAdapter<String> {
        private Context context;
        private LayoutInflater inflater;

        private Map<String, DiskLruCache.Snapshot> snapshots;

        public ImageListAdapter(Context context, Map<String, DiskLruCache.Snapshot> snapshots) {
            super(context, R.layout.listview_item_image, snapshots.keySet().toArray(new String[snapshots.keySet().size()]));

            this.context = context;
            this.snapshots = snapshots;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
                final String key = getItem(position);
                DiskLruCache.Snapshot s = snapshots.get(key);
                InputStream is =  s.getInputStream(0);
                ((ImageView) convertView).setImageBitmap(BitmapFactory.decodeStream(is));
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
                                                Uri local = addToGallery(key, snapshots.get(key).getInputStream(0));
                                                Intent i = new Intent(Intent.ACTION_SEND);
                                                i.setType("image/jpeg");
                                                i.putExtra(Intent.EXTRA_STREAM, local);
                                                startActivity(Intent.createChooser(i, "Share Image"));
                                                break;
                                            case 2:
                                                wallpaperGetter.removeFlickrImageByKey(key);
                                                v.setVisibility(View.GONE); //TODO animate
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

//
//            Bitmap bm = BitmapFactory.decodeStream(is);
//            iv.setImageBitmap(bm);
//            iv.setOnClickListener(this);
//            iv.setOnLongClickListener(this);
//            viewKeys.put(iv, snapshot.getKey());


            return convertView;
        }
    }
}
