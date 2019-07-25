package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class event_admin_list extends AppCompatActivity {

    private List<Event> allEvtList;
    Statement stmt;
    Connection conn;
    RecyclerView EvtRv;
    CardAdapter cardAdapter;
    ProgressBar progressBar;
    TextView noEvtTv;

    int ownId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_admin_list);

        setTitle("Tous les évenements");

        Intent intent = getIntent();
        ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));

        progressBar = findViewById(R.id.prb2);
        noEvtTv = findViewById(R.id.noEvtTv);
    }

    public void refreshCards(List<Event> allEvtList) {
        //Repérage du RecyclerView
        EvtRv = (RecyclerView) findViewById(R.id.allEvtRv);
        noEvtTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if (allEvtList.size() != 0) {
            noEvtTv.setVisibility(View.GONE);
            // Création CardAdapter
            cardAdapter = new CardAdapter(allEvtList, CardAdapter.EDIT_MODE);
            EvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            EvtRv.setAdapter(cardAdapter);
        } else {
            noEvtTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new eventListDbCo(ownId).execute();
    }

    class eventListDbCo extends AsyncTask<Void, Void,List> {
        int id;
        public eventListDbCo(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List eventList) {
            refreshCards(eventList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allEvtList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                // regarde le niveau d'accès de l'utilisateur
                ResultSet resultSet = stmt.executeQuery("SELECT levelAccess FROM accountdb WHERE idUser='"+String.valueOf(id)+"'");
                int levelAccess = 0;
                if(resultSet.next()) {
                    levelAccess = resultSet.getInt("levelAccess");
                }
                // Affiche les événements en conséquence
                // requête n°1 pour avoir les objects dans eventsdb, sans nameClub
                if (levelAccess >= 2) {
                    resultSet = stmt.executeQuery("SELECT * FROM eventsdb ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC");
                } else {
                    resultSet = stmt.executeQuery("SELECT * FROM eventsdb WHERE idAuthor="+id+" ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC");
                }

                while(resultSet.next()){
                    // prend les valeurs dans depuis la requête
                    int id  = resultSet.getInt("idEvent");
                    String title  = resultSet.getString("titleEvent");
                    String detail = resultSet.getString("detailsEvent");
                    String titleClub  = "Error name";
                    int idClub  = resultSet.getInt("idClub");

                    Date dateOfCreation = resultSet.getDate("dayOfCreation");
                    Date dateOfEvent = resultSet.getTimestamp("dayOfEvent");
                    Date dateEndOfEvent = resultSet.getTimestamp("dayEndOfEvent");

                    Event evt = new Event(id,title,detail,titleClub,idClub);
                    evt.setDayOfCreation(dateOfCreation);
                    evt.setDayOfEvent(dateOfEvent);
                    evt.setDayEndOfEvent(dateEndOfEvent);
                    allEvtList.add(evt);
                }
                //Si 1er requête à fonctionné :
                // requête n°2 pour associé idClub à un nameClub trouvé dans clubdb
                resultSet = stmt.executeQuery("SELECT idClub, titleClub FROM clubdb");
                int objectSetted=0;
                while (resultSet.next() && allEvtList.size()>objectSetted) {
                    int idClub = resultSet.getInt("idClub");
                    String titleClub = resultSet.getString("titleClub");

                    for(Event evtTemp : allEvtList) {
                        if (evtTemp.getIdClubLinked() == idClub) {
                            evtTemp.setTitleClubLinked(titleClub);
                            objectSetted++;
                        }
                    }
                }


                resultSet.close();
                stmt.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }finally{
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            return allEvtList;
        }
    }
}
