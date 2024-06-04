package com.example.musicapp.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ZingMp3Api {

    private final String VERSION;
    private final String URL;
    private final String SECRET_KEY;
    private final String API_KEY;
    private final String CTIME;

    private Context mContext;
    private RequestQueue mRequestQueue;

    // Define a callback interface
    public interface SongUriCallback {
        void onSuccess(String songUri);
        void onFailure(String error);
    }

    public ZingMp3Api(Context context, String VERSION, String URL, String SECRET_KEY, String API_KEY, String CTIME) {
        this.mContext = context;
        this.VERSION = VERSION;
        this.URL = URL;
        this.SECRET_KEY = SECRET_KEY;
        this.API_KEY = API_KEY;
        this.CTIME = CTIME;

        // Initialize the RequestQueue
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    private String getHash256(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(str.getBytes());
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    private String hashParam(String path, String id) throws NoSuchAlgorithmException, InvalidKeyException {
        String n = "ctime=" + CTIME + "id=" + id + "version=" + VERSION;
        String r = getHash256(n); // Hash the combined string of ctime and id
        String bOc = SECRET_KEY;
        String mInput = path + r;
        return getHmac512(mInput, bOc);
    }

    private String getHmac512(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        hmacSHA512.init(secretKeySpec);
        byte[] bytes = hmacSHA512.doFinal(data.getBytes());
        return bytesToHex(bytes);
    }

    private void requestZingMp3(String path, String id, String sig, final SongUriCallback callback, final String cookie) {
        String fullUrl = URL + path + "?id=" + id + "&ctime=" + CTIME + "&sig=" + sig + "&apiKey=" + API_KEY + "&version=" + VERSION;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataObject = response.getJSONObject("data");
                            String songUri = dataObject.optString("128");
                            if (!songUri.isEmpty()) {
                                callback.onSuccess(songUri);
                            } else {
                                callback.onFailure("No song URI found");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFailure("Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (cookie != null) {
                    headers.put("Cookie", cookie);
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Map<String, String> headers = response.headers;
                    if (headers != null && headers.containsKey("Set-Cookie")) {
                        String cookie = headers.get("Set-Cookie");
                        // Handle the cookie here, you can store it in SharedPreferences or elsewhere
                        requestZingMp3(path, id, sig, callback, cookie);
                    }
                    return super.parseNetworkResponse(response);
                } catch (Exception e) {
                    return Response.error(new VolleyError(e));
                }
            }
        };

        // Set retry policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getSongURI(String songId, final SongUriCallback callback) {
        try {
            String sig = hashParam("/api/v2/song/get/streaming", songId);
            requestZingMp3("/api/v2/song/get/streaming", songId, sig, callback, null);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            callback.onFailure("Error generating signature");
        }
    }
}
