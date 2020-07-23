package com.example.threadscontrolapplication.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.threadscontrolapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//this class receives a list of participants from notificationFragment
//we access firebase db
//search for each participants and push the message onto their message list
//the firebase automatically triggers a function that sends push notification to each user when a message is added to their message list
public class NotifyParticipants extends AppCompatActivity implements View.OnClickListener {
    Button sendNotification;
    EditText notification;
    ProgressBar pb;
    Toolbar toolbar;
    ArrayList<Participants> listParticipants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_participants);
        wireUpListeners();
    }

    private void wireUpListeners() {
        sendNotification=(Button)findViewById(R.id.p_send_notification);
        notification=(EditText)findViewById(R.id.p_notification);
        pb=(ProgressBar)findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
        sendNotification.setOnClickListener(this);
        listParticipants=(ArrayList<Participants>)getIntent().getSerializableExtra("list_participants");
        //get all participants to be notified as a list
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("notify "+listParticipants.size()+" participant(s)");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.p_send_notification:sendNotificationsToDb();
                                     break;
        }
    }

    private void sendNotificationsToDb() {
        pb.setVisibility(View.VISIBLE);
        //get reference to users data
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //search for each user
               for(DataSnapshot ds:dataSnapshot.getChildren()) {
                   for (Participants p : listParticipants) {
                       //if a match push messages to each users messageList
                       if (p.userId.equals(ds.getKey()))
                           ds.child("messages").getRef().push().setValue(notification.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {

                               }
                           });
                   }
               }
               pb.setVisibility(View.GONE);
               Toast.makeText(NotifyParticipants.this,"message sent successfully",Toast.LENGTH_SHORT).show();
               finish();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
    //to handle back pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
