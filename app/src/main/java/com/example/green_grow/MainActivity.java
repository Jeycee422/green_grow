package com.example.green_grow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    TextView messageBox;
    TextView regMessageBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageBox = findViewById(R.id.message_box);



        if(!NetworkUtils.isNetworkAvailable(this)){
            messageBox.setText("Please check your internet connection.");
            messageBox.setTextColor(getResources().getColor(R.color.message_gray));
            messageBox.setBackground(getResources().getDrawable(R.drawable.message_gray));
        }
        View rootView = findViewById(android.R.id.content);

        Dialog registerDialog = new Dialog(this);
        registerDialog.setContentView(R.layout.register_dialog);
        regMessageBox = registerDialog.findViewById(R.id.reg_message_box);

        final EditText username = findViewById(R.id.log_username);
        final EditText password = findViewById(R.id.log_pass);

        final EditText regUsername = registerDialog.findViewById(R.id.reg_email);
        final EditText regPassword = registerDialog.findViewById(R.id.reg_pass);
        final EditText regFName = registerDialog.findViewById(R.id.reg_firstName);
        final EditText regLName = registerDialog.findViewById(R.id.reg_lastName);


        Window window = registerDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            window.setBackgroundDrawableResource(R.drawable.register_dialog_bg);

            InsetDrawable inset = new InsetDrawable(getDrawable(R.drawable.register_dialog_bg), 20);
            window.setBackgroundDrawable(inset);
        }
        registerDialog.setCancelable(true);

        TextView account_create = findViewById(R.id.account_create);
        account_create.setOnClickListener(v -> {
            registerDialog.show();
        });

        LinearLayout exitDialog = registerDialog.findViewById(R.id.exit_btn);
        exitDialog.setOnClickListener(v -> {
            registerDialog.dismiss();
        });

        TextView showPassLog = findViewById(R.id.show_pass_log);
        showPassLog.setOnClickListener(v -> {
            EditText logPass = findViewById(R.id.log_pass);
            boolean isLogPassShown = (logPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            if(isLogPassShown) {
                logPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else {
                logPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        TextView showPassReg = registerDialog.findViewById(R.id.show_pass_reg);
        showPassReg.setOnClickListener(v -> {
            EditText regPass = registerDialog.findViewById(R.id.reg_pass);
            boolean isRegPassShown = (regPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            if(isRegPassShown) {
                regPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else {
                regPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        TextView loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(v -> {

            messageBox.setText("Loading...");
            messageBox.setTextColor(getResources().getColor(R.color.message_green));
            messageBox.setBackground(getResources().getDrawable(R.drawable.message_green));
            String userName = username.getText().toString();
            String userPass = password.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // URL for the sign-in endpoint
                        URL url = new URL("https://greengrow-mongodb-expressjs.onrender.com/sign-in");

                        // Open a connection
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        // Set the request method to POST
                        conn.setRequestMethod("POST");

                        // Set request headers
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");

                        // Create JSON data for the sign-in request using template string
                        String requestData = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", userName, userPass);

                        // Enable output and send JSON data
                        conn.setDoOutput(true);
                        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                            byte[] data = requestData.getBytes(StandardCharsets.UTF_8);
                            wr.write(data);
                        }

                        // Get the response code
                        int responseCode = conn.getResponseCode();
                        Log.d("Response Code: " ,String.valueOf(responseCode));

                        // Read the response
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            Log.d("Response: ", response.toString());
                        }

                        // Close the connection
                        conn.disconnect();

                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                        // Access JSON fields
                        String userStatus = jsonResponse.get("status").getAsString();
                        String userMessage = jsonResponse.get("message").getAsString();
                        runOnUiThread(() -> {
                            closeKeyboard(rootView);
                            if(userStatus.equals("false")) {
                                messageBox.setText(userMessage);
                                messageBox.setTextColor(getResources().getColor(R.color.message_red));
                                messageBox.setBackground(getResources().getDrawable(R.drawable.message_red));
                            }else {

                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, main_app.class).putExtra("email",userName).putExtra("token",jsonResponse.get("data").toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                };

                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(runnable);
                            }
//                            Toast.makeText(MainActivity.this, accessToken.getClass().toString(), Toast.LENGTH_SHORT).show();
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });


        TextView registerBtn = registerDialog.findViewById(R.id.reg_btn);
        registerBtn.setOnClickListener(v -> {

            String userName = regUsername.getText().toString();
            String userPass = regPassword.getText().toString();
            String userFName = regFName.getText().toString();
            String userLName = regLName.getText().toString();

            if (userFName.equals("") || userLName.equals("") || userName.equals("") || userPass.equals("")) {
                regMessageBox.setText("Please fill all field");
                regMessageBox.setTextColor(getResources().getColor(R.color.message_red));
                regMessageBox.setBackground(getResources().getDrawable(R.drawable.message_red));
            }else {
                regMessageBox.setText("Loading...");
                regMessageBox.setTextColor(getResources().getColor(R.color.message_green));
                regMessageBox.setBackground(getResources().getDrawable(R.drawable.message_green));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // URL for the sign-in endpoint
                            URL url = new URL("https://greengrow-mongodb-expressjs.onrender.com/sign-up");

                            // Open a connection
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            // Set the request method to POST
                            conn.setRequestMethod("POST");

                            // Set request headers
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept", "application/json");

                            // Create JSON data for the sign-in request using template string
                            String requestData = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"first_name\": \"%s\", \"last_name\": \"%s\"}", userName, userPass, userFName, userLName);

                            // Enable output and send JSON data
                            conn.setDoOutput(true);
                            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                                byte[] data = requestData.getBytes(StandardCharsets.UTF_8);
                                wr.write(data);
                            }

                            // Get the response code
                            int responseCode = conn.getResponseCode();
                            Log.d("Response Code: " ,String.valueOf(responseCode));

                            // Read the response
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                Log.d("Response: ", response.toString());
                            }

                            // Close the connection
                            conn.disconnect();
                            regFName.setText("");
                            regLName.setText("");
                            regUsername.setText("");
                            regPassword.setText("");
                            regMessageBox.setText("");
                            regMessageBox.setVisibility(View.INVISIBLE);

                            Gson gson = new Gson();
                            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                            // Access JSON fields
                            String accessToken = jsonResponse.get("status").getAsString();
                            runOnUiThread(() -> {
                                if(accessToken.toLowerCase().equals("success")) {
                                    Toast.makeText(MainActivity.this, accessToken, Toast.LENGTH_SHORT).show();
                                    registerDialog.dismiss();
                                    closeKeyboard(registerDialog.getCurrentFocus());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        });



//        try {
//            // Create URL
//            URL url = new URL("https://greengrow-mongodb-expressjs.onrender.com/comment/test-article-slug-4");
//
//            // Create connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            connection.setRequestProperty("Authorization","eyJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI2NjNmOTYxZjk5NDZjOTI2ZTAxZDU2ODgiLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImVtYWlsIjoiYWRtaW5AZ21haWwuY29tIiwicGFzc3dvcmQiOiIkMmIkMTAkSzcuY1YvRjMuN05wazRyOEI5TllKT3VwNFJUdkZwalRCRnZNdDU5RmtiLzBMRmk2cTdIS0siLCJjcmVhdGVkQXQiOiIyMDI0LTA1LTExVDE2OjAwOjMxLjYzOFoiLCJ1cGRhdGVkQXQiOiIyMDI0LTA1LTExVDE2OjAwOjMxLjYzOFoiLCJfX3YiOjB9.JwFWOnHrjbh2kexB9Dc76o7LjkVZ1ZZwv5sn2Iztur8");
//
//            // Read response
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            // Log response
//            Log.d("HTTP Response", response.toString());
//
//            // Close connection
//            connection.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}