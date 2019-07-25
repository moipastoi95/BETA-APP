package com.example.george.betamdl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

public class CardAdapterClub extends RecyclerView.Adapter<CardAdapterClub.CardHolderClub>{

    List<Club> clubList;

    public CardAdapterClub(List<Club> clubList) {
        this.clubList = clubList;
    }

    @NonNull
    @Override
    public CardHolderClub onCreateViewHolder(@NonNull ViewGroup parent, int ViewTypes) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        view = layoutInflater.inflate(R.layout.club_item, parent, false);
        return new CardHolderClub(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolderClub cardHolderClub, int position) {
        cardHolderClub.Display(clubList.get(position));
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    class CardHolderClub extends RecyclerView.ViewHolder {
        private TextView titleClubTv, detailsClubTv;
        private Button clubSideBtn;
        private Context context;
        private CardView cardView;

        public CardHolderClub(View itemView) {
            super(itemView);

            titleClubTv = itemView.findViewById(R.id.titleClubTv);
            detailsClubTv = itemView.findViewById(R.id.detailsClubTv);
            clubSideBtn = itemView.findViewById(R.id.clubSideBtn);
            cardView = itemView.findViewById(R.id.cardViewClub);

            context = itemView.getContext();
        }

        private void Display(final Club club) {

            titleClubTv.setText(club.getTitleClub());
            detailsClubTv.setText(club.getDetailsClub());

            // quand on clique sur une card
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mettre les paramètres pour que les cases soient pré-remplis
                    Intent intent = new Intent(context,AddClubActivity.class);
                    intent.putExtra("club", club);
                    context.startActivity(intent);
                }
            });

            // quand on clique sur la poubelle rouge
            clubSideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Confirmer ?");
                    alertDialogBuilder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    new deleteItemClub(club).execute();
                                    int position = getAdapterPosition();
                                    clubList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, clubList.size());
                                    Toast.makeText(context,"Club supprimé ! ",Toast.LENGTH_LONG).show();
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
            });
        }
    }

    class deleteItemClub extends AsyncTask<Void,Void,Void> {
        Connection conn;
        Statement stmt;

        private Club club;

        public deleteItemClub(Club club) {
            this.club = club;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                // supprime le club
                stmt.executeUpdate("DELETE FROM clubdb WHERE idClub="+club.getIdClub());

                // supprime les events associés au club
                stmt.executeUpdate("DELETE FROM eventsdb WHERE idClub="+club.getIdClub());

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
