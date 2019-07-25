package com.example.george.betamdl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameET, passwordET;
    private TextView forgot_pass;
    private Button submit;
    private CheckBox rememberMeCb;

    // Variable de connexion à la base de données
    ConnectionDataBase connectionClass;
    EditText edtuserid,edtpass;
    Button btnlogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Connexion");

        submit = findViewById(R.id.valid);
        forgot_pass = findViewById(R.id.forgot_pass);
        rememberMeCb = findViewById(R.id.rememberMeCb);


        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(help);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameET = (EditText) findViewById(R.id.username);
                passwordET = (EditText) findViewById(R.id.password);

                if (usernameET.getText().toString().length() > 0 && passwordET.getText().toString().length() > 0) {
                    Toast.makeText(LoginActivity.this, "Connexion en cours", Toast.LENGTH_SHORT).show();

                    final AlertDialog dialogBuilder = new AlertDialog.Builder(LoginActivity.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.progresspopup, null);

                    new DbClass().execute();

                }else {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class DbClass extends AsyncTask<String,String,String>
    {
        Connection conn = null;
        Statement stmt = null;

        String toastConnection = "Un probleme est survenu";

        String username = usernameET.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"");
        String password = passwordET.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"");
        boolean rememberMe;

        boolean isSuccess = false;

        // variable récupérée
        int id = 0;
        int levelAccess;
        String email;

        @Override
        protected void onPreExecute() {
            rememberMe = rememberMeCb.isChecked();
        }

        @Override
        protected void onPostExecute(String r) {
            if (isSuccess == true) {
                // "se souvient" de l'utilisateur si il le désir
                if (rememberMe == true) {
                    SharedPreferences preferences = getSharedPreferences(MainActivity.REMEMBER_USER_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putInt(MainActivity.REMEMBER_ID_KEY, id);
                    edit.putInt(MainActivity.REMEBER_LEVEL_ACCESS_KEY, levelAccess);
                    edit.apply();
                }

                // prepare le changement d'activité
                Intent adminAct = new Intent(LoginActivity.this,AdminActivity.class);
                adminAct.putExtra("id",String.valueOf(id));
                //adminAct.putExtra("name", username);
                //adminAct.putExtra("password", password);
                adminAct.putExtra("levelAccess", String.valueOf(levelAccess));

                startActivity(adminAct);
                finish();
            }
            else {

                Toast.makeText(LoginActivity.this, r, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT idUser, levelAccess FROM accountdb WHERE nameUser='"+username+"' AND passwordUser='"+password+"'");

                if (resultSet.next()) {
                    id  = resultSet.getInt("idUser");
                    levelAccess = resultSet.getInt("levelAccess");
                    //email = resultSet.getString("email");

                    isSuccess = true;
                    toastConnection = "Bonjour "+username;
                }else {
                    toastConnection = "Ce compte n'existe pas";
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
            return toastConnection;
        }
    }
}
