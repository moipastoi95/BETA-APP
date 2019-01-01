package com.example.george.betamdl;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {
    List<Event> listEvents;

    CardAdapter(List<Event> listEvents) {
        this.listEvents = listEvents;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_item, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHolder cardHolder, int position) {
        cardHolder.Display(listEvents.get(position));
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mDetail;
        private Button evtNowRead;
        private Context context;

        CardHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.titleEvtTv);
            mDetail = (TextView) itemView.findViewById(R.id.detailEvtTv);
            evtNowRead = (Button) itemView.findViewById(R.id.readEvtBtn);
            context = itemView.getContext();
        }

        void Display(final Event event) {
            if (event.getTitle().length() > 23)
                mTitle.setText(event.getTitle().substring(0, 19)+" ...");
            else
                mTitle.setText(event.getTitle());

            if (event.getDetail().length() > 63)
                mDetail.setText(event.getDetail().substring(0, 61)+" ...");
            else
                mDetail.setText(event.getDetail());

            evtNowRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,EventFullActivity.class);
                    intent.putExtra("event", event);
                    context.startActivity(intent);
                }
            });
        }
    }
}
