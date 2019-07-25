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

public class CardAdapterMembers extends RecyclerView.Adapter<CardAdapterMembers.CardHolderMember> {

    List<User> userList;

    CardAdapterMembers(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public CardHolderMember onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new CardHolderMember(view);
    }

    @Override
    public void onBindViewHolder(CardHolderMember cardHolderMember, int position) {
        cardHolderMember.Display(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class CardHolderMember extends RecyclerView.ViewHolder {

        private TextView nameUserTv;
        private TextView fNameUserTv;
        private TextView lvlAccUserTv;
        private TextView passwordUserTv;
        private Button userSideBtn;
        private CardView cardView;
        private Context context;

        public CardHolderMember(View itemView) {
            super(itemView);

            nameUserTv = itemView.findViewById(R.id.nameUserTv);
            fNameUserTv = itemView.findViewById(R.id.fNameUserTv);
            lvlAccUserTv = itemView.findViewById(R.id.lvlAccUserTv);
            passwordUserTv = itemView.findViewById(R.id.passwordUserTv);
            userSideBtn = itemView.findViewById(R.id.userSideBtn);
            cardView = itemView.findViewById(R.id.cardViewUserNow);

            context = itemView.getContext();
        }

        private void Display(final User user) {

            nameUserTv.setText("Prenom :" +user.getNameUser());
            fNameUserTv.setText("Nom :" +user.getFamilyNameUser());
            lvlAccUserTv.setText("Niveau d'accès :" +String.valueOf(user.getLevelAccess()));
            passwordUserTv.setText("mot de passe :" +user.getPasswordUser());

            // quand on clique sur une card
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mettre les paramètres pour que les cases soient pré-remplis
                    Intent intent = new Intent(context,AddUserActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });

            // quand on clique sur la poubelle rouge
            userSideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Confirmer ?");
                    alertDialogBuilder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    new deleteItem(user).execute();
                                    userList.remove(getAdapterPosition());
                                    int position = getAdapterPosition();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, userList.size());


                                    Toast.makeText(context,"Utilisateur supprimé ! ",Toast.LENGTH_LONG).show();
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

    class deleteItem extends AsyncTask<Void,Void,Void> {
        Connection conn;
        Statement stmt;

        private User user;

        public deleteItem(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                stmt.executeUpdate("DELETE FROM accountdb WHERE idUser=" + user.getIdUser());

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