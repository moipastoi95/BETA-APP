package com.example.george.betamdl;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Parcelable {
    private int idEvent;
    private String title;
    private String detail;

    private String titleClubLinked;
    private int idClubLinked;

    private Date dayOfCreation;
    private Date dayOfEvent;
    private Date dayEndOfEvent;

    transient SimpleDateFormat format = new SimpleDateFormat();

    // constructeur
    public Event(){format.applyPattern(MainActivity.SHOWN_DATE);}

    // constructeur
    public Event(int idEvent, String title, String detail, String titleClubLinked, int idClubLinked) {
        this.idEvent = idEvent;
        this.title = title;
        this.detail = detail;
        this.titleClubLinked = titleClubLinked;
        this.idClubLinked = idClubLinked;

        format.applyPattern(MainActivity.SHOWN_DATE);
    }

    protected Event(Parcel in) {
        // Dans l'ordre
        idEvent = in.readInt();
        title = in.readString();
        try {
            format.applyPattern(MainActivity.UNI_DATE);
            dayOfCreation = format.parse(in.readString());
            format.applyPattern(MainActivity.UNI_DATE_TIME);
            dayOfEvent = format.parse(in.readString());
            dayEndOfEvent = format.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        detail = in.readString();
        titleClubLinked = in.readString();
        idClubLinked = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    //Getter
    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public String getTitleClubLinked() {
        return titleClubLinked;
    }

    public Date getDayOfCreation() {
        return dayOfCreation;
    }

    public Date getDayOfEvent() {
        return dayOfEvent;
    }

    public Date getDayEndOfEvent() {
        return dayEndOfEvent;
    }

    public String getStringDayOfCreation() {
        format.applyPattern(MainActivity.SHOWN_DATE);
        return format.format(dayOfCreation);
    }

    public String getStringDayOfEvent() {
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(dayOfEvent);
    }

    public String getStringDayEndOfEvent() {
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(dayEndOfEvent);
    }

    public String getStringUniversalDayOfCreation() {
        format.applyPattern(MainActivity.UNI_DATE);
        return format.format(dayOfCreation);
    }

    public String getStringUniversalDayOfEvent() {
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        return format.format(dayOfEvent);
    }

    public String getStringUniversalDayEndOfEvent() {
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        return format.format(dayEndOfEvent);
    }

    public int getIdClubLinked() {
        return idClubLinked;
    }


    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setTitleClubLinked(String titleClubLinked) {
        this.titleClubLinked = titleClubLinked;
    }

    public void setIdClubLinked(int idClubLinked) {
        this.idClubLinked = idClubLinked;
    }

    public void setDayOfCreation(Date dayOfCreation) {
        this.dayOfCreation = dayOfCreation;
    }

    public void setDayOfEvent(Date dayOfEvent) {
        this.dayOfEvent = dayOfEvent;
    }

    public void setDayEndOfEvent(Date dayEndOfEvent) {
        this.dayEndOfEvent = dayEndOfEvent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // Dans l'ordre
        parcel.writeInt(idEvent);
        parcel.writeString(title);
        format.applyPattern(MainActivity.UNI_DATE);
        parcel.writeString(format.format(dayOfCreation));
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        parcel.writeString(format.format(dayOfEvent));
        parcel.writeString(format.format(dayEndOfEvent));
        parcel.writeString(detail);
        parcel.writeString(titleClubLinked);
        parcel.writeInt(idClubLinked);
    }
}
