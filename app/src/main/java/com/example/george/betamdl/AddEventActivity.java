package com.example.george.betamdl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    // Variables liées au layout
    private EditText evtTitle, evtDetail;
    private SearchableSpinner spinner; //https://github.com/miteshpithadiya/SearchableSpinner
    private Button sendBtn, backBtn, evtDateTimeStart, evtDateTimeEnd, evtPic;
    private LinearLayout secondDateLayout;
    private Switch multiDateSwitch;

    // Object du nouvel évènement
    private Event newEvent;
    private Event formerEvent;
    // objet pour le bon fonctionnement des methodes de dialog
    private InterDialog interDialog;

    // Variables pour afficher les PickerDialogue
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    // Variables des dates
    //aujourd'hui
    Date today;
    String dayNow, monthNow, yearNow, hourNow, minuteNow;

    int idAuthor;
    int positionClubSelected = 0;
    private List<Club> clubList;

    //variables base de données
    private int SELECT_MODE = 0;
    private int INSERT_MODE = 1;

    // variable pour le mode d'affichage et du systeme
    private int show_mode;
    private static int INSERT_EVENT_MODE = 0, EDIT_EVENT_MODE = 1,
            FIRST_DATE_ID = 0, LAST_DATE_ID = 1;

    // format des dates
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // recupère l'idAuthor de l'acivité précédente, si il y en a
        Intent intent = getIntent();
        if (intent.hasExtra("idAuthor")) {
            // Titre
            setTitle("Ajouter un évènement");

            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            show_mode = INSERT_EVENT_MODE;
        } else if (intent.hasExtra("event")) { // si c'est un edit
            // Titre
            setTitle("Modifier un évènement");
            formerEvent = intent.getParcelableExtra("event");
            show_mode = EDIT_EVENT_MODE;
        }

        format = new SimpleDateFormat();
        format.applyPattern(MainActivity.UNI_DATE_TIME);

        // instancie le nouvel objet
        newEvent = new Event();
        interDialog = new InterDialog();

        // initialise les variables d'aujourd'hui
        calendar = Calendar.getInstance();
        dayNow = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        // monthNow commence au mois 0
        monthNow = String.valueOf(calendar.get(Calendar.MONTH));
        yearNow = String.valueOf(calendar.get(Calendar.YEAR));
        hourNow = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        minuteNow = String.valueOf(calendar.get(Calendar.MINUTE));
        try {
            today = format.parse(yearNow + "-" + String.valueOf(Integer.valueOf(monthNow) + 1) + "-" + dayNow + " " + hourNow + ":" + minuteNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // initialise les variables de la date choisit à aujourd'hui
        if (show_mode == INSERT_EVENT_MODE) {
            setEvtDayOfEvent(yearNow, monthNow, dayNow, hourNow, minuteNow);
            setEvtDayEndOfEvent(yearNow, monthNow, dayNow, hourNow, minuteNow);
        } else {
            newEvent = formerEvent;
        }

        // Initialisation des éléments du layout
        evtTitle = findViewById(R.id.evtNewTitle);
        evtDetail = findViewById(R.id.evtNewDetail);
        evtDateTimeStart = findViewById(R.id.evtNewDateTimeStart);
        evtDateTimeEnd = findViewById(R.id.evtNewDateTimeEnd);
        sendBtn = findViewById(R.id.evtNewSendBtn);
        backBtn = findViewById(R.id.evtNewBackBtn);
        spinner = findViewById(R.id.sSpinnerClub);
        secondDateLayout = findViewById(R.id.dateTimeEndLayout);
        multiDateSwitch = findViewById(R.id.multiDaysSwitch);
        // Affichage de la date et l'heure
        if (show_mode == INSERT_EVENT_MODE) {
            evtDateTimeStart.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));
            evtDateTimeEnd.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));
        } else {
            evtTitle.setText(formerEvent.getTitle());
            evtDetail.setText(formerEvent.getDetail());

            evtDateTimeStart.setText(formerEvent.getStringDayOfEvent());
            evtDateTimeEnd.setText(formerEvent.getStringDayEndOfEvent());
        }

        if (!newEvent.getStringUniversalDayOfEvent().equals(newEvent.getStringUniversalDayEndOfEvent())) {
            multiDateSwitch.setChecked(true);
            secondDateLayout.setVisibility(View.VISIBLE);
        }

        // DatePicker Dialog : First day of Event
        evtDateTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(FIRST_DATE_ID,
                        Integer.valueOf(new SimpleDateFormat("yyyy").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("MM").format(newEvent.getDayOfEvent())) - 1,
                        Integer.valueOf(new SimpleDateFormat("dd").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("HH").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("mm").format(newEvent.getDayOfEvent())));

            }
        });

        // DatePicker Dialog : Last day of Event
        evtDateTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(LAST_DATE_ID,
                        Integer.valueOf(new SimpleDateFormat("yyyy").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("MM").format(newEvent.getDayEndOfEvent())) - 1,
                        Integer.valueOf(new SimpleDateFormat("dd").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("HH").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("mm").format(newEvent.getDayEndOfEvent())));
            }
        });

        // Send/valid Button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tester si tous les champs ont été remplis + conditions de(s) date(s)
                if (
                        (!evtTitle.getText().toString().isEmpty() &&
                        !evtDetail.getText().toString().isEmpty() &&
                        spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                        ) &&
                        //si le switch et les dates entre elles corrrespondent
                        (// pour 2 dates
                                (multiDateSwitch.isChecked() && newEvent.getDayOfEvent().before(newEvent.getDayEndOfEvent())
                                ) || (
                                        !multiDateSwitch.isChecked()
                                        )

                                )
                ) {
                    if (!multiDateSwitch.isChecked()) {
                        newEvent.setDayEndOfEvent(newEvent.getDayOfEvent());
                    }

                    newEvent.setTitle(evtTitle.getText().toString());
                    newEvent.setDetail(evtDetail.getText().toString());
                    newEvent.setDayOfCreation(today);
                    newEvent.setTitleClubLinked(clubList.get(positionClubSelected).getTitleClub());
                    newEvent.setIdClubLinked(clubList.get(positionClubSelected).getIdClub());

                    // si il est bien connecté à internet
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Insert ou edit selon le mode
                        if (show_mode == INSERT_EVENT_MODE) {
                            new DbClass(INSERT_MODE, "INSERT INTO eventsdb " +
                                    "(titleEvent, detailsEvent, idAuthor, idClub, dayOfCreation, dayOfEvent, dayEndOfEvent) VALUES ('"
                                    + newEvent.getTitle().replace("\'", "\\\'").replace("\"", "\\\"") + "','"
                                    + newEvent.getDetail().replace("\'", "\\\'").replace("\"", "\\\"") + "',"
                                    + String.valueOf(idAuthor) + "," + String.valueOf(newEvent.getIdClubLinked()) + ",'"
                                    + newEvent.getStringUniversalDayOfCreation() + "','"
                                    + newEvent.getStringUniversalDayOfEvent() + "','"
                                    + newEvent.getStringUniversalDayEndOfEvent() + "')").execute();
                        } else {
                            new DbClass(INSERT_MODE, "UPDATE eventsdb SET " +
                                    "titleEvent='" + newEvent.getTitle().replace("\'", "\\\'").replace("\"", "\\\"") +
                                    "', detailsEvent='" + newEvent.getDetail().replace("\'", "\\\'").replace("\"", "\\\"") +
                                    "', idClub=" + String.valueOf(newEvent.getIdClubLinked()) +
                                    ", dayOfEvent='" + newEvent.getStringUniversalDayOfEvent() +
                                    "', dayEndOfEvent='" + newEvent.getStringUniversalDayEndOfEvent() +
                                    "' WHERE idEvent=" + formerEvent.getIdEvent()).execute();
                        }
                    }
                } else {
                    Toast.makeText(AddEventActivity.this, "Veullez remplir tous les champs", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        multiDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondDateLayout.setVisibility(View.VISIBLE);
                } else {
                    secondDateLayout.setVisibility(View.GONE);
                }
            }
        });

        // appelle de la base de donnée pour afficher le drop down des clubs
        new DbClass(SELECT_MODE, "SELECT idClub,titleClub FROM clubdb").execute(); // ORDER BY nameClub Alphabétique
    }

    // DayPickerDialog, selectedMonth commence à 0
    private void showDayPickerDialog(int idDate, int selectedYear, int selectedMonth, int selectedDay,
                                     int selectedHour, int selectedMinute) {

        // Montre le Dialog de selection de date
        datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Dyear, int Dmonth, int DdayOfMonth) {

                interDialog.setDay(String.valueOf(Dyear), String.valueOf(Dmonth), String.valueOf(DdayOfMonth));

                //TimePicker Dialog
                // Montre le Dialog de selection de l'heure
                timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int DhourOfDay, int Dminute) {
                        //Affiche et fixe les variables du nouvel evenement par l'heure choisit
                        interDialog.setTime(String.valueOf(DhourOfDay), String.valueOf(Dminute));

                        if (idDate == FIRST_DATE_ID) {
                            setEvtDayOfEvent(interDialog.year, interDialog.month, interDialog.day, interDialog.hour, interDialog.minute);
                            evtDateTimeStart.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
                                    interDialog.hour, interDialog.minute));
                        } else if (idDate == LAST_DATE_ID) {
                            setEvtDayEndOfEvent(interDialog.year, interDialog.month, interDialog.day, interDialog.hour, interDialog.minute);
                            evtDateTimeEnd.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
                                    interDialog.hour, interDialog.minute));
                        }
                    }
                }, selectedHour, selectedMinute, true);
                timePickerDialog.show();
            }
        }, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void setEvtDayOfEvent(String selectedYear, String selectedMonth, String selectedDay, String selectedHour, String selectedMinute) {
        Date dayToEdit = new Date();
        try {
            dayToEdit = format.parse(selectedYear + "-" + String.valueOf(Integer.valueOf(selectedMonth) + 1) + "-" + selectedDay + " " + selectedHour + ":" + selectedMinute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newEvent.setDayOfEvent(dayToEdit);
    }

    private void setEvtDayEndOfEvent(String selectedYear, String selectedMonth, String selectedDay, String selectedHour, String selectedMinute) {
        Date dayToEdit = new Date();
        try {
            dayToEdit = format.parse(selectedYear + "-" + String.valueOf(Integer.valueOf(selectedMonth) + 1) + "-" + selectedDay + " " + selectedHour + ":" + selectedMinute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newEvent.setDayEndOfEvent(dayToEdit);
    }

    // Connexion à la base de donnée
    public class DbClass extends AsyncTask<Void, Void, List<Club>> {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        // variable récupérées
        int idClub;
        String titleClub;

        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        //contructeur 1
        public DbClass(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<Club> clubListArray) {
            if (modeQuery == INSERT_MODE) {
                if (show_mode == INSERT_EVENT_MODE)
                    Toast.makeText(AddEventActivity.this, "Nouvel évènement crée !", Toast.LENGTH_LONG).show();
                else if (show_mode == EDIT_EVENT_MODE) {
                    Toast.makeText(AddEventActivity.this, "Evènement modifié !", Toast.LENGTH_LONG).show();
                }
                finish();
            } else if (modeQuery == SELECT_MODE) {
                clubList = clubListArray;
                int positionFormerEvtClub = 0;
                int tempPositionClub = 0;
                List<String> nameClubList = new ArrayList<>();
                for (Club clubTmp : clubList) {
                    nameClubList.add(clubTmp.getTitleClub());
                    if (show_mode == EDIT_EVENT_MODE) {
                        if (clubTmp.getIdClub() == formerEvent.getIdClubLinked()) {
                            positionFormerEvtClub = tempPositionClub;
                        }
                        tempPositionClub++;
                    }
                }
                ArrayAdapter clubAdapter = new ArrayAdapter(AddEventActivity.this, android.R.layout.simple_dropdown_item_1line, nameClubList);
                spinner.setAdapter(clubAdapter);
                spinner.setSelection(positionFormerEvtClub);
                spinner.setTitle("Selectioner le club associé");
                spinner.setPositiveButton("OK");
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        positionClubSelected = spinner.getSelectedItemPosition();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }

        @Override
        protected List<Club> doInBackground(Void... params) {
            List clubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                if (modeQuery == SELECT_MODE) {
                    ResultSet resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        idClub = resultSet.getInt("idClub");
                        titleClub = resultSet.getString("titleClub");
                        clubList.add(new Club(idClub, titleClub));
                    }
                    resultSet.close();
                } else if (modeQuery == INSERT_MODE) {
                    stmt.executeUpdate(query);
                }

                stmt.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return clubList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class Club {
        int idClub;
        String titleClub;

        // constructeur
        public Club(int idClub, String titleClub) {
            this.idClub = idClub;
            this.titleClub = titleClub;
        }

        // getter
        public int getIdClub() {
            return idClub;
        }

        public String getTitleClub() {
            return titleClub;
        }
    }

    private class InterDialog {
        String year, month, day, hour, minute;

        public void setDay(String year, String month, String day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public void setTime(String hour, String minute) {
            this.hour = hour;
            this.minute = minute;
        }
    }

    // les mois doivent commencer à 0
    private String showDateTime(String day, String month, String year, String hour, String minute) {
        SimpleDateFormat format = new SimpleDateFormat();
        Date date;
        try {
            format.applyPattern(MainActivity.UNI_DATE_TIME);
            date = format.parse(year + "-" + String.valueOf(Integer.valueOf(month) + 1) + "-" + day + " " + hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(date);
    }
}
