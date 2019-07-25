package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddClubActivity extends AppCompatActivity {

    // variables du layout
    private EditText addClubTitleEt,addClubDetailsEt;
    private Button clubNewBackBtn, clubNewSendBtn;

    // mode
    private int showMode;
    int ADD_MODE = 0;
    int EDIT_MODE = 1;

    // objet actuellement modifié ou créé
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        // trouver les éléments du layout
        addClubTitleEt = findViewById(R.id.addClubTitleEt);
        addClubDetailsEt = findViewById(R.id.addClubDetailsEt);
        clubNewBackBtn = findViewById(R.id.clubNewBackBtn);
        clubNewSendBtn = findViewById(R.id.clubNewSendBtn);

        // bouton retour
        clubNewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // bouton sauvegarder
        clubNewSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // .replace("\'", "\\\'").replace("\"", "\\\"")

                if (showMode == ADD_MODE) {
                    new dbClub(ADD_MODE, "INSERT INTO clubdb(titleClub, detailsClub) VALUES ('"+addClubTitleEt.getText().toString()+"','"+addClubDetailsEt.getText().toString()+"')").execute();
                } else {
                    new dbClub(EDIT_MODE, "UPDATE clubdb SET titleClub='"+addClubTitleEt.getText().toString()+"',detailsClub='"+addClubDetailsEt.getText().toString()+"' WHERE idClub="+club.getIdClub()).execute();
                }
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("club")) {
            // titre
            setTitle("Modifier un club");
            club = intent.getParcelableExtra("club");
            showMode = EDIT_MODE;

            addClubTitleEt.setText(club.getTitleClub());
            addClubDetailsEt.setText(club.getDetailsClub());
        } else {
            // titre
            setTitle("Ajouter un club");
            showMode = ADD_MODE;
        }
    }

    public class dbClub extends AsyncTask<Void, Void, Void>
    {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        //contructeur 1
        public dbClub(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void nothing) {
            if (modeQuery == ADD_MODE) {
                Toast.makeText(AddClubActivity.this, "Nouveau club crée !", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(AddClubActivity.this, "club modifié !", Toast.LENGTH_LONG).show();
            }
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List clubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                stmt.executeUpdate(query);

                stmt.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) { }
    }
}
