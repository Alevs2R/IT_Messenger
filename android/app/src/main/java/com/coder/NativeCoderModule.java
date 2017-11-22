package com.coder;

import com.coder.Hamming.Hamming74Coder;
import com.coder.Hamming.Hamming74Decoder;
import com.coder.Repetition.RepetitionDecoder;
import com.coder.Repetition.RepetitionEncoder;
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
import java.util.Random;

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
    public void encode(String fileName, String fileUri, String compressAlgorithm, String codingAlgorithm, String noiseLevel, Promise promise) {
        try {
            byte[] resultBytes;
            Uri myUri = Uri.parse(fileUri);
            InputStream inputStream = getReactApplicationContext().getContentResolver().openInputStream(myUri);
            byte[] inputBytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);

            switch(compressAlgorithm){
                case "arithmetic":
                    ArithmeticCoding coder = new ArithmeticCoding();
                    resultBytes = coder.compress(inputBytes);
                    break;
                case "rle":
                    RunLengthCompression runLengthCompression = new RunLengthCompression();
                    resultBytes = runLengthCompression.compress(inputBytes);
                    break;
                case "lzw":
                    LZW lzw = new LZW();
                    resultBytes = lzw.compress(inputStream);
                    break;
                default:
                    throw new Exception("no compress algorithm provided");
            }

            switch(codingAlgorithm){
                case "hamming":
                    Hamming74Coder hamming74coder = new Hamming74Coder();
                    resultBytes = hamming74coder.coding(resultBytes);
                    break;
                case "rm":
                    ReedMuller reedMuller = new ReedMuller();
                    resultBytes = reedMuller.encode(resultBytes);
                    break;
                case "repetition":
                    RepetitionEncoder repetitionEncoder = new RepetitionEncoder();
                    resultBytes = repetitionEncoder.encode(resultBytes, 3);
                    break;
                default:
                    throw new Exception("no coding algorithm provided");
            }

            switch(noiseLevel){
                case "1":
                    generateNoise(resultBytes, 0.01);
                    break;
                case "5":
                    generateNoise(resultBytes, 0.05);
                    break;
                case "15":
                    generateNoise(resultBytes, 0.15);
                    break;
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
            byte[] resultBytes = null;
           // Uri myUri = Uri.parse(fileUri);
           // InputStream inputStream = getReactApplicationContext().getContentResolver().openInputStream(myUri);
            InputStream inputStream = new FileInputStream(fileUri);
            byte[] inputBytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);

            File decodedDir = new File(getReactApplicationContext().getFilesDir(), "decoded");
            decodedDir.mkdir();
            File resultFile = new File(decodedDir, fileName);
            FileOutputStream outputStream = null;

            int endSize = 0;

            switch(algorithm){
                case "arithmetic":
                    ArithmeticCoding coder = new ArithmeticCoding();
                    resultBytes = coder.decompress(inputBytes);
                    break;
                case "hamming":
                    Hamming74Decoder hamming74Coder = new Hamming74Decoder();
                    resultBytes = hamming74Coder.decoding(inputBytes);
                    break;
                case "rm":
                    ReedMuller reedMuller = new ReedMuller();
                    resultBytes = reedMuller.decode(inputBytes);
                    break;
                case "rle":
                    RunLengthCompression runLengthCompression = new RunLengthCompression();
                    resultBytes = runLengthCompression.decompress(inputBytes);
                    break;
                case "lzw":
                    LZW lzw = new LZW();
                    outputStream = new FileOutputStream(resultFile, false);
                    endSize = lzw.decompress(inputBytes,outputStream);
                    break;
                case "repetition":
                    RepetitionDecoder repetitionDecoder = new RepetitionDecoder();
                    resultBytes = repetitionDecoder.decode(inputBytes, 3);
                    break;
                default:
                    throw new Exception("no algorithm provided");
            }

            if(outputStream == null){
                outputStream = new FileOutputStream(resultFile, false);
                outputStream.write(resultBytes);
                outputStream.close();
            }

            WritableMap map = Arguments.createMap();
            map.putString("fileName", resultFile.getName());
            map.putString("type", "application/octet-stream");
            map.putString("uri", resultFile.toURI().getPath());
            map.putInt("encodedSize", inputBytes.length);
            if(resultBytes != null){
                map.putInt("decodedSize", resultBytes.length);
            } else {
                map.putInt("decodedSize", endSize);
            }
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

    /**
     * This function takes byte array as an input and performs some flipping of bits
     * with probability of epsilon.
     * @param array is the input byte array
     * @param epsilon is the probability of flipping a bit
     */
    static void generateNoise(byte[] array, double epsilon) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            StringBuilder newValue = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                byte temp = array[i];
                int value = (temp >>> (j)) & 1;
                if (epsilon >= random.nextDouble())
                    value = value == 1 ? 0 : 1;
                else
                    value = value == 1 ? 1 : 0;
                newValue.append(value);
            }
            newValue = newValue.reverse();
            String str = newValue.toString();
            int tr = Integer.parseInt(str, 2);
            array[i] = (byte)tr;
        }
    }
}