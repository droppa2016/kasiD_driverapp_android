package co.za.kasi.utils;

import android.content.Context;
import android.content.res.Resources;
import java.io.InputStream;
import java.util.Properties;

import co.za.kasi.R;


public class PropertyReader {

    public static String getPropertyValue(Context context, String key) {
        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.dev);
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            // Get the value associated with the key
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
