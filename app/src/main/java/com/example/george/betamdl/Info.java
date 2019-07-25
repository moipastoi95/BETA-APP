package com.example.george.betamdl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Info extends AppCompatActivity {

    private TextView mTextMessage;

    private String APropos = "A propos : \n\n Cette application a pour but de communiquer avec les élèves du lycée " +
            "au sujet des événements, passés, présents et futurs. Ils sont présentés sous forme de " +
            "Timeline, offrant ainsi à l’utilisateur une vue d’ensemble de la vie lycéenne.\n\nCelui-ci aura par ailleurs " +
            "accès à des informations complémentaires concernant chaque organisme travaillant pour améliorer le cadre de vie " +
            "du lycée.",
    Contact = "Contact : \n\n En cas de problèmes, ou pour d'éventuelles informations complémentaires, vous " +
            "pouvez vous adressez à Julien De Gentille. \n\n\n Pour signaler" +
            "un problème dans l'application ou autre : \n\n vincent.ky09@gmail.com / theomanea9@gmail.com /  " +
            "juliendegentile@lilo.org \n\n SchoolEventManagement v 1.0",
    Credit = "Crédits : \n\n Nous remercions pour cette application  Julien De Gentille les deux développeurs " +
            "Théo Manea, et Vincent Ky et la FMDL pour son soutien.";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(APropos);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(Contact);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(Credit);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mTextMessage.setText(APropos);
    }

}
