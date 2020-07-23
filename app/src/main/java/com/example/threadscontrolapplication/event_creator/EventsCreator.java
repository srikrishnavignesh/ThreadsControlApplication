package com.example.threadscontrolapplication.event_creator;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.threadscontrolapplication.DatePickerFragment;
import com.example.threadscontrolapplication.R;
import com.example.threadscontrolapplication.TimePickerFragment;
import com.example.threadscontrolapplication.event_model.Events;
import com.example.threadscontrolapplication.events_list.EventsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */


//to create an event in the symposium
public class EventsCreator extends Fragment implements View.OnClickListener {

    EditText contact;
    EditText name;
    EditText venue;
    EditText desc;
    Button startTimeBtn;
    Button endTimeBtn;
    NumberPicker levelsPicker;
    Button dateBtn;
    Button createBtn;
    Events evnt;
    ProgressBar pb;
    public EventsCreator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wireUpListeners(view);
        if(savedInstanceState!=null)
        {
            evnt=(Events) savedInstanceState.getSerializable("events");
            fillUpWidgets();
        }
    }

    private void fillUpWidgets() {
        dateBtn.setText(evnt.date);
        startTimeBtn.setText(evnt.startTime);
        endTimeBtn.setText(evnt.endTime);
        levelsPicker.setValue(evnt.levels);

    }

    private void wireUpListeners(View view) {
        name=(EditText)view.findViewById(R.id.event_name);
        desc=(EditText)view.findViewById(R.id.event_desc);
        venue=(EditText)view.findViewById(R.id.venue);
        dateBtn=(Button)view.findViewById(R.id.date);
        startTimeBtn=(Button)view.findViewById(R.id.start_time);
        endTimeBtn=(Button)view.findViewById(R.id.end_time);
        levelsPicker=(NumberPicker)view.findViewById(R.id.levels);
        levelsPicker.setMinValue(1);
        levelsPicker.setMaxValue(5);
        createBtn=(Button)view.findViewById(R.id.create_event);
        createBtn.setOnClickListener(this);
        contact=(EditText)view.findViewById(R.id.contact);
        evnt=new Events();
        startTimeBtn.setOnClickListener(this);
        endTimeBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        pb=(ProgressBar)view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.start_time:showTimeDialog((Button)v);
                                    break;
            case R.id.end_time:showTimeDialog((Button)v);
                                    break;
            case R.id.date:showDateDialog((Button)v);
                                    break;
            case R.id.create_event:if(validateInput())
                                        checkForEvntInDataBase();

        }
    }
    //check whether an event bu the same name exists in database
    private void checkForEvntInDataBase() {
        pb.setVisibility(View.VISIBLE);
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Events");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(evnt.name)) {
                    getAlertDialogBox();
                    pb.setVisibility(View.GONE);
                }
                else
                    sendToDataBase();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"error occurred please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //alert dialog box if duplicate event exists
    private void getAlertDialogBox() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMessage("Event already exists please check events list");
        builder.setTitle("Event creation error");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //to create an event in db
    private void sendToDataBase() {
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Events/"+evnt.name);
        dr.setValue(evnt).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Event Created Successfully",Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EventsFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(),"error occurred please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateDialog(Button v) {
        DatePickerFragment df=new DatePickerFragment();
        df.setDate(v);
        df.show(getActivity().getSupportFragmentManager(),"date_dialog");
    }

    private void showTimeDialog(Button v) {
        TimePickerFragment tf=new TimePickerFragment();
        tf.setTime(v);
        tf.show(getActivity().getSupportFragmentManager(),"time_dialog");
    }

    //validate all inputs before updating them in db
    boolean validateInput()  {
        if(name.getText().toString().length()==0 || name.getText().toString().trim().length()==0)
        {
            name.setError("please enter a valid event name");
            return false;
        }
        if(name.getText().toString().trim().contains(" "))
        {
            name.setError("please use _ for space");
            Toast.makeText(getActivity(),"please use _ for space in event name",Toast.LENGTH_LONG).show();
            return false;
        }
        if(desc.getText().toString().trim().length()==0)
        {
            desc.setError("please provide event description");
            return false;
        }
        if(venue.getText().toString().length()==0 || venue.getText().toString().trim().length()==0)
        {
            venue.setError("please enter a valid venue");
            return false;
        }
        if(dateBtn.getText().toString().toLowerCase().equals("date") || dateBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(getContext(),"please pick a date",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(startTimeBtn.getText().toString().toLowerCase().equals("start_time") || startTimeBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(getContext(),"please pick a startTime",Toast.LENGTH_SHORT).show();
            return false;

        }
        if(endTimeBtn.getText().toString().toLowerCase().equals("end_time") || endTimeBtn.getText().toString().trim().length()==0)
        {
            Toast.makeText(getContext(),"please pick a endTime",Toast.LENGTH_SHORT).show();
            return false;

        }
        if(contact.getText().toString().trim().length()==0)
        {
            contact.setError("please provide a valid contact");
            return false;
        }
        createEventFromUi();
        return true;

    }

    private void createEventFromUi() {
        evnt.name=name.getText().toString();
        evnt.venue=venue.getText().toString();
        evnt.date=dateBtn.getText().toString();
        evnt.startTime=startTimeBtn.getText().toString();
        evnt.endTime=endTimeBtn.getText().toString();
        evnt.desc=desc.getText().toString();
        evnt.levels=levelsPicker.getValue();
        evnt.contact=contact.getText().toString();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        createEventFromUi();
        outState.putSerializable("events",evnt);
    }
}
