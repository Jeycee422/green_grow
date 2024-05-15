package com.example.green_grow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent articleIntent = getIntent();
        String imageSrc = articleIntent.getStringExtra("image");
        String tokenStr = articleIntent.getStringExtra("token");
        String dateStr = articleIntent.getStringExtra("date");
        String bodyStr = articleIntent.getStringExtra("heading");
        String titleStr = articleIntent.getStringExtra("title");
        String articleSlug = articleIntent.getStringExtra("slug");

        Log.d("BODY", tokenStr);

        TextView articleTitle = findViewById(R.id.article_heading);
        ImageView articleImage = findViewById(R.id.article_image);
        TextView articleDate = findViewById(R.id.article_date);
        TextView articleBody = findViewById(R.id.article_articleBody);

        articleTitle.setText(titleStr);
        int resourceId = getResources().getIdentifier(imageSrc,"drawable",getPackageName());
        articleImage.setImageResource(resourceId);

        articleDate.setText(dateStr);
        articleBody.setText(bodyStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://greengrow-mongodb-expressjs.onrender.com/comment/"+ articleSlug);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestProperty("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI2NjNmOTYxZjk5NDZjOTI2ZTAxZDU2ODgiLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImVtYWlsIjoiYWRtaW5AZ21haWwuY29tIiwicGFzc3dvcmQiOiIkMmIkMTAkSzcuY1YvRjMuN05wazRyOEI5TllKT3VwNFJUdkZwalRCRnZNdDU5RmtiLzBMRmk2cTdIS0siLCJjcmVhdGVkQXQiOiIyMDI0LTA1LTExVDE2OjAwOjMxLjYzOFoiLCJ1cGRhdGVkQXQiOiIyMDI0LTA1LTExVDE2OjAwOjMxLjYzOFoiLCJfX3YiOjB9.JwFWOnHrjbh2kexB9Dc76o7LjkVZ1ZZwv5sn2Iztur8");
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
                            LinearLayout linearLayout = findViewById(R.id.comment_section);
//                            LinearLayout replyLinear = findViewById(R.id.reply_section);

                            for (JsonElement comment : jsonResponse.getAsJsonArray("data")) {
                                JsonObject commentObject = comment.getAsJsonObject();
                                JsonObject userObject = commentObject.getAsJsonObject("user");
                                JsonArray conversationArray = commentObject.getAsJsonArray("conversation");

                                View userItemView = getLayoutInflater().inflate(R.layout.comment_layout, null);
                                TextView textComment = userItemView.findViewById(R.id.comment_text);
                                TextView textEmail = userItemView.findViewById(R.id.user_email);
                                textComment.setText(commentObject.get("comment").getAsString());
                                String fullName = userObject.get("first_name").getAsString() + " " + userObject.get("last_name").getAsString();
                                textEmail.setText(fullName);
                                linearLayout.addView(userItemView);

                                // Check if there are conversations/replies
                                if (conversationArray != null && conversationArray.size() > 0) {
                                    for (JsonElement reply : conversationArray) {
                                        JsonObject replyObject = reply.getAsJsonObject();
                                        JsonObject replyUserObject = replyObject.getAsJsonObject("user");

                                        // Inflate a new instance of comment_layout for each reply
                                        View replyView = getLayoutInflater().inflate(R.layout.comment_layout, null);
                                        TextView textReply = replyView.findViewById(R.id.comment_text);
                                        TextView textUserReply = replyView.findViewById(R.id.user_email);
                                        textReply.setText(replyObject.get("reply").getAsString());
                                        String fullUserReplyName = replyUserObject.get("first_name").getAsString() + " " + replyUserObject.get("last_name").getAsString();
                                        textUserReply.setText(fullUserReplyName);

                                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) replyView.getLayoutParams();

                                        if (params == null) {
                                            params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        }
                                        int marginStartPx = getResources().getDimensionPixelSize(R.dimen.replyDimen);
                                        params.setMarginStart(marginStartPx);

                                        replyView.setLayoutParams(params);
                                        linearLayout.addView(replyView);
                                    }
                                }
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