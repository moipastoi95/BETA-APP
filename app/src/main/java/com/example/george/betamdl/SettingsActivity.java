package com.example.george.betamdl;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    String password, username, email;
    int levelAccess;
    int id;

    TextView userET, passwordET, levelET, countArticleTv;
    Button action;

    EditText newusername, newpassword, confirm_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Modification des données");

        Intent intent = getIntent();
        id = Integer.valueOf(intent.getStringExtra("id"));
        //email = intent.getStringExtra("email");
        levelAccess = Integer.valueOf(intent.getStringExtra("levelAccess"));

        // layout
        userET = findViewById(R.id.user);
        passwordET = findViewById(R.id.password);
        levelET = findViewById(R.id.levelaccess);
        action = findViewById(R.id.action);
        countArticleTv = findViewById(R.id.countArticle);



        //emailET.setText(email);
        levelET.setText(String.valueOf(levelAccess));

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialogBuilder = new AlertDialog.Builder(SettingsActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custompopup, null);

                // layout
                Button button1 = dialogView.findViewById(R.id.buttonSubmit);
                Button button2 = dialogView.findViewById(R.id.buttonCancel);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        newusername =  dialogView.findViewById(R.id.username);
                        newpassword = dialogView.findViewById(R.id.password);
                        confirm_password = dialogView.findViewById(R.id.confirm_mdp);
                        String newusernameStr = newusername.getText().toString();
                        String newpasswordStr = newpassword.getText().toString();
                        String confirm_passwordStr = confirm_password.getText().toString();

                        if(!newpasswordStr.equals("") && !newusernameStr.equals("") && !confirm_passwordStr.equals("") && newpasswordStr.equals(confirm_passwordStr))
                        {

                            // .replace("\'", "\\\'").replace("\"", "\\\"")
                            new Insert( "UPDATE accountdb SET " +
                                    "nameUser='" + newusernameStr +
                                    "', passwordUser='" + newpasswordStr  +
                                    "' WHERE idUser=" + String.valueOf(id)).execute();

                            dialogBuilder.dismiss();

                            new refreshInfos().execute();

                        }
                        else
                        {

                            Snackbar.make(view, "Erreur, veuillez rééssayer", Snackbar.LENGTH_SHORT).show();

                        }




                    }

                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        new refreshInfos().execute();

    }


    public class Insert extends AsyncTask<Void, Void, Void>{
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        private String query;

        //contructeur 1
        public Insert(String query) {

            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void hjy) {

            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Yooolllllooooo ! Modifications effectuées ! ", Snackbar.LENGTH_SHORT).show();

        }
        @Override
        protected  Void doInBackground(Void ...params) {
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

    public class refreshInfos extends AsyncTask<Void, Void, Void> {

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        private int countArticles;
        private String newname, newpass;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            countArticleTv.setText(String.valueOf(countArticles));
            userET.setText(String.valueOf(newname));
            passwordET.setText(String.valueOf(newpass));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM eventsdb WHERE idAuthor=" + String.valueOf(id));
                resultSet.next();
                countArticles = resultSet.getInt("rowcount");

                ResultSet rs = stmt.executeQuery("SELECT nameUser, passwordUser FROM accountdb WHERE idUser=" +  String.valueOf(id));
                if(rs.next()){
                    newname = rs.getString("nameUser");
                    newpass = rs.getString("passwordUser");
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
