package com.example.threadscontrolapplication.events_list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.threadscontrolapplication.event_model.Events;
import com.example.threadscontrolapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

//displays events in the symposium as a list
//each event uses a different random font and random backGroundColor
//on pressing an event it directs to updateEventActivity
//onLongPress enables multiple event selection fro deletion
class MyAdapter extends RecyclerView.Adapter<MyAdapter.EventHolder> implements Filterable{
    ArrayList<Events> list;
    ArrayList<Events> listFull;
    EventsFragment ef;
    int count=0;
    AlertDialog dialog;
    MyAdapter(EventsFragment listener,ArrayList<Events> list)
    {
        this.ef=listener;
        this.list=list;
        listFull=list;
    }
    void setList(ArrayList<Events> eventList)
    {
        this.list=eventList;
        this.listFull=eventList;
    }
    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.events_list,parent,false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        holder.eventName.setText(list.get(position).name);
        holder.date.setText(list.get(position).date);
        holder.time.setText(list.get(position).startTime);
        Typeface type=getFont(position);
        holder.eventName.setTypeface(type);
        holder.venue.setText(list.get(position).venue);
        holder.itemView.setBackgroundColor(getColor(position));
    }
    //get a random color and set it to the view
    private int getColor(int pos) {
        String color[]=ef.getContext().getResources().getStringArray(R.array.color_types);
        Random r=new Random();
        int index=(int)(Math.abs(r.nextLong())%color.length);
        int id=ef.getContext().getResources().getIdentifier(color[index],"color",ef.getContext().getPackageName());
        return ContextCompat.getColor(ef.getContext(),id);
    }

    //get a random font and set it to the view
    private Typeface getFont(int pos) {
        String font[]=ef.getContext().getResources().getStringArray(R.array.font_types);
        Random r=new Random();
        int index=(int)(Math.abs(r.nextLong())%font.length);
        int id=ef.getContext().getResources().getIdentifier(font[index],"font",ef.getContext().getPackageName());
        return ResourcesCompat.getFont(ef.getContext(),id);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return eventsFilter;
    }

    //filter out events list by name
    private Filter eventsFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String search=constraint.toString();
            ArrayList<Events> filteredList;
            if(search==null || search.trim().length()==0)
                filteredList=listFull;
            else
            {
                filteredList=new ArrayList<Events>();
                for(Events evnt:listFull)
                {
                    if(evnt.name.toLowerCase().startsWith(search))
                        filteredList.add(evnt);
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list=new ArrayList<Events>();
            list.addAll((ArrayList<Events>)results.values);
            notifyDataSetChanged();
        }
    };


    public class EventHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView date;
        TextView time;
        TextView venue;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            date=itemView.findViewById(R.id.date);
            time=itemView.findViewById(R.id.time);
            venue=itemView.findViewById(R.id.venue);

            //on click to update
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ef.eventSelected(list.get(getAdapterPosition()));                 //add them to selected events list
                }
            });

            //onLongClick to enable multiple selection
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteItem(list.get(getAdapterPosition()));
                    return true;
                }
            });

        }
    }

    //an alertDialogBox to enable deletion
    private void deleteItem(final Events event) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ef.getContext());
        LayoutInflater inflater=ef.getActivity().getLayoutInflater();
        View view=(View)inflater.inflate(R.layout.alert_dialog_delete,null);
        builder.setView(view);
        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ef.progressBar.setVisibility(View.VISIBLE);
                deleteFromDb(event);
                dialog.cancel();
            }
        });
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog=builder.create();
        dialog.show();
    }

    //to delete the corresponding event from db
    private void deleteFromDb(final Events event) {
       DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Events/"+event.name);
       dr.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Toast.makeText(ef.getContext(),"event deleted successfully",Toast.LENGTH_LONG).show();
                list.remove(event);
                listFull.remove(event);
                notifyDataSetChanged();
                ef.progressBar.setVisibility(View.GONE);
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(ef.getContext(),"error occurred please try again",Toast.LENGTH_LONG).show();
               ef.progressBar.setVisibility(View.GONE);
           }
       });
    }

}
