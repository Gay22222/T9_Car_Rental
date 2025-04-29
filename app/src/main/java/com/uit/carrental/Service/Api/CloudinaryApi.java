package com.uit.carrental.Service.Api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryApi {

    private static final String CLOUD_NAME = "drc8ylzsd";
    private static final String API_KEY = "186315811678634";
    private static final String UPLOAD_PRESET = "car_rent_unsigned";

    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("unsigned", true);
        MediaManager.init(context, config);
        initialized = true;
    }

    public static void uploadImage(Context context, Uri imageUri, UploadCallbackCustom callback) {
        init(context);

        MediaManager.get().upload(imageUri)
                .option("upload_preset", UPLOAD_PRESET)
                .option("unsigned", true) // THÊM DÒNG NÀY !!!
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload Started: " + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("Cloudinary", "Progress: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        if (callback != null) callback.onSuccess(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        if (callback != null) callback.onFailure(new Exception(error.getDescription()));
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        if (callback != null) callback.onFailure(new Exception(error.getDescription()));
                    }
                })
                .dispatch();
    }


    public interface UploadCallbackCustom {
        void onSuccess(String url);
        void onFailure(Exception e);
    }
}
