package com.amere.deliverqasem;

import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class dialogItem extends AppCompatActivity {
    //----------------------- Variables & Objects -------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_display);

    }

}