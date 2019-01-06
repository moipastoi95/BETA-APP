package com.example.george.betamdl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    // Variables liées au layout
    private EditText evtTitle;
    private EditText evtDetail;
    private Button evtDate;
    private Button evtHour;
    private Button evtPic;
    private Button sendBtn;
    private Button backBtn;

    // Object du nouvel évènement
    private Event newEvent;

    // Variables pour afficher les PickerDialogue
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    // Variables d'aujourd'hui
    int dayNow;
    int monthNow;
    int yearNow;
    int hourNow;
    int minuteNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setTitle("Ajouter un énènement");

        // initialise les variables d'aujourd'hui
        calendar = Calendar.getInstance();
        dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        monthNow = calendar.get(Calendar.MONTH);
        yearNow = calendar.get(Calendar.YEAR);
        hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        minuteNow = calendar.get(Calendar.MINUTE);

        // Création de l'object "Nouvel Evenement"
        newEvent = new Event();

        // Initialisation des éléments du layout
        evtTitle = (EditText) findViewById(R.id.evtNewTitle);
        evtDetail = (EditText) findViewById(R.id.evtNewDetail);
        evtDate = (Button) findViewById(R.id.evtNewDate);
        evtDate.setText(DateShow(dayNow,monthNow,yearNow));
        evtHour = (Button) findViewById(R.id.evtNewHour);
        evtHour.setText(TimeShow(hourNow, minuteNow));
        sendBtn = (Button) findViewById(R.id.evtNewSendBtn);

        // DatePicker Dialog
        evtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // les donne à l'objet du nouvel evenement
                newEvent.setDayOfMonthCreation(dayNow);
                newEvent.setDayOfMonthCreation(monthNow);
                newEvent.setYearCreation(yearNow);

                // Montre le Dialog de selection de date
                datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Dyear, int Dmonth, int DdayOfMonth) {
                        //Affiche et fixe les variables du nouvel evenement par la date choisit
                        // Le mois +1 car pour android, les mois commencent à 0 et non à 1
                        evtDate.setText(DateShow(DdayOfMonth,Dmonth,Dyear));
                        newEvent.setDayOfMonthEventIs(DdayOfMonth);
                        newEvent.setMonthEventIs(Dmonth);
                        newEvent.setYearEventIs(Dyear);
                    }
                }, yearNow, monthNow, dayNow);
                datePickerDialog.show();
            }
        });

        //TimePicker Dialog
        evtHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Montre le Dialog de selection de l'heure
                timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int DhourOfDay, int Dminute) {
                        //Affiche et fixe les variables du nouvel evenement par l'heure choisit
                        evtHour.setText(TimeShow(DhourOfDay, Dminute));
                        newEvent.setHourEventIs(DhourOfDay);
                        newEvent.setMinuteEventIs(Dminute);
                    }
                }, hourNow, minuteNow, true);
                timePickerDialog.show();
            }
        });

        // Send Button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tester si tous les champs ont été remplis
                if (!evtTitle.getText().toString().isEmpty() && !evtDetail.getText().toString().isEmpty()) {
                    newEvent.setTitle(evtTitle.getText().toString());
                    newEvent.setDetail(evtDetail.getText().toString());
                    // Faire quelque chose avec la base de donnée pour ajouter un évènement avec l'object event
                    Toast.makeText(AddEventActivity.this, "Nouvel évènement crée !", Toast.LENGTH_LONG).show();
                    finish();
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
    }

    private String DateShow(int theDay, int theMonth, int theYear) {
        theMonth++;
        String dayStr = (theDay<10) ? "0"+String.valueOf(theDay) : String.valueOf(theDay);
        String monthStr = (theMonth<10) ? "0"+String.valueOf(theMonth) : String.valueOf(theMonth);
        return dayStr+"/"+monthStr+"/"+theYear;
    }
    private String TimeShow(int theHour, int theMinute){
        String hourStr = (theHour<10) ? "0"+String.valueOf(theHour) : String.valueOf(theHour);
        String minuteStr = (theMinute<10) ? "0"+String.valueOf(theMinute) : String.valueOf(theMinute);
        return hourStr+":"+minuteStr;
    }
}
