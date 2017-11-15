package com.coder;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;


import java.util.Map;
import java.util.HashMap;

public class NativeCoderModule extends ReactContextBaseJavaModule {

    public NativeCoderModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "nativeCoder";
    }

    @ReactMethod
    public void encode(String fileUri, String algorithm, Promise promise) {
        try {
            WritableMap map = Arguments.createMap();

            map.putString("my_result", "kek");

            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("ENCODE_ERROR", e);
        }
    }
}