package audio.rabid.dev.wallpapersetter.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;

import audio.rabid.dev.wallpapersetter.R;

/**
 * Created by  charles  on 12/26/15.
 */
public class ImageCompareSliderPreference extends SliderPreference implements OnSeekBarChangeListener {

    ImageView left, right;

    public ImageCompareSliderPreference(Context con, AttributeSet attrs) {
        super(con, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        View parent = super.onCreateDialogView();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        ViewGroup v = (ViewGroup) layoutInflater.inflate(R.layout.dialog_fragment_image_comparer_preference, null);
        v.addView(parent);
        left = (ImageView) v.findViewById(R.id.imageLeft);
        right = (ImageView) v.findViewById(R.id.imageRight);
        getSeekBar().setOnSeekBarChangeListener(this);
        return v;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            left.setImageBitmap(loadLeftFromAssets(getContext(), progress));
            right.setImageBitmap(loadRightFromAssets(getContext(), progress));
        }catch (IOException e){
            Log.e("Wallpaper", "Problem loading asset", e);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static Bitmap loadLeftFromAssets(Context context, int opacity) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open("samples/wye/Wye%20Oak_Shriek_"+String.valueOf(opacity)+".png"));
    }

    private static Bitmap loadRightFromAssets(Context context, int opacity) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open("samples/jay/Jay-Z_The%20Black%20Album_"+String.valueOf(opacity)+".png"));
    }
}
