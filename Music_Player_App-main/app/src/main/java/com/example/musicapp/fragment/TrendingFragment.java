package com.example.musicapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicapp.R;
import com.example.musicapp.adapter.TrendingSongAdapter;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrendingFragment extends Fragment implements OnChartValueSelectedListener {

    private RecyclerView recyclerView;
    private ArrayList<Song> mListSong = new ArrayList<>();
    private TrendingSongAdapter trendingSongAdapter;
    private TextView tv_dateAndTime;
    private TextView tv_trending;
    private CombinedChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        tv_dateAndTime = view.findViewById(R.id.tv_dateAndTime);
        tv_trending = view.findViewById(R.id.tv_trending);

        recyclerView = view.findViewById(R.id.recyclerView);
        trendingSongAdapter = new TrendingSongAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(trendingSongAdapter);

        mChart = view.findViewById(R.id.combinedChart);
        displayChart();

        return view;
    }

    private void displayChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.parseColor("#35234b"));
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);

        fetchSongs();
        fetchChartData();
    }

    private void fetchSongs() {
        MusicService.fetchSongs(getContext(), new MusicService.SongCallback() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                mListSong.clear();
                mListSong.addAll(songs);
                trendingSongAdapter.setData(getContext(), mListSong);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error fetching songs: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChartData() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1";
        RequestQueue requestQueue = Volley.newRequestQueue(TrendingFragment.this.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject =new JSONObject(s);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            JSONObject jsonObject_songHis = jsonObject_data.getJSONObject("songHis");
                            JSONObject jsonObject1 = jsonObject_songHis.getJSONObject("data");

                            // Convert JSONObject to JSONArray
                            JSONArray jsonArray = jsonObject1.toJSONArray(jsonObject1.names());

                            final List<String> xLabel = new ArrayList<>();
                            LineData lineData = new LineData();
                            for (int i = 0; i < 3; i++){
                                JSONArray jsonArray1 = jsonArray.getJSONArray(i);

                                List<Entry> entries = new ArrayList<>();
                                for (int j = 11; j <24; j++){ // 12h gần nhất
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                    if (i == 0){
                                        String hour = jsonObject2.getString("hour");
                                        xLabel.add(hour);
                                    }
                                    if (j == 23) {
                                        String time = jsonObject2.getString("time");
                                        String hour = jsonObject2.getString("hour");
                                        long l = Long.valueOf(time);
                                        Date date = new Date(l);    //convert ms(Java requirement)
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy"); //HH-mm-ss
                                        String Day = simpleDateFormat.format(date);
                                        tv_dateAndTime.setText(Day + " - " + hour + ":00");
                                    }

                                    int counter = jsonObject2.getInt("counter");
                                    entries.add(new Entry(j-11, counter));
                                }
                                //LineDataSet dataSet = new LineDataSet(entries, "Bài hát top " + String.valueOf(i+1));
                                LineDataSet dataSet = new LineDataSet(entries, null);

                                dataSet.setCircleColor(Color.RED);
                                dataSet.setFillColor(Color.RED);
                                dataSet.setGradientColor(Color.RED, Color.RED);
                                if (i == 0){
                                    setColor(dataSet, Color.parseColor("#3468f0"));
                                } else if (i == 1) {
                                    setColor(dataSet, Color.parseColor("#15c5a1"));
                                }else {
                                    setColor(dataSet, Color.parseColor("#e57437"));
                                }

                                lineData.addDataSet(dataSet);

                            }
                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setDrawGridLines(false);
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(){
                                @Override
                                public String getFormattedValue(float value) {
                                    return xLabel.get((int) value % xLabel.size());
                                }
                            });

                            xAxis.setGranularity(1f);

                            CombinedData data = new CombinedData();
                            data.setData(lineData);
                            mChart.setData(data);
                            mChart.invalidate();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(getContext(), "Value: "
                + e.getY()
                + ", index: "
                + h.getX()
                + ", DataSet index: "
                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
    }

    private void setColor( LineDataSet dataSet1, int color){
        dataSet1.setColor(color);
        dataSet1.setLineWidth(2f);
        dataSet1.setCircleColor(color);
        dataSet1.setCircleRadius(5f);
        dataSet1.setFillColor(color);
        dataSet1.setDrawCircles(true);
        dataSet1.setCircleRadius(2.5f);
        //set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet1.setDrawValues(false);   // Tắt hiển thị giá trị trục ox
    }
}
