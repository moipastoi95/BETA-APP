package com.example.george.betamdl;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {

    List<Event> listEvents;
    public static int SPECTATOR_MODE = 0;
    public static int EDIT_MODE = 1;
    private int mode;
    private Context mContext;

    CardAdapter(List<Event> listEvents, int mode) {
        this.listEvents = listEvents;
        this.mode = mode;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        if (mode == SPECTATOR_MODE) {
            view = layoutInflater.inflate(R.layout.event_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.event_item_admin, parent, false);
        }
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHolder cardHolder, int position) {
        cardHolder.Display(listEvents.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        TextView mTitle;
        TextView mDetail;
        TextView mEvtDate;
        Button evtSideBtn;
        CardView cardView;
        Context context;

        CardHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.titleEvtTv);
            mDetail = itemView.findViewById(R.id.detailEvtTv);
            mEvtDate = itemView.findViewById(R.id.dayEvtTv);
            if (mode == EDIT_MODE) {
                evtSideBtn = itemView.findViewById(R.id.evtSideBtn);
            }
            cardView = itemView.findViewById(R.id.cardViewEvtNow);

            context = itemView.getContext();
        }

        void Display(final Event event, int position) {

            mTitle.setText(event.getTitle());
            //"Pos :"+position+ "\n adpaPos :"+getAdapterPosition()+ "\n"+
            mDetail.setText(event.getDetail());
            mEvtDate.setText(event.getStringDayOfEvent());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode == SPECTATOR_MODE)  {
                        Intent intent = new Intent(context,EventDetailActivity.class);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                        //Toast.makeText(context,"Pos" +position+" ApdaptPos :"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    } else if (mode == EDIT_MODE) {
                        // mettre les paramètres pour que les cases soient pré-remplis
                        Intent intent = new Intent(context,AddEventActivity.class);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                    }
                }
            });
            if (mode == EDIT_MODE) {
                evtSideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mode == EDIT_MODE) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setMessage("Confirmer ?");
                            alertDialogBuilder.setPositiveButton("Oui",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            // supprimer l'event dans la base de données
                                            new deleteItem(event).execute();
                                            // supprime l'event dans la liste affiché
                                            listEvents.remove(getAdapterPosition());
                                            int position = getAdapterPosition();
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, listEvents.size());


                                            Toast.makeText(context,"Evenement supprimé ! ",Toast.LENGTH_LONG).show();
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ;
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                });
            }
        }
    }

    class deleteItem extends AsyncTask<Void,Void,Void>{
        Connection conn;
        Statement stmt;

        private Event event;

        public deleteItem(Event event) {
            this.event = event;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                stmt.executeUpdate("DELETE FROM eventsdb WHERE idEvent=" + event.getIdEvent());

                stmt.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null)
                        stmt.close();
                } catch (SQLException se2) {
                }// nothing we can do
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void aVoid) { }
    }
}
