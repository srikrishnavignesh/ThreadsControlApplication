package com.example.threadscontrolapplication.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.threadscontrolapplication.R;

//activity that displays complete details of a participant
//the participant is received as a intent from NotificationFragment
//it displays
//participant  Name,participant year,dept,collegeName,Participant payment info
public class ShowParticipant extends AppCompatActivity {
    Participants p;
    TextView name;
    TextView collegeName;
    TextView payment;
    TextView dept;
    TextView year;
    TextView phoneNumber;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_participant);
        p=(Participants) getIntent().getSerializableExtra("participant");

        wireUplisteners();

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User_details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillViewWithData();
    }

    private void fillViewWithData() {
        name.setText(p.name);
        collegeName.setText(p.collegeName);
        if(p.payment)
            payment.setText("payment_completed");
        else
            payment.setText("payment_not_completed");
        dept.setText(p.dept);
        year.setText(p.year+"");
        phoneNumber.setText(p.phoneNumber);
    }


    private void wireUplisteners() {
        name=(TextView)findViewById(R.id.p_name);
        collegeName=(TextView)findViewById(R.id.p_college);
        payment=(TextView)findViewById(R.id.p_payment);
        dept=(TextView)findViewById(R.id.p_dept);
        year=(TextView)findViewById(R.id.p_year);
        phoneNumber=(TextView)findViewById(R.id.p_phone_number);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:finish();
                                    break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
