package audio.rabid.dev.wallpapersetter;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by  charles  on 12/25/15.
 */
public class WallpaperPreferenceFragment extends PreferenceFragment {

    protected static final String PREF_INTERVAL = "PREF_INTERVAL";
    protected static final String PREF_START = "PREF_START";

    protected static final String PREF_OPACITY = "PREF_OPACITY";
    protected static final String PREF_RESOLUTION = "PREF_RESOLUTION";

    //TODO check enabled
    protected static final String PREF_FLICKR_ENABLED = "PREF_FLICKR_ENABLED";
    protected static final String PREF_ALBUM_ART_ENABLED = "PREF_ALBUM_ART_ENABLED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
