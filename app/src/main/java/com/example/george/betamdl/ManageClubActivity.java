package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageClubActivity extends AppCompatActivity {

    int ownId;
    //loader
    ProgressBar progressbar;

    // layout
    RecyclerView allClubRv;
    CardAdapterClub cardAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton addClubFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_club);

        setTitle("Gérer les clubs");

        Intent intent = getIntent();
        ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));

        // layout
        progressBar = findViewById(R.id.progressBarClub);
        progressBar.setVisibility(View.VISIBLE);
        addClubFab = findViewById(R.id.addClubFab);
        addClubFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageClubActivity.this, AddClubActivity.class);
                startActivity(intent);
            }
        });
    }

    public void refreshCards(List<Club> allClubList) {
        //Repérage du RecyclerView
        allClubRv = findViewById(R.id.allClubsRv);

        progressBar.setVisibility(View.GONE);

        // Création CardAdapter
        cardAdapter = new CardAdapterClub(allClubList);
        allClubRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        allClubRv.setAdapter(cardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ClubListDbCo().execute();
    }

    class ClubListDbCo extends AsyncTask<Void, Void,List> {

        private List<Club> allClubList;
        Connection conn;
        Statement stmt;

        // contructeur
        public ClubListDbCo() {
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List clubList) {
            refreshCards(clubList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allClubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT * FROM clubdb ORDER BY titleClub ASC");

                while(resultSet.next()){
                    // prend les données
                    int id = resultSet.getInt("idClub");
                    String title = resultSet.getString("titleClub");
                    String details = resultSet.getString("detailsClub");

                    allClubList.add(new Club(id, title, details));
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
            return allClubList;
        }
    }
}
