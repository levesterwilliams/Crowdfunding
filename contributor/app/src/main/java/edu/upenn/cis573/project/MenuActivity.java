package edu.upenn.cis573.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }


    public void onViewDonationsButtonClick(View view) {

        Intent i = new Intent(this, ViewDonationsActivity.class);


        startActivity(i);

    }

    public void onMakeDonationButtonClick(View view) {

        Intent i = new Intent(this, MakeDonationActivity.class);


        startActivity(i);

    }
}