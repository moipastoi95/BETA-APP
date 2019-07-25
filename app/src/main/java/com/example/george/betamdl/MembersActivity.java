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

public class MembersActivity extends AppCompatActivity {

    // layout
    RecyclerView MembersRv;
    CardAdapterMembers cardAdapter;
    FloatingActionButton addUserFab;
    ProgressBar progressBar;

    int ownId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        setTitle("Gérer les membres");

        Intent intent = getIntent();
        ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));

        // layout
        addUserFab = findViewById(R.id.addUserFab);
        addUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MembersActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
        progressBar = findViewById(R.id.prb);
    }

    public void refreshCards(List<User> allUserList) {
        //Repérage du RecyclerView
        MembersRv = findViewById(R.id.allUserRv);

        progressBar.setVisibility(View.GONE);
        // Création CardAdapter
        cardAdapter = new CardAdapterMembers(allUserList);
        MembersRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        MembersRv.setAdapter(cardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UserListDbCo().execute();
    }

    class UserListDbCo extends AsyncTask<Void, Void,List> {

        private List<User> allUserList;
        Statement stmt;
        Connection conn;

        // contructeur
        public UserListDbCo() {
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List userList) {
            refreshCards(userList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allUserList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT * FROM accountdb WHERE idUser !="+String.valueOf(ownId));

                while(resultSet.next()){
                    // prend les valeurs depuis la requête
                    int idUser  = resultSet.getInt("idUser");
                    String name  = resultSet.getString("nameUser");
                    String fName = resultSet.getString("familyNameUser");
                    String password = resultSet.getString("passwordUser");
                    int lvlaccess = resultSet.getInt("levelAccess");

                    // Historique XD
                    // Event evt = new Event(idUser,detail,"Nom :" + title + ", mot de passe : " + mdp + ", niveau de sécurité :" + lvlaccess,"nothing",1);
                    User user = new User(idUser, name, fName, password, lvlaccess);

                    allUserList.add(user);
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
            return allUserList;
        }
    }
}

