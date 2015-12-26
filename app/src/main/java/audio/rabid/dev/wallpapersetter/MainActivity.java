package audio.rabid.dev.wallpapersetter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new WallpaperPreferenceFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.fetch_new_wallpaper);

        if(BuildConfig.DEBUG) {
            menu.add(R.string.set_album_art);
            menu.add(R.string.restore_background);
            menu.add(R.string.set_change_alarm);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CharSequence i = item.getTitle();
        if (i.equals(getString(R.string.fetch_new_wallpaper))) {
            WallpaperSetService.setNewBackground(this);
        } else if (i.equals(getString(R.string.restore_background))) {
            WallpaperSetService.restoreBackground(this);
        } else if (i.equals(getString(R.string.set_album_art))) {
            WallpaperSetService.changeArt(this, "Wye Oak", "Shriek");
        } else if (i.equals(getString(R.string.set_change_alarm))) {
            WallpaperSetService.setWallpaperAlarm(this);
        } else {
            return false;
        }
        return true;
    }

    /*
        albums = []
        # albums.push({ artist: "Wye%20Oak", album: "Shriek" })
        albums.push({ artist: "Jay-Z", album: "The%20Black%20Album" })

        albums.each do |a|
            (0..100).each do |opacity|

                url = "https://random-flickr-image.herokuapp.com/albumart?artist=#{a[:artist]}&album=#{a[:album]}&opacity=#{opacity}"

                `wget -O '#{a[:artist]}_#{a[:album]}_#{opacity}.png' '#{url}'`
            end
        end
     */
}
