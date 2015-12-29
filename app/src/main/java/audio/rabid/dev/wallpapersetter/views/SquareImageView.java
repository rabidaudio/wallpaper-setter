package audio.rabid.dev.wallpapersetter.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by  charles  on 12/28/15.
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        super.setImageBitmap(squarifyBitmap(bitmap));
    }

    private static Bitmap squarifyBitmap(Bitmap source){
        int width = source.getWidth();
        int height = source.getHeight();
        int minSize = Math.min(width, height);
        int startX = source.getWidth()/2 - minSize/2;
        int startY = source.getHeight()/2 - minSize/2;
        return Bitmap.createBitmap(source, startX, startY, minSize, minSize);
    }
}
