package com.example.threadscontrolapplication.events_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.threadscontrolapplication.DatePickerFragment;
import com.example.threadscontrolapplication.event_model.Events;
import com.example.threadscontrolapplication.R;
import com.example.threadscontrolapplication.TimePickerFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    EditText venue;
    NumberPicker levelsPicker;
    TextView eventName;
    TextView eventDesc;
    SeekBar levelProgress;
    Events evnt;
    Button dateBtn;
    Button startTimeBtn;
    Button endTimeBtn;
    Button updateBtn;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_update);
        evnt=(Events)getIntent().getSerializableExtra("event");
        wireUpListeners();
        fillWidgets();
    }

    private void fillWidgets() {
        eventName.setText(evnt.name);
        eventDesc.setText(evnt.desc);
        venue.setText(evnt.venue);
        levelsPicker.setMinValue(1);
        levelsPicker.setMaxValue(evnt.levels);
        levelsPicker.setValue(evnt.currentLevel);
        levelProgress.setProgress(evnt.progress);
        dateBtn.setText(evnt.date);
        startTimeBtn.setText(evnt.startTime);
        endTimeBtn.setText(evnt.endTime);

    }

    private void wireUpListeners() {
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventName=(TextView)findViewById(R.id.event_name);
        eventDesc=(TextView)findViewById(R.id.event_desc);
        levelsPicker=(NumberPicker)findViewById(R.id.num_levels);
        venue=(EditText)findViewById(R.id.event_venue);
        levelProgress=(SeekBar)findViewById(R.id.progress);
        startTimeBtn=(Button)findViewById(R.id.start_time);
        endTimeBtn=(Button)findViewById(R.id.end_time);
        dateBtn=(Button)findViewById(R.id.event_date);
        updateBtn=(Button)findViewById(R.id.update_event);
        updateBtn.setOnClickListener(this);
        pb=(ProgressBar)findViewById(R.id.progress_bar);
        startTimeBtn.setOnClickListener(this);
        endTimeBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.update_event:updateValuesToDb();
                                    break;
            case R.id.event_date:showDateDialog();
                                    break;
            case R.id.start_time:showTimeDialog(startTimeBtn);
                                    break;
            case R.id.end_time:showTimeDialog(endTimeBtn);
                                    break;
        }
    }

    private void showTimeDialog(Button time) {
        TimePickerFragment tf=new TimePickerFragment();
        tf.setTime(time);
        tf.show(getSupportFragmentManager(),"time_dialog");
    }

    private void showDateDialog() {
        DatePickerFragment df=new DatePickerFragment();
        df.setDate(dateBtn);
        df.show(getSupportFragmentManager(),"date_dialog");
    }

    private void updateValuesToDb() {
        if(validateInput())
        {
            pb.setVisibility(View.VISIBLE);
            DatabaseReference dr= FirebaseDatabase.getInstance().getReference("Symposium/Events/"+evnt.name);
            HashMap<String,Object> map=new HashMap<>();
            map.put("currentLevel",evnt.currentLevel);
            map.put("date",evnt.date);
            map.put("startTime",evnt.startTime);
            map.put("endTime",evnt.endTime);
            map.put("progress",evnt.progress);
            dr.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"updated event successfully",Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent();
                    intent.putExtra("updated_event",evnt);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"error occured please check connection",Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            });


        }

    }

    boolean validateInput()  {

        if(venue.getText().toString().length()==0 || venue.getText().toString().trim().length()==0)
        {
            venue.setError("please enter a valid venue");
            return false;
        }
        if(dateBtn.getText().toString().toLowerCase().equals("date") || dateBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(this,"please pick a date",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(startTimeBtn.getText().toString().toLowerCase().equals("start_time") || startTimeBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(this,"please pick a startTime",Toast.LENGTH_SHORT).show();
            return false;

        }
        if(endTimeBtn.getText().toString().toLowerCase().equals("end_time") || endTimeBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(this,"please pick a endTime",Toast.LENGTH_SHORT).show();
            return false;

        }
        evnt.venue=venue.getText().toString();
        evnt.date=dateBtn.getText().toString();
        evnt.startTime=startTimeBtn.getText().toString();
        evnt.endTime=endTimeBtn.getText().toString();
        evnt.currentLevel=levelsPicker.getValue();
        evnt.progress=levelProgress.getProgress();
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
