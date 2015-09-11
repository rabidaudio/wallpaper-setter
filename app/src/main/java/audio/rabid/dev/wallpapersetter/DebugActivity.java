package audio.rabid.dev.wallpapersetter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        findViewById(R.id.changeBackground).setOnClickListener(this);
        findViewById(R.id.restoreFlickr).setOnClickListener(this);
        findViewById(R.id.setAlbumArt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, WallpaperSetService.class);

        switch (v.getId()){
            case R.id.changeBackground:
                i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);
                break;
            
            case R.id.restoreFlickr:
                i.setAction(WallpaperSetService.ACTION_RESTORE_FLICKR_WALLPAPER);
                break;

            case R.id.setAlbumArt:
                i.setAction(WallpaperSetService.ACTION_SET_ALBUM_ART);
                i.putExtra(WallpaperSetService.EXTRA_ARTIST, "Wye Oak");
                i.putExtra(WallpaperSetService.EXTRA_ALBUM, "Shriek");
                break;
        }

        startService(i);
    }
}
