package general;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;

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

    public static String readResourceAsString(String name) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getClassLoader().
                getResourceAsStream(name)));
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        br.close();

        return builder.toString();
    }

    //TODO: Store token in secure httpOnly cookie
    public static String parseToken(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst("Authorization");
    }

}
