package com.example.george.betamdl;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity {

    // mode d'affichage
    private int showMode;
    private int ADD_MODE = 0;
    private int EDIT_MODE = 1;

    private User user;

    // éléments du layouts
    EditText name, fName, password;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button userNewBackBtn, userNewSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // chercher elements du layout
        name = findViewById(R.id.nameEt);
        fName = findViewById(R.id.fNameEt);
        password = findViewById(R.id.passwordEt);
        radioGroup = findViewById(R.id.radioButtonGroup);
        userNewSendBtn = findViewById(R.id.userNewSendBtn);
        userNewBackBtn = findViewById(R.id.userNewBackBtn);

        // bouton retour
        userNewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // bouton sauvegarder
        userNewSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idRadioBtn = radioGroup.getCheckedRadioButtonId();
                int lvlAccess = 0;
                switch (idRadioBtn) {
                    case R.id.rBAdmin:
                        lvlAccess = 1;
                        break;
                    case R.id.rBSuperAdmin:
                        lvlAccess = 2;
                        break;
                }

                if (showMode == ADD_MODE) {
                    new dbUser(ADD_MODE, "INSERT INTO accountdb(nameUser, familyNameUser, passwordUser, levelAccess) VALUES ('"+name.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"','"+fName.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"','"+password.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"',"+String.valueOf(lvlAccess)+")").execute();
                } else {
                    new dbUser(EDIT_MODE, "UPDATE accountdb SET nameUser='"+name.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"',familyNameUser='"+fName.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"',passwordUser='"+password.getText().toString().replace("\'", "\\\'").replace("\"", "\\\"")+"',levelAccess="+String.valueOf(lvlAccess)+" WHERE idUser="+user.getIdUser()).execute();
                }
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("user")) {
            // titre
            setTitle("Modifier un utilisateur");
            user = intent.getParcelableExtra("user");
            showMode = EDIT_MODE;

            name.setText(user.getNameUser());
            fName.setText(user.getFamilyNameUser());
            password.setText(user.getPasswordUser());
            if (user.getLevelAccess() >= 2) {
                RadioButton rd = findViewById(R.id.rBSuperAdmin);
                rd.setChecked(true);
            } else {
                RadioButton rd = findViewById(R.id.rBAdmin);
                rd.setChecked(true);
            }
        } else {
            // titre
            setTitle("Ajouter un utilisateur");
            showMode = ADD_MODE;
        }
    }

    public class dbUser extends AsyncTask<Void, Void, Void>
    {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        //contructeur 1
        public dbUser(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void nothing) {
            if (modeQuery == ADD_MODE) {
                Toast.makeText(AddUserActivity.this, "Nouvel utilisateur crée !", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(AddUserActivity.this, "utilisateur modifié !", Toast.LENGTH_LONG).show();
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
