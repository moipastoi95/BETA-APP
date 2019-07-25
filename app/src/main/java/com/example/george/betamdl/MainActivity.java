package com.example.george.betamdl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ConnectionDataBase.Listeners{

    /*Variables pour le "Navigation Drawer"*/
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private SubMenu clubmenu, fonctionnalityMenu;
    private List<Club> allClubList;
    private List<Event> allEventList;
    private boolean isRemembered = false;
    // variable du layout
    private SwipeRefreshLayout swipeRefreshLayout;
    /*--------------------------------------*/
    /*Variables pour la base de données*/
    protected static String DB_URL = "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7296952";
    protected static String USER = "sql7296952";
    protected static String PASS = "wDaA2LhGtA";

    private String event_Query, club_Query;

    private int idSchool;
    /*--------------------------------------*/
    // Variables des CardView et de leur contenu
    private RecyclerView EvtRv;
    private CardAdapter cardAdapter;

    // Shared Preferences Keys
    static String SETTINGS_NAME = "settings", NOTIFICATION_KEY = "notification",
            REMEMBER_USER_NAME = "remember", REMEMBER_ID_KEY = "idUser", REMEBER_LEVEL_ACCESS_KEY = "levelAccess",
            IF_NO_CONNECTED_NAME = "ifNoConnected", SAVED_EVENT_NO_CO_KEY = "savedEvent", SAVED_CLUB_NO_CO_KEY = "savedClub",
            SCHOOL_NAME = "whichSchool", SCHOOL_ID_KEY = "idSchoolKey", SCHOOL_NAME_KEY = "schoolName";

    // DateFormat
    static String UNI_DATE = "yyyy-MM-dd", UNI_DATE_TIME = "yyyy-MM-dd HH:mm",
    SHOWN_DATE = "dd/MM/yyyy", SHOWN_DATE_TIME = "dd/MM/yyyy - HH:mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initQuery();

        // lance le service, si preference notif est activé
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(NOTIFICATION_KEY, true)) {
            Intent intent = new Intent(this, HelloService.class);
            startService(intent);
        }

        Toast.makeText(MainActivity.this, "Bienvenue ! ",Toast.LENGTH_LONG).show();

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        clubmenu = menu.findItem(R.id.menuClubs).getSubMenu();
        // pas besoin de les déclarer pour toute l'activity -> juste localement suffit
        fonctionnalityMenu = menu.findItem(R.id.appFonctionnality).getSubMenu();
        amIConnected();

        // elements du layout
        swipeRefreshLayout = findViewById(R.id.SRefreshMain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkIfAbleRefresh();
            }
        });
        checkIfAbleRefresh();
    }

    // methods pour le navigation drawer
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
                // le bouton de connexion / deconnexion
                if (isRemembered) { // se deconnecte
                    getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE).edit().clear().apply();
                    amIConnected();
                } else {
                    Intent connexion = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(connexion);
                }
                break;
            case R.id.infos:
                Intent infos = new Intent(MainActivity.this,Info.class);
                startActivity(infos);
                break;
            case R.id.param:
                Intent param = new Intent(MainActivity.this,userSettings.class);
                startActivity(param);
                break;
            case R.id.adminZone:
                SharedPreferences preferences = getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE);
                // prepare le changement d'activité
                Intent adminAct = new Intent(MainActivity.this,AdminActivity.class);
                adminAct.putExtra("id",String.valueOf(preferences.getInt(REMEMBER_ID_KEY, -1)));
                adminAct.putExtra("levelAccess", String.valueOf(preferences.getInt(REMEBER_LEVEL_ACCESS_KEY, 0)));
                startActivity(adminAct);
                break;

            default:
                for(Club clubTmp : allClubList) {
                    if (clubTmp.getIdClub() == item.getItemId()) {
                        Intent intentDetailsClub = new Intent(MainActivity.this,ClubDetailActivity.class);
                        intentDetailsClub.putExtra("club", clubTmp);
                        startActivity(intentDetailsClub);
                    }
                }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // test la connection internet et réagit par rapport à ça
    public void checkIfAbleRefresh(){
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            // Commence le processus de rafraichissement des cards, à chaque retour à la page
            startAsyncTask(event_Query, ConnectionDataBase.EVENT_QUERY);
            // rafraichit les clubs
            startAsyncTask(club_Query, ConnectionDataBase.CLUB_QUERY);

            // Toast.makeText(MainActivity.this, "Coco à internet", Toast.LENGTH_SHORT).show();
        } else { // n'est pas connecté à internet
            //afficher un message d'info et afficher les events pré-enregistrés
            Snackbar.make(swipeRefreshLayout, "Vous n'êtes pas connecté à internet", Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            loadData();
        }
    }

    // fonction de génération des cards pour les events
    public void refreshCards(List<Event> allEvtList) {
        //https://gist.github.com/gabrielemariotti/4c189fb1124df4556058#file-simpleadapter-java

        // Pour les évènements actuels
        int howManyNowEvt = 0;
        // cherche le nombre d'event d'aujourd'hui, dans la liste
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        Calendar calendarCompare = Calendar.getInstance();
        for (Event evtTemp : allEvtList) {
            calendarStart.setTime(evtTemp.getDayOfEvent());
            calendarEnd.setTime(evtTemp.getDayEndOfEvent());
            calendarCompare.setTime(new Date());
            if (calendarStart.get(Calendar.DAY_OF_YEAR) <= calendarCompare.get(Calendar.DAY_OF_YEAR) &&
                    calendarStart.get(Calendar.YEAR) <= calendarCompare.get(Calendar.YEAR) &&

                    calendarEnd.get(Calendar.DAY_OF_YEAR) >= calendarCompare.get(Calendar.DAY_OF_YEAR) &&
                    calendarEnd.get(Calendar.YEAR) >= calendarCompare.get(Calendar.YEAR)) {
                howManyNowEvt++;
            }
        }


        //Repérage du RecyclerView
        EvtRv = (RecyclerView) findViewById(R.id.EvtRv);

        // Création CardAdapter
        cardAdapter = new CardAdapter(allEvtList, CardAdapter.SPECTATOR_MODE);
        //SimpleAdapter simpleAdapter = new SimpleAdapter(this, new String[] {"le 1","le 2", "le 3","le 4"});
        EvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Création de la section de la list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        // Configurer les sections
        // Afficher les selections selon les evenements presents et futures
        String strEvtNow = "Evenements Actuels", strEvtFtr = "Evenements futurs";
        if (allEvtList.size() == 0) { // si la liste est vide : ni present, ni future
            strEvtNow = "Pas d'évenements";
            strEvtFtr = "";
        } else if (allEvtList.size() == howManyNowEvt) { // si il n'y a QUE des evenements present
            strEvtFtr = "Pas d'évenements futurs";
        } else if (howManyNowEvt == 0 && allEvtList.size() != 0) { // Si il n'y a QUE des events futures
            strEvtNow = "Pas d'évenements presents";
        }

        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,strEvtNow));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(howManyNowEvt,strEvtFtr));

        //ajouter l'adapter à sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text,cardAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        EvtRv.setAdapter(mSectionedAdapter);
    }

    // fonction de génération des clubs
    private void refreshClubs(@NonNull List<Club> allClubList) {
        clubmenu.clear();
        for (Club clubTmp : allClubList) {
            // ajoute un club, un par un
            // id Item == id Club
            clubmenu.add(0, clubTmp.getIdClub(), Menu.NONE, clubTmp.getTitleClub());
        }
    }

    // 4 methodes de callbacks pour ConnectionDataBase
    public void startAsyncTask(String query, int modeQuery) {
        new ConnectionDataBase(MainActivity.this, query, modeQuery).execute();
    }

    @Override
    public void onPreExecute(int modeQuery) {
    }

    @Override
    public void doInBackground() {

    }

    @Override
    public void onPostExecute(List result, int modeObjRtrn) {
        swipeRefreshLayout.setRefreshing(false);

        if (modeObjRtrn == ConnectionDataBase.EVENT_QUERY) {
            allEventList = new ArrayList<>();
            allEventList = result;
            refreshCards(result);
        } else {
            allClubList = new ArrayList<>();
            allClubList = result;
            refreshClubs(result);
        }

        // enregistre les evenements pour votre plus grand confort !
        saveData();
    }

    // methode appelé lors du rafraichissement de la page, quand l'utilisateur vient/revient dessus
    @Override
    protected void onResume() {
        super.onResume();
        // regarde si il est associé
        checkInitSchool();

        // regarde si il est connecté(loggé) en tant qu'utilisateur ou pas
        amIConnected();
    }

    private void saveData(){

        SharedPreferences sharedPreferences = getSharedPreferences(IF_NO_CONNECTED_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
        String jsonEvent = gson.toJson(allEventList);
        String jsonClub = gson.toJson(allClubList);

        editor.putString(SAVED_EVENT_NO_CO_KEY, jsonEvent);
        editor.putString(SAVED_CLUB_NO_CO_KEY, jsonClub);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(IF_NO_CONNECTED_NAME, MODE_PRIVATE);

        Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();

        String jsonEvent = sharedPreferences.getString(SAVED_EVENT_NO_CO_KEY, null);
        String jsonClub = sharedPreferences.getString(SAVED_CLUB_NO_CO_KEY,null);
        Type typeEvent = new TypeToken<ArrayList<Event>>() {}.getType();
        Type typeClub = new TypeToken<ArrayList<Club>>() {}.getType();

        allEventList = gson.fromJson(jsonEvent, typeEvent);
        allClubList = gson.fromJson(jsonClub, typeClub);

        if (allEventList == null)
            allEventList = new ArrayList<>();
        else
            refreshCards(allEventList);

        if (allClubList == null)
            allClubList = new ArrayList<>();
        else
            refreshClubs(allClubList);

    }

    private void amIConnected() {
        // cache ou pas le bouton de l'espace admin + bouton "connexion" ou "deconnexion" ?
        SharedPreferences preferences = getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE);
        if (preferences.getInt(REMEMBER_ID_KEY, -1) != -1) { // si il est reconnu "se souvenir de moi"
            fonctionnalityMenu.findItem(R.id.adminZone).setVisible(true);
            fonctionnalityMenu.findItem(R.id.connexion).setTitle("Deconnexion");
            isRemembered = true;
        } else { // sinon

            fonctionnalityMenu.findItem(R.id.adminZone).setVisible(false);
            fonctionnalityMenu.findItem(R.id.connexion).setTitle(R.string.connexion);
            isRemembered = false;
        }
    }

    private void checkInitSchool() {
        SharedPreferences preferences = getSharedPreferences(SCHOOL_NAME, MODE_PRIVATE);
        idSchool = preferences.getInt(SCHOOL_ID_KEY, 0);

        // si aucun lycée n'est associé
        if (idSchool == 0){
            Intent intent = new Intent(MainActivity.this, ChooseSchoolActivity.class);
            startActivity(intent);
        } else {
            initQuery();
            setTitle(preferences.getString(SCHOOL_NAME_KEY, "Bienvenue"));
        }
    }

    private void initQuery() {
        // INITIALISATION DES REQUETES
        event_Query = "(" +
                "    SELECT *" +
                "    FROM eventsdb " +
                "    WHERE DATE(dayEndOfEvent) >= CURDATE() AND DATE(dayOfEvent) <= CURDATE() AND idSchool = "+idSchool +
                "    ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC" +
                ") UNION (" +
                "    SELECT * " +
                "    FROM eventsdb " +
                "    WHERE DATE(dayOfEvent) > CURDATE()  AND idSchool = "+idSchool +
                "    ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC LIMIT 10" +
                ")";

        club_Query = "SELECT * FROM clubdb WHERE idSchool="+idSchool+"ORDER BY titleClub ASC";
    }


}

