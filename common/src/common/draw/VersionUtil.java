package common.draw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class VersionUtil {

    public static int loadRevision() {
        InputStream is = VersionUtil.class.getResourceAsStream("/revision.properties");
        if (is != null) {
            try (BufferedReader rdr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line = rdr.readLine();
                if (line != null) {
                    try {
                        return Integer.parseInt(line);
                    } catch (NumberFormatException nfex) {
                        // ignore
                    }
                }
            } catch (IOException ex) {
                // ignore
            }
        }
        return 0;
    }

    public static String getTitle(int revision, String title) {
        return revision > 0 ? title + " v." + revision : title;
    }
}
