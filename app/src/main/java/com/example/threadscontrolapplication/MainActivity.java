package com.example.threadscontrolapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.threadscontrolapplication.event_creator.EventsCreator;
import com.example.threadscontrolapplication.events_list.EventsFragment;
import com.example.threadscontrolapplication.notification.NotificationFragment;
import com.google.android.material.navigation.NavigationView;

//the mainActivity simply hosts a navigationDrawer that displays a fragment based on option selected
//the navigation drawer provides the following options
//by default events list is displayed
//option for creating events in real-time
//option for targeting individual users and notifying them
//option for checking whether a user has carried out payment
public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView eventsNavigator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get reference to eac view and enable them fro selection
        wireUpListeners();

        //display events List initially when no option is selected
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new EventsFragment()).commit();
            eventsNavigator.setCheckedItem(R.id.events);
        }
    }

    private void wireUpListeners() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventsNavigator=(NavigationView)findViewById(R.id.navigation_view);
        eventsNavigator.setNavigationItemSelectedListener(this);
        drawer=(DrawerLayout)findViewById(R.id.drawer_navigation);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }



    //when an option is selected in navigation drawer enable the corresponding fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.events:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EventsFragment()).commit();
                        break;
            case R.id.create_event:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EventsCreator()).commit();
                        break;
            case R.id.notify:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotificationFragment()).commit();
                        break;
            case R.id.total_present:getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserAttendence()).commit();
                        break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
