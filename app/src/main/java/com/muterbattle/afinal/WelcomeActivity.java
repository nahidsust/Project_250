package com.muterbattle.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
                                public void run() {
                                    startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
                                    finish();
                                }
                            },2000
        );
    }
}