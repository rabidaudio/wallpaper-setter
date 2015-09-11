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
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, WallpaperSetService.class);
        i.setAction(WallpaperSetService.ACTION_CHANGE_FLICKR_WALLPAPER);
        startService(i);
    }
}
