package com.example.george.betamdl;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*Variables pour le "Navigation Drawer"*/
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    /*--------------------------------------*/
    /*Variables pour la base de données*/
    /*DatabaseHelper mDatabaseHelper;*/
    private static final String TAG = "ListDataActivity";
    private ListView mListView;
    /*--------------------------------------*/

    // Variables des CardView et de leur contenu
        //des évènements actuels
    private RecyclerView nowEvtRv;
    private List<Event> nowEvt;
    private CardAdapter nowCardAdapter;
        //des évènements futures
    private RecyclerView futureEvtRv;
    private List<Event> futureEvt;
    private CardAdapter futureCardAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Bienvenue");


        Toast.makeText(MainActivity.this, "Bienvenue ! ",Toast.LENGTH_LONG).show();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);




        /*populateCards();*/

        // Pour les évènements actuels
        CardGenerator cardGenerator = new CardGenerator();

        nowEvtRv = (RecyclerView) findViewById(R.id.nowEvtRv);
        nowCardAdapter = new CardAdapter(cardGenerator.nowEvents(5));
        nowEvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        nowEvtRv.setAdapter(nowCardAdapter);

        // pour les évènements futures
        futureEvtRv = (RecyclerView) findViewById(R.id.futureEvtRv);
        futureCardAdapter = new CardAdapter(new CardGenerator().futureEvents(5));
        futureEvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        futureEvtRv.setAdapter(futureCardAdapter);


        // TESTS

        /*Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(0);
        calendar.set(1989, 7, 14, 12, 45, 0);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
        String dateDF = dateFormat.format(dateFormat);
        Toast.makeText(this, dateDF, Toast.LENGTH_LONG);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.connexion:
                Intent connexion = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(connexion);
                break;
            case R.id.infos:
                Intent infos = new Intent(MainActivity.this,Infos.class);
                startActivity(infos);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void populateCards(){


    }
}
