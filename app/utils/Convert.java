package utils;

import com.google.inject.Singleton;
import org.hashids.*;

@Singleton
public class Convert {
    static final Hashids hashids = new Hashids("this is my salt");

    public static String encode(Long id){
        return hashids.encode(id);
    }

    public static Long decode(String slug){
        return hashids.decode(slug)[0];
    }
}
