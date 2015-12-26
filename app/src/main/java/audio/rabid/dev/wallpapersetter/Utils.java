package audio.rabid.dev.wallpapersetter;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by  charles  on 12/26/15.
 */
public class Utils {
    public static void pipe(InputStream is, OutputStream os) throws IOException{
        byte[] buffer = new byte[1024];
        int total = 0;
        int len = is.read(buffer);
        while (len >= 0) {
            os.write(buffer, 0, len);
            os.flush();
            total = total+len;
            len = is.read(buffer);
        }
        is.close();
        os.close();
    }
}
