package com.arieschwartzman.cortana;

import android.app.Activity;
import android.app.SearchManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CountSteps extends Activity  {

    private TextView tv;
    private ProgressBar progressBar;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        setContentView(R.layout.activity_count_steps);
        SharedPreferences pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        String token = pref.getString("Token","");
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(token);
    }


    public void renderResponse(SummaryResponse response) {
        int stepAvg = 0;
        int caloriesAvg = 0;
        for (int s=0; s < response.getItemCount(); s++) {
            stepAvg+=response.getSummaries().get(s).getStepsTaken();
            caloriesAvg += response.getSummaries().get(s).getCaloriesBurnedSummary().getTotalCalories();
        }

        String msg = "You took "  + stepAvg/response.getItemCount() + " steps";
        msg+=" and burnned " + caloriesAvg/response.getItemCount() + " calories this week on average";
        tv.setText(msg);
        progressBar.setVisibility(View.INVISIBLE);
    }


    public class BackgroundTask extends AsyncTask<String,Void,SummaryResponse> {

        private ISummaryService service;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.microsofthealth.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ISummaryService.class);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected SummaryResponse doInBackground(String... strings) {

            Calendar cal  = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DATE, -7);
            Date yesterday = cal.getTime();
            Date today = new Date();

            String yesterdayString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(yesterday);
            String todayString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(today);


            Call<SummaryResponse> summaries = service.getSummaries("Bearer " + strings[0], yesterdayString, todayString);
            Response<SummaryResponse> response = null;
            try {
                response = summaries.execute();
                if (response.errorBody() != null) {
                    String err = new String(response.errorBody().bytes());
                    Log.e("", err);
                }
                else {
                    Log.d("BackgroundTask", response.body().getSummaries().size() + "");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.body();
        }

        @Override
        protected void onPostExecute(SummaryResponse response) {
            super.onPostExecute(response);
            CountSteps.this.renderResponse(response);

        }
    }
}
