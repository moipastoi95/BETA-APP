package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminActivity extends AppCompatActivity{

    // Variables des éléments du layout
    CardView adduser, addclub, addevent, manageevent, settings, statistiques,managehighschools, messagerie, config;
    Button back;
    TextView greetings;

    // id user
    int id;
    String name, password, email;
    int levelAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Espace Administrateur");

        Intent intent = getIntent();
        id = Integer.valueOf(intent.getStringExtra("id"));
        levelAccess = Integer.valueOf(intent.getStringExtra("levelAccess"));
        //name = intent.getStringExtra("name");
        //password = intent.getStringExtra("password");
        //email = intent.getStringExtra("email");


        // elements du layout
        greetings = findViewById(R.id.greetings);
        greetings.setText("Bienvenue");

        adduser = findViewById(R.id.adduser);
        addclub = findViewById(R.id.addclub);
        addevent = findViewById(R.id.addevent);
        manageevent = findViewById(R.id.manageevent);
        settings = findViewById(R.id.settings);
        statistiques = findViewById(R.id.statistiques);
        managehighschools = findViewById(R.id.managehighschools);
        messagerie = findViewById(R.id.messagerie);
        config = findViewById(R.id.config);
        back = findViewById(R.id.back);



        if(levelAccess == 1)
        {
            adduser.setVisibility(View.GONE);
            addclub.setVisibility(View.GONE);
            ViewGroup layout = (ViewGroup) managehighschools.getParent();
            layout.removeView(managehighschools);
            ViewGroup layout1 = (ViewGroup) messagerie.getParent();
            layout1.removeView(messagerie);
            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout1.removeView(config);
        }
        else if(levelAccess == 2)
        {
            ViewGroup layout = (ViewGroup) managehighschools.getParent();
            layout.removeView(managehighschools);
            ViewGroup layout1 = (ViewGroup) messagerie.getParent();
            layout1.removeView(messagerie);

        }
        else if(levelAccess == 3)
        {
            ViewGroup layout1 = (ViewGroup) addclub.getParent();
            layout1.removeView(addclub);

            ViewGroup layout2 = (ViewGroup) addevent.getParent();
            layout2.removeView(addevent);

            ViewGroup layout3 = (ViewGroup) manageevent.getParent();
            layout3.removeView(manageevent);

            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout1.removeView(config);


        }
        else if(levelAccess == 4)
        {

            addclub.setVisibility(View.GONE);

            ViewGroup layout = (ViewGroup) addevent.getParent();
            layout.removeView(addevent);

            ViewGroup layout1 = (ViewGroup) addclub.getParent();
            layout1.removeView(addclub);

            ViewGroup layout2 = (ViewGroup) manageevent.getParent();
            layout2.removeView(manageevent);

            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout1.removeView(config);




        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // bouton des stats
        statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stats = new Intent(AdminActivity.this, StatsActivity.class);
                startActivity(stats);
            }
        });

        // bouton de création d'évents
        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEvent = new Intent(AdminActivity.this, AddEventActivity.class);
                addEvent.putExtra("idAuthor", String.valueOf(id));
                startActivity(addEvent);
            }
        });

        // bouton de gestion des events
        manageevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageEvent = new Intent(AdminActivity.this, event_admin_list.class);
                manageEvent.putExtra("idAuthor", String.valueOf(id));
                startActivity(manageEvent);
            }
        });

        // bouton de gestion des membres
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent members = new Intent(AdminActivity.this, MembersActivity.class);
                members.putExtra("idAuthor", String.valueOf(id));
                startActivity(members);
            }
        });

        // bouton de gestion des clubs
        addclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent club = new Intent(AdminActivity.this, ManageClubActivity.class);
                club.putExtra("idAuthor", String.valueOf(id));
                startActivity(club);
            }
        });

        // bouton des settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(AdminActivity.this, SettingsActivity.class);
                settings.putExtra("levelAccess", String.valueOf(levelAccess));
                settings.putExtra("id", String.valueOf(id));
                startActivity(settings);
            }
        });

        // bouton Other
        statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AdminActivity.this, StatsActivity.class);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TrumpRefresh().execute();
    }

    public class TrumpRefresh extends AsyncTask<Void, Void, Void> {

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        String username;

        @Override
        protected void onPostExecute(Void aVoid) {
            greetings.setText("Bienvenue " + username);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT nameUser FROM accountdb WHERE idUser=" + String.valueOf(id));
                if (resultSet.next()) {
                    username = resultSet.getString("nameUser");
                }

                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}