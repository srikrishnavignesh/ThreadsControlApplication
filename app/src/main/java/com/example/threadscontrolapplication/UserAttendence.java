package com.example.threadscontrolapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.threadscontrolapplication.notification.Participants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
//the class gets all users from remote db
//displays info about about total attendees,total payed,participants from 1st year,2nd year,3rd year,4th year,cse and other dept
public class UserAttendence extends Fragment {


    public UserAttendence() {
        // Required empty public constructor
    }
    TextView total;
    TextView cse;
    TextView paid;
    TextView firstYear;
    TextView secondYear;
    TextView thirdYear;
    TextView otherDept;
    ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_attendence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wireUpListeners(view);

        //if first time get data from db
        if(savedInstanceState==null)
            getDataFromDb();
        else
        {
            pb.setVisibility(View.GONE);
            //fill widgets with data
            fillWidgetsWithData(savedInstanceState);
        }
    }

    private void fillWidgetsWithData(Bundle saved) {

        //get all saved data from the bundle
        total.setText(saved.getInt("total")+"");
        paid.setText(saved.getInt("paid")+"");
        cse.setText(saved.getInt("cse")+"");
        otherDept.setText(saved.getInt("other")+"");
        firstYear.setText(saved.getInt("first")+"");
        secondYear.setText(saved.getInt("second")+"");
        thirdYear.setText(saved.getInt("third")+"");

    }

    private void getDataFromDb() {
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants");

        //single event listener for displaying each participant info
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pb.setVisibility(View.GONE);
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Participants p=ds.getValue(Participants.class);
                    setYearText(p.year);
                    setDeptText(p.dept);
                    total.setText((Integer.parseInt(total.getText().toString())+1)+"");
                    setPaymentText(p.payment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setPaymentText(boolean isPaid) {
        if(isPaid)
        {
            paid.setText((Integer.parseInt(paid.getText().toString())+1)+"");
        }
    }

    private void setDeptText(String dept) {
        if(dept.toLowerCase().equals("cse"))
        {
            cse.setText((Integer.parseInt(cse.getText().toString())+1)+"");
        }
        else
        {
            otherDept.setText((Integer.parseInt(otherDept.getText().toString())+1)+"");
        }
    }

    private void setYearText(int yr) {
        switch(yr)
        {
            case 1:firstYear.setText((Integer.parseInt(firstYear.getText().toString())+1)+"");
                    break;
            case 2:secondYear.setText((Integer.parseInt(secondYear.getText().toString())+1)+"");
                    break;
            case 3:thirdYear.setText((Integer.parseInt(thirdYear.getText().toString())+1)+"");
                    break;
        }
    }

    private void wireUpListeners(View view) {
        total=view.findViewById(R.id.total);
        cse=view.findViewById(R.id.total);
        paid=view.findViewById(R.id.total_paid);
        firstYear=view.findViewById(R.id.first_year);
        secondYear=view.findViewById(R.id.second_year);
        thirdYear=view.findViewById(R.id.third_year);
        otherDept=view.findViewById(R.id.other);
        pb=view.findViewById(R.id.progress);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the data onto the bundle
        outState.putInt("total",Integer.parseInt(total.getText().toString()));
        outState.putInt("paid",Integer.parseInt(paid.getText().toString()));
        outState.putInt("cse",Integer.parseInt(cse.getText().toString()));
        outState.putInt("other",Integer.parseInt(otherDept.getText().toString()));
        outState.putInt("first",Integer.parseInt(firstYear.getText().toString()));
        outState.putInt("second",Integer.parseInt(secondYear.getText().toString()));
        outState.putInt("third",Integer.parseInt(thirdYear.getText().toString()));
    }
}
