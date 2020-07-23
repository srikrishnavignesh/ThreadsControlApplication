package com.example.threadscontrolapplication.events_list;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.threadscontrolapplication.event_model.Events;
import com.example.threadscontrolapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */

//

public class EventsFragment extends Fragment {

    private static final int UPDATE_RQ =12;
    RecyclerView recycler;
    MyAdapter adapter;
    ArrayList<Events> list;
    private Events tempEvent;
    ProgressBar progressBar;
    TextView progressText;
    boolean downloadedData;
    SwipeRefreshLayout eventRefresh;
    public EventsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_events, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createRecyclerAndAdapter(view);
        if(savedInstanceState==null || !savedInstanceState.getBoolean("download_data")) {
            getDataFromDb();                  //if first time fragment created get data from db or if not have been downloaded yet
        }
        else
        {
            /*get the list from bundle and intialize adapter and recycler view*/
            downloadedData=true;
            list.clear();
            list.addAll((ArrayList<Events>) savedInstanceState.getSerializable("list_events"));
        }


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.events_fragment_menu,menu);
        MenuItem searchEvnt=menu.findItem(R.id.search_events);
        androidx.appcompat.widget.SearchView searchView= (androidx.appcompat.widget.SearchView) searchEvnt.getActionView();
        //search bar to get queries
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void getDataFromDb() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        DatabaseReference eventsRef=FirebaseDatabase.getInstance().getReference("Symposium/Events/");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Events e=ds.getValue(Events.class);
                    list.add(e);
                }
                downloadedData=true;  //once complete data is downloaded
                adapter.setList(list);
                adapter.notifyDataSetChanged(); //notify adapter about data from firebase*/
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
            }
        });
    }
    private void createRecyclerAndAdapter(View view) {
        recycler=(RecyclerView)view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        list=new ArrayList<Events>();
        adapter=new MyAdapter(this,list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        progressBar=(ProgressBar)view.findViewById(R.id.progress);
        progressText=(TextView)view.findViewById(R.id.progress_wait);
        eventRefresh=(SwipeRefreshLayout)view.findViewById(R.id.refresher);
        //refresh to get all events form server once again
        eventRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                getDataFromDb();
                eventRefresh.setRefreshing(false);
            }
        });
    }

    //if an event is selected pass this event as an intent to update its attributes
    public void eventSelected(Events event) {
        Intent intent=new Intent(getContext(), EventUpdateActivity.class);
        intent.putExtra("event",event);
        tempEvent=event;
        startActivityForResult(intent,UPDATE_RQ);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UPDATE_RQ && data!=null)
        {
                Events updated=(Events) data.getSerializableExtra("updated_event");
                list.remove(tempEvent);
                list.add(updated);
                adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("download_data",downloadedData); //if data successfully downloaded from server
        outState.putSerializable("list_events",list); //original list of events from firebase
    }
}
