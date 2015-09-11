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
        switch (v.getId()){
            case R.id.changeBackground:
                WallpaperSetService.setNewBackground(this);
                break;

            case R.id.restoreFlickr:
                WallpaperSetService.restoreBackground(this);
                break;

            case R.id.setAlbumArt:
                WallpaperSetService.changeArt(this, "Wye Oak", "Shriek");
                break;
        }
    }
}
