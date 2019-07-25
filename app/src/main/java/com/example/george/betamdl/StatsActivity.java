package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatsActivity extends AppCompatActivity {

    private TextView nbconnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        nbconnection = findViewById(R.id.nbconnection);


    }


    public class DbClass extends AsyncTask<String,String,String>
    {
        Connection conn = null;
        Statement stmt = null;






        // variable récupérée
        int nbconnect;


        @Override
        protected void onPreExecute() { }
        @Override
        protected void onPostExecute(String r) {

            nbconnection.setText(nbconnect);

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT nbconnection FROM stats WHERE id= 1 ");

                if (resultSet.next()) {
                    nbconnect  = resultSet.getInt("nbconnection");

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
            return null;
        }
    }



}
