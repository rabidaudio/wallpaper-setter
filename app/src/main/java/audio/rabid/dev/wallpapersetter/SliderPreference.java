package audio.rabid.dev.wallpapersetter;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by  charles  on 12/26/15.
 */
public class SliderPreference extends DialogPreference {

    private int value = 0;
    private int maxValue = 10;
    private SeekBar seekBar;


    public SliderPreference(Context con, AttributeSet attrs) {
        super(con, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

        for(int i=0; i<attrs.getAttributeCount(); i++){
            if(attrs.getAttributeNameResource(i) == R.attr.slider_preference_max){
                maxValue = attrs.getAttributeIntValue(i, 10);
            }
        }
    }

    protected SeekBar getSeekBar(){
        return seekBar;
    }

    @Override
    protected View onCreateDialogView() {
        seekBar = new SeekBar(getContext());

        return seekBar;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        seekBar.setMax(maxValue);
        seekBar.setProgress(value);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            value = seekBar.getProgress();

            if (callChangeListener(value)) {
                persistInt(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getInt(index, 0));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int def = (defaultValue == null ? 0 : (Integer) defaultValue);
        if(restoreValue){
            value = getPersistedInt(def);
        }else{
            value = def;
            if(shouldPersist()){
                persistInt(def);
            }
        }
    }

}
