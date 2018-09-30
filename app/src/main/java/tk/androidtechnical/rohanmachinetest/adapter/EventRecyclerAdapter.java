package tk.androidtechnical.rohanmachinetest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import tk.androidtechnical.rohanmachinetest.R;
import tk.androidtechnical.rohanmachinetest.model.Event;



public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.DrawerDataHolder> {

    ArrayList<Event> events;
    Context context;
    int type;

    public EventRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setEvents(ArrayList<Event> events){
        this.events = events;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public DrawerDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_event, parent, false);
        DrawerDataHolder drawerDataHolder = new DrawerDataHolder(v);
        return drawerDataHolder;
    }

    @Override
    public void onBindViewHolder(final DrawerDataHolder holder, final int position) {
        final Event event = events.get(position);
        final DrawerDataHolder hold = holder;
        if (type == 1){
            holder.txt_date_time.setText(event.getTime());
        } else {
            holder.txt_date_time.setText(event.getDate() + " " + event.getTime());
        }
        holder.txt_agenda.setText(event.getAgenda());
        holder.txt_paticipants.setText(event.getParticipants());
    }

    public class DrawerDataHolder extends RecyclerView.ViewHolder {

        TextView txt_date_time;
        TextView txt_agenda, txt_paticipants;

        public DrawerDataHolder(View v) {
            super(v);
            txt_date_time = (TextView) v.findViewById(R.id.txt_date_time);
            txt_agenda = (TextView) v.findViewById(R.id.txt_agenda);
            txt_paticipants = (TextView) v.findViewById(R.id.txt_paticipants);
        }
    }
}

