package ru.sberbank.lesson4.task.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.startService);
        button.setOnClickListener(mStartListener);
        button = findViewById(R.id.startAdditionalActivity);
        button.setOnClickListener(mStartAdditionalActivityListener);
    }

    private View.OnClickListener mStartListener = new View.OnClickListener() {
        public void onClick(View v) {
            startService(new Intent(MainActivity.this,
                    ExampleService.class));
        }
    };

    private View.OnClickListener mStartAdditionalActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, AdditionalActivity.class));
        }
    };
}
