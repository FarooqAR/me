package com.example.stranger.me.helper;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farooq on 1/9/2016.
 */
public class CloudinaryHelper {
    private static Cloudinary cloudinary = null;
    public static Cloudinary getInstance(){
        if(cloudinary == null){
            Map config = new HashMap();
            config.put("cloud_name","farooq");
            config.put("api_key", "197774461189835");
            config.put("api_secret", "jgGD6adK-TFRScPkpH_aSIWOSI0");
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}
