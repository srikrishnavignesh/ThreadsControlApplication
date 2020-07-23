package com.example.threadscontrolapplication.notification;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.threadscontrolapplication.R;
import com.example.threadscontrolapplication.event_model.Events;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */

//the fragment handles notifying each participant and displaying their info
//the participants are displayed as a list view
//onLongPress a participant is selected
//multiple participants can be selected to send a push notification to them
//on clicking a view we get complete details about a participant
public class NotificationFragment extends Fragment {

    private static final int REQUEST_NOTIFY = 12;
    private static final int REQUEST_PARTICIPANT_SHOW = 13;
    RecyclerView recycler;
    NotificationAdapter adapter;
    ArrayList<Participants> list;
    ProgressBar progress;
    TextView progressText;
    boolean downloadedData;
    SwipeRefreshLayout refresh;
    public NotificationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list=new ArrayList<Participants>();
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        //create listView for participants
        createRecyclerViewAndAdapter(view);

        //get data from db
        if(savedInstanceState==null || !savedInstanceState.getBoolean("download_data"))
            getFromDatabase();
        else
        {
            /*get the list from bundle and intialize adapter and recycler view*/
            downloadedData=true;
            list.clear();
            list.addAll((ArrayList<Participants>) savedInstanceState.getSerializable("list_participants"));
            boolean actionModeIsOn=savedInstanceState.getBoolean("action_mode",false);
            /*if previously when fragment's actionmode is on get selected events*/
            if(actionModeIsOn)
            {
                ArrayList<Participants> evntlist=(ArrayList<Participants>) savedInstanceState.getSerializable("selected_participants");
                adapter.setListSelectedParticipants(evntlist);
                adapter.startActionMode();       /*startactionmode,update mode bar's title and notifydata set changed*/
                adapter.updateModeBar();
            }
        }
    }

    private void createRecyclerViewAndAdapter(View view) {
        recycler=(RecyclerView)view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);
        adapter=new NotificationAdapter(this,list);
        recycler.setAdapter(adapter);
        progress=(ProgressBar)view.findViewById(R.id.progress);
        progressText=(TextView)view.findViewById(R.id.progress_text);
        refresh=(SwipeRefreshLayout)view.findViewById(R.id.refresher);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                getFromDatabase();
                refresh.setRefreshing(false);
            }
        });
    }

    private void getFromDatabase() {
        progressText.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);

        //get All participants from db
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren()) {
                    list.add(snap.getValue(Participants.class));
                    list.get(list.size()-1).userId=snap.getKey(); //get uniqued id generated by firebase and use it as userID
                }
                progressText.setVisibility(GONE);
                progress.setVisibility(GONE);
                adapter.notifyDataSetChanged();
                downloadedData=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressText.setVisibility(GONE);
                progress.setVisibility(GONE);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.events_fragment_menu,menu);
        MenuItem searchEvnt=menu.findItem(R.id.search_events);
        androidx.appcompat.widget.SearchView searchView= (androidx.appcompat.widget.SearchView) searchEvnt.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    //to notify participants move to notifyParticipants Activity
    public void notifyParticipants(ArrayList<Participants> list) {
        Intent intent=new Intent(getContext(),NotifyParticipants.class);
        intent.putExtra("list_participants",list);
        startActivityForResult(intent,REQUEST_NOTIFY);
    }

    //to show details about participant move to ShowParticipant activity
    public void showParticipant(Participants p) {
        Intent intent=new Intent(getContext(),ShowParticipant.class);
        intent.putExtra("participant",p);
        startActivityForResult(intent,REQUEST_PARTICIPANT_SHOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_NOTIFY)
            adapter.modeBar.finish();
    }

    //store whether data has been downloaded
    //if few participants are selected,make action mode true and obtain the list of selected participants
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(adapter.modeBar!=null)
        {
            outState.putBoolean("action_mode",true);
            outState.putSerializable("selected_participants",adapter.selectedParticipants);
        }
        outState.putBoolean("download_data",downloadedData);
        outState.putSerializable("list_participants",list);
    }
}
