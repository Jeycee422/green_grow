package com.example.green_grow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class main_app extends AppCompatActivity {

    private String  token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        ImageView articleOne = findViewById(R.id.article1);
        LinearLayout latestContainer = findViewById(R.id.article_latest_container);
        int contId = 10;
        latestContainer.setId(contId);
        LinearLayout recommendedContainer = findViewById(R.id.article_recommended_container);

        Intent userAuthIntent = getIntent();
        String email = userAuthIntent.getStringExtra("email");
        token = userAuthIntent.getStringExtra("token");
        TextView userEmail = findViewById(R.id.userEmail);
        userEmail.setText(email);

        Log.d("User token", token);

        articleOne.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header1")
                    .putExtra("heading",getResources().getString(R.string.article_1))
                    .putExtra("date","2024-05-07")
                    .putExtra("slug", "da-vows-to-reinforce-agri-based-industries-supports-livestock-and-aquaculture-philippines-2024")
                    .putExtra("title","DA vows to reinforce agri-based industries, supports Livestock and Aquaculture Philippines 2024"));
        });
        getArticles(latestContainer);
        getArticles(recommendedContainer);

    }

    private int getMemo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

//        long totalMemory = memoryInfo.totalMem / (1024 * 1024); // Convert bytes to MB
        int memoryClass = activityManager.getMemoryClass();
        return memoryClass;
    }
    @Override
    public void onBackPressed() {
        // Do nothing (disable back button)
    }

    public void getArticles(LinearLayout linearLayout) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    int idVal = 10;

                    Log.d("ID", String.valueOf(linearLayout.getId()));
                    if(linearLayout.getId() == idVal) {

                        url = new URL("https://greengrow-mongodb-expressjs.onrender.com/articles?limit=10");
                    }else {
                        url = new URL("https://greengrow-mongodb-expressjs.onrender.com/articles/recommended");
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestProperty("Authorization", token.trim());
                    conn.setRequestMethod("GET");

                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    int responseCode = conn.getResponseCode();
                    Log.d("Response Code: " ,String.valueOf(responseCode));

                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        Log.d("Response: ", response.toString());
                    }

                    conn.disconnect();

                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (JsonElement article : jsonResponse.getAsJsonArray("data")) {
                                JsonObject articleObject = article.getAsJsonObject();
                                JsonObject metaObject = articleObject.getAsJsonObject("metadata");

                                View userItemView = getLayoutInflater().inflate(R.layout.article_card, null);
                                TextView authorText = userItemView.findViewById(R.id.article_author);
                                TextView publishedDate = userItemView.findViewById(R.id.article_published);
                                TextView contents = userItemView.findViewById(R.id.article_context);
                                TextView articleTitle = userItemView.findViewById(R.id.article_title);
                                ImageView articleImage = userItemView.findViewById(R.id.article_img);



                                Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
                                builder.memoryCache(new LruCache((getMemo() * 20) / 100)); // Set the cache size in bytes
                                Picasso picasso = builder.build();
                                picasso.setIndicatorsEnabled(true); // For debugging
                                picasso.load(articleObject.get("thumbnail").getAsString()).resize(800, 600).into(articleImage);

                                authorText.setText(metaObject.get("author_name").getAsString());
                                articleTitle.setText(articleObject.get("title").getAsString());
                                Spanned htmlToText = Html.fromHtml(metaObject.get("contents").getAsString());
                                contents.setText(htmlToText);
                                publishedDate.setText(metaObject.get("da_date_uploaded").getAsString());

                                articleImage.setOnClickListener(v -> {
                                    startActivity(new Intent(getApplicationContext(), ArticleActivity.class)
                                            .putExtra("token",token)
                                            .putExtra("image",articleObject.get("thumbnail").getAsString())
                                            .putExtra("heading", metaObject.get("contents").getAsString())
                                            .putExtra("date",publishedDate.getText())
                                            .putExtra("slug", articleObject.get("slug").getAsString())
                                            .putExtra("title",articleTitle.getText()));
                                });

                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) userItemView.getLayoutParams();

                                if (params == null) {
                                    params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                }
                                int marginStartPx = getResources().getDimensionPixelSize(R.dimen.articleDimen);
                                params.setMargins(0,0,0,marginStartPx);

                                userItemView.setLayoutParams(params);

                                linearLayout.addView(userItemView);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}