package com.henley.shadowlayout.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text_card).setOnClickListener(this);
        findViewById(R.id.text_shadow_view).setOnClickListener(this);
        findViewById(R.id.text_shadow_view2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_card:
                startActivity(new Intent(this, CardViewActivity.class));
                break;
            case R.id.text_shadow_view:
                startActivity(new Intent(this, ShadowViewActivity.class));
                break;
            case R.id.text_shadow_view2:
                startActivity(new Intent(this, ShadowViewDemo2Activity.class));
                break;
        }
    }
}
