package com.example.threadscontrolapplication.notification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.threadscontrolapplication.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> implements Filterable,ActionMode.Callback {
    NotificationFragment nf;
    ArrayList<Participants> list;
    ArrayList<Participants> listFull;
    ActionMode modeBar;
    int counter;
    ArrayList<Participants> selectedParticipants;
    void setList(ArrayList<Participants> participantsList)
    {
        list=participantsList;
        listFull=participantsList;
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        counter=0;
        modeBar=mode;
        menu.add("notify");
        return true;
    }
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        //move to notifyParicipants activity to send a group message to all participants
        nf.notifyParticipants(selectedParticipants);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
            selectedParticipants.clear();
            modeBar=null;
            counter=0;
            notifyDataSetChanged();
    }
    NotificationAdapter(NotificationFragment nf, ArrayList<Participants> list)
    {
        this.nf=nf;
        this.list=list;
        listFull=list;
        selectedParticipants=new ArrayList<Participants>();
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list,parent,false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationHolder holder, int position) {
        holder.pName.setText(list.get(position).name);
        holder.pPhoneNumber.setText(list.get(position).phoneNumber);
        holder.user.setImageResource(getImage(position));
        if(list.get(position).payment)
            holder.payment.setImageResource(R.drawable.payment_done);
        //if action mode is false,all items appear white
        if(modeBar==null)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(nf.getContext(),R.color.colorAccent));
        else
        {
            //selected participants are gray rest are white
            if(selectedParticipants.contains(list.get(position)))
                    holder.itemView.setBackgroundColor(Color.GRAY);
            else
                holder.itemView.setBackgroundColor(ContextCompat.getColor(nf.getContext(),R.color.colorAccent));
        }
    }

    private int getImage(int pos) {
        String userImg[]=nf.getResources().getStringArray(R.array.user_types);
        int index=pos%userImg.length;
        int id=nf.getResources().getIdentifier(userImg[index],"drawable",nf.getContext().getPackageName());
        return id;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return ParticipantsFilter;
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        TextView pName;
        TextView pPhoneNumber;
        ImageView payment;
        ImageView user;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            pName=(TextView)itemView.findViewById(R.id.participant_name);
            pPhoneNumber=itemView.findViewById(R.id.phone_number);
            payment=(ImageView)itemView.findViewById(R.id.payment);
            user=(ImageView)itemView.findViewById(R.id.user_img);

            //click to get participants complete detail
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modeBar!=null)
                        setSelection(list.get(getAdapterPosition()));
                    else
                        nf.showParticipant(list.get(getAdapterPosition()));
                }
            });

            //onLongClickListener to select multiple participants to notify them
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(modeBar==null) {
                        startActionMode();
                        setSelection(list.get(getAdapterPosition()));
                    }
                    return true;
                }
            });

        }
        //make selected items gray in color
        //other items appear white
        private void setSelection(Participants participants) {
                    if(!selectedParticipants.contains(participants))
                    {
                        selectedParticipants.add(participants);
                        itemView.setBackgroundColor(Color.GRAY);
                        updateModeBar();
                    }
                    else
                    {
                        selectedParticipants.remove(participants);
                        itemView.setBackgroundColor(Color.WHITE);
                        updateModeBar();
                    }
        }
    }

    //a dark action bar appears
    void startActionMode() {
        ((AppCompatActivity)nf.getActivity()).startSupportActionMode(this);
    }

    //a selected participants number appear in action bar
    void updateModeBar()
    {
        modeBar.setTitle(selectedParticipants.size()+" item(s) selected");
    }

    //when activity is recreated make selected participants gray again
    void setListSelectedParticipants(ArrayList<Participants> list)
    {
        selectedParticipants.clear();
        selectedParticipants.addAll(list);
    }


    //only pick particpants whose name or phone Number matches with the text in search bar
    Filter ParticipantsFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String search=constraint.toString();
            ArrayList<Participants> searchList=new ArrayList<Participants>();
            if(search==null || search.length()==0)
                searchList.addAll(listFull);
            else
            {
                for(Participants p:listFull)
                {
                    if(p.phoneNumber.startsWith(search) || p.name.toLowerCase().startsWith(search.toLowerCase()))
                        searchList.add(p);
                }
            }
            FilterResults results=new FilterResults();
            results.values=searchList;
            return results;
        }

        //make few participants selected
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list=null;
            list=new ArrayList<Participants>();
            list.addAll((ArrayList<Participants>)results.values);
            notifyDataSetChanged();
        }
    };
}
