package com.coder;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.content.Intent;


import java.util.Map;
import java.util.HashMap;
import java.io.*;
import com.coder.Arithmetic.*;


public class NativeCoderModule extends ReactContextBaseJavaModule {

    public NativeCoderModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "nativeCoder";
    }

    @ReactMethod
    public void encode(String fileName, String fileUri, String algorithm, Promise promise) {
        try {
            byte[] resultBytes;
            Uri myUri = Uri.parse(fileUri);
            InputStream inputStream = getReactApplicationContext().getContentResolver().openInputStream(myUri);
            byte[] inputBytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);

            switch(algorithm){
                case "arithmetic":
                    ArithmeticCoding coder = new ArithmeticCoding();
                    resultBytes = coder.compress(inputBytes);
                    break;
                default:
                    throw new Exception("no algorithm provided");
            }

            File resultFile = new File(getReactApplicationContext().getFilesDir(),fileName);
            FileOutputStream outputStream = new FileOutputStream(resultFile, false);
            outputStream.write(resultBytes);
            outputStream.close();

            WritableMap map = Arguments.createMap();
            map.putString("fileName", resultFile.getName());
            map.putString("type", "application/octet-stream");
            map.putString("uri", resultFile.toURI().toString());

            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("ENCODE_ERROR", e);
        }
    }

    @ReactMethod
    public void decode(String fileName, String fileUri, String algorithm, Promise promise) {
        try {
            byte[] resultBytes;
           // Uri myUri = Uri.parse(fileUri);
           // InputStream inputStream = getReactApplicationContext().getContentResolver().openInputStream(myUri);
            InputStream inputStream = new FileInputStream(fileUri);
            byte[] inputBytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);

            switch(algorithm){
                case "arithmetic":
                    ArithmeticCoding coder = new ArithmeticCoding();
                    resultBytes = coder.decompress(inputBytes);
                    break;
                default:
                    throw new Exception("no algorithm provided");
            }

            File decodedDir = new File(getReactApplicationContext().getFilesDir(), "decoded");
            decodedDir.mkdir();
            File resultFile = new File(decodedDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(resultFile, false);
            outputStream.write(resultBytes);
            outputStream.close();

            WritableMap map = Arguments.createMap();
            map.putString("fileName", resultFile.getName());
            map.putString("type", "application/octet-stream");
            map.putString("uri", resultFile.toURI().getPath());
            map.putInt("encodedSize", inputBytes.length);
            map.putInt("decodedSize", resultBytes.length);
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("ENCODE_ERROR", e);
        }
    }

    @ReactMethod
    public void openFile(String fileUri) {
        Context context = getReactApplicationContext();

        File file = new File(fileUri);

        // Get URI and MIME type of file
        Uri uri = FileProvider.getUriForFile(context, "com.magic_messenger" + ".fileprovider", file);
        String mime = getReactApplicationContext().getContentResolver().getType(uri);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }
}