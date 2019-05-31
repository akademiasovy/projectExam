package general;

import general.http.Index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static byte[] readResource(String name) throws IOException {
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (;;) {
            int nread = is.read(buffer);
            if (nread <= 0) {
                break;
            }
            baos.write(buffer, 0, nread);
        }
        return baos.toByteArray();
    }

}
