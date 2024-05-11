package com.example.green_grow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dialog registerDialog = new Dialog(this);
        registerDialog.setContentView(R.layout.register_dialog);
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

    }
}