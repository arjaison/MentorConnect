package com.ecorp.MentorConnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OptionPage extends AppCompatActivity {
    Button mentor,mentee;

    Typeface MR, MRR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option);

        MRR = Typeface.createFromAsset(getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");



        mentor = findViewById(R.id.mentor);
        mentee = findViewById(R.id.mentee);


        mentor.setTypeface(MR);
        mentee.setTypeface(MR);


        mentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OptionPage.this, RegisterActivity.class));
            }
        });

        mentee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OptionPage.this, RegisterActivity_Mentee.class));
            }
        });
    }
}
