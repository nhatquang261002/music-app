package com.example.musicapp.service;

import com.android.volley.toolbox.HurlStack;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicapp.model.Song;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class MusicService {

    public interface SongCallback {
        void onSuccess(ArrayList<Song> songs);
        void onError(String message);
    }

    private static final String BASE_URL = "https://mp3.zing.vn/xhr/";

    public static void fetchSongs(Context context, final SongCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSSLContext().getSocketFactory()));

        final String TRENDING_URL = BASE_URL + "chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, TRENDING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<Song> songs = parseData(response);
                            callback.onSuccess(songs);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Error fetching data");
            }
        });

        requestQueue.add(stringRequest);
    }

    public static void searchSongs(Context context, String query, final SongCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSSLContext().getSocketFactory()));
        String SEARCH_URL = "https://ac.mp3.zing.vn/complete?type=artist,song,key,code&num=500&query=" + query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEARCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            ArrayList<Song> songs = parseSearchData(response);
                            callback.onSuccess(songs);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error fetching data";
                if (error.networkResponse != null) {
                    errorMessage += " - Status code: " + error.networkResponse.statusCode;
                }
                if (error.getMessage() != null) {
                    errorMessage += " - Message: " + error.getMessage();
                }
                callback.onError(errorMessage);
            }
        });

        requestQueue.add(stringRequest);
    }

    private static ArrayList<Song> parseSearchData(String response) throws JSONException {
        ArrayList<Song> songs = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray jsonDataArray = jsonResponse.getJSONArray("data");

        // Assuming there's only one object in the data array
        JSONObject dataObject = jsonDataArray.getJSONObject(0);
        JSONArray songArray = dataObject.getJSONArray("song");

        for (int i = 0; i < songArray.length(); i++) {
            JSONObject jsonSong = songArray.getJSONObject(i);
            Song song = new Song(
                    jsonSong.getString("id"),
                    jsonSong.getString("name"),
                    jsonSong.getString("artist"),
                    "https://photo-resize-zmp3.zmdcdn.me/w94_r1x1_jpeg/" + jsonSong.getString("thumb"),
                    "", // Assuming no position information in the response
                    "", // Assuming no lyric information in the response
                    "", // Assuming no source information in the response
                    "", // Assuming no code information in the response
                    jsonSong.getInt("duration")
            );

            songs.add(song);
        }

        return songs;
    }
    private static SSLContext getSSLContext() {
        try {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }

                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());

            // Disable hostname verification
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;  // Always return true, ignoring hostname verification
                }
            });

            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Song> parseData(String response) throws JSONException {
        ArrayList<Song> songs = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        JSONArray jsonArray = jsonData.getJSONArray("song");

        for (int i = 0; i < 20; i++) {
            JSONObject jsonSong = jsonArray.getJSONObject(i);
            Song song = new Song(
                    jsonSong.getString("id"),
                    jsonSong.getString("name"),
                    jsonSong.getString("artists_names"),
                    jsonSong.getString("thumbnail"),
                    jsonSong.getString("position"),
                    jsonSong.getString("lyric"),
                    "source_placeholder",
                    jsonSong.getString("code"),
                    jsonSong.getInt("duration")
            );

            // Adjust thumbnail URL
            int index = song.getThumbnail().indexOf("w94_r1x1_jpeg/");
            String thumbnailUrl = song.getThumbnail().substring(0, index) + song.getThumbnail().substring(index + "w94_r1x1_jpeg/".length());
            song.setThumbnail(thumbnailUrl);

            songs.add(song);
        }

        return songs;
    }

    public static void getSongByCode(Context context, String songCode, final SongCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSSLContext().getSocketFactory()));

        Log.d("Song Code", songCode);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "media/get-source?type=audio&key=" + songCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ApiService", "Response: " + response); // Log the response here
                        try {
                            Song song = parseSongByCode(response);
                            ArrayList<Song> songs = new ArrayList<>();
                            songs.add(song);
                            callback.onSuccess(songs);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Error fetching data");
            }
        });

        requestQueue.add(stringRequest);
    }

    private static Song parseSongByCode(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonData = jsonObject.getJSONObject("data");

        JSONObject jsonSong = jsonData;
        String id = jsonSong.getString("id");
        String name = jsonSong.getString("name");
        String artistsNames = jsonSong.getString("artists_names");
        String thumbnail = jsonSong.getString("thumbnail");
        String code = jsonSong.getString("code");
        String lyric = jsonSong.getString("lyric");
        String source = jsonSong.getJSONObject("source").getString("128");
        int duration = jsonSong.getInt("duration");

        Song song = new Song(
                id,
                name,
                artistsNames,
                thumbnail,
                "position_placeholder",
                lyric,
                source,
                code,
                duration
        );

        // Adjust thumbnail URL
        int index = song.getThumbnail().indexOf("w94_r1x1_jpeg/");
        String thumbnailUrl = song.getThumbnail().substring(0, index) + song.getThumbnail().substring(index + "w94_r1x1_jpeg/".length());
        song.setThumbnail(thumbnailUrl);

        return song;
    }
}