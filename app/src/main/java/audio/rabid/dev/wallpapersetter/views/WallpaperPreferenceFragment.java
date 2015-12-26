package audio.rabid.dev.wallpapersetter.views;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import audio.rabid.dev.wallpapersetter.R;

/**
 * Created by  charles  on 12/25/15.
 */
public class WallpaperPreferenceFragment extends PreferenceFragment {

    public static final String PREF_INTERVAL = "PREF_INTERVAL";
    public static final String PREF_START = "PREF_START";

    public static final String PREF_OPACITY = "PREF_OPACITY";
    public static final String PREF_RESOLUTION = "PREF_RESOLUTION";

    public static final String PREF_FLICKR_ENABLED = "PREF_FLICKR_ENABLED";
    public static final String PREF_ALBUM_ART_ENABLED = "PREF_ALBUM_ART_ENABLED";

    public static final String PREF_MAX_CACHE_SIZE = "PREF_MAX_CACHE_SIZE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
