package com.example.green_grow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class main_app extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        ImageView articleOne = findViewById(R.id.article1);
        ImageView articleOneToo = findViewById(R.id.article1too);
        ImageView articleTwo = findViewById(R.id.article2);
        ImageView articleThree = findViewById(R.id.article3);
        ImageView articleFour = findViewById(R.id.article4);
        ImageView articleFive = findViewById(R.id.article5);

        Intent userAuthIntent = getIntent();
        String email = userAuthIntent.getStringExtra("email");
        String token = userAuthIntent.getStringExtra("token");
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

        articleOneToo.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header1")
                    .putExtra("heading",getResources().getString(R.string.article_1))
                    .putExtra("date","2024-05-07")
                    .putExtra("slug", "da-vows-to-reinforce-agri-based-industries-supports-livestock-and-aquaculture-philippines-2024")
                    .putExtra("title","DA vows to reinforce agri-based industries, supports Livestock and Aquaculture Philippines 2024"));
        });

        articleTwo.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header2")
                    .putExtra("heading",getResources().getString(R.string.article_2))
                    .putExtra("date","2024-05-09")
                    .putExtra("slug", "agriculture-shows-growth-in-1q-despite-challenges")
                    .putExtra("title","Agriculture shows growth in 1Q despite challenges"));
        });

        articleThree.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header3")
                    .putExtra("heading",getResources().getString(R.string.article_3))
                    .putExtra("date","2024-05-06")
                    .putExtra("slug", "da-to-showcase-initiatives-expertise-in-livestock-and-aquaculture-philippines-2024")
                    .putExtra("title","DA to showcase initiatives, expertise in Livestock and Aquaculture Philippines 2024"));
        });

        articleFour.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header4")
                    .putExtra("heading",getResources().getString(R.string.article_4))
                    .putExtra("date","2024-05-06")
                    .putExtra("slug", "da-leads-philcz-commits-to-better-health-for-filipinos-and-the-animal-population")
                    .putExtra("title","DA leads PhilCZ, commits to better health for Filipinos and the animal population"));
        });

        articleFive.setOnClickListener(v -> {
            startActivity(new Intent(this, ArticleActivity.class)
                    .putExtra("token",token)
                    .putExtra("image","@drawable/header5")
                    .putExtra("heading",getResources().getString(R.string.article_5))
                    .putExtra("date","2024-05-06")
                    .putExtra("slug", "restore-some-nfa-functions-in-rtl-extension-da-urges-congress")
                    .putExtra("title","Restore some NFA functions in RTL extension, DA urges Congress"));
        });
    }
}