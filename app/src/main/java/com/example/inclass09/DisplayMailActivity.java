package com.example.inclass09;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayMailActivity extends AppCompatActivity {
    TextView name,subject,message,created;
    Button close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_mail);
        name=findViewById(R.id.tv_sender);
        subject=findViewById(R.id.tv_ssubject);
        message=findViewById(R.id.tv_message);
        created=findViewById(R.id.tv_date);
        close=findViewById(R.id.btn_back);
        setTitle("Mail");
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        name.setText("Sender:"+extras.getString("sender"));
        subject.setText("Subject:"+extras.getString("subject"));
        message.setText("Message:"+extras.getString("message"));

        String datestr = extras.getString("date");
        String toDate = parseDateToddMMyyyy(datestr);
        created.setText("Date:"+    toDate);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }

}
