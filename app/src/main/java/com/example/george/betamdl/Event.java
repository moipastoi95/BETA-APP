package com.example.george.betamdl;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private int idEvent;
    private String title;
    private String detail;

    private String nameClubLinked;
    private int idClubLinked;

    private int dayOfMonthEventIs;
    private int monthEventIs;
    private int yearEventIs;
    private int hourEventIs;
    private int minuteEventIs;

    private int dayOfMonthCreation;
    private int monthCreation;
    private int yearCreation;
    //private Date dateOfEvent;
    //Dates ...

    public Event(){}

    public Event(int idEvent, String title, String detail, String nameClubLinked, int idClubLinked) {
        this.idEvent = idEvent;
        this.title = title;
        this.detail = detail;
        this.nameClubLinked = nameClubLinked;
        this.idClubLinked = idClubLinked;
    }

    protected Event(Parcel in) {
        idEvent = in.readInt();
        title = in.readString();
        detail = in.readString();
        nameClubLinked = in.readString();
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

    public String getNameClubLinked() {
        return nameClubLinked;
    }

    public int getIdClubLinked() {
        return idClubLinked;
    }

    public int getDayOfMonthEventIs() {
        return dayOfMonthEventIs;
    }

    public int getMonthEventIs() {
        return monthEventIs;
    }

    public int getYearEventIs() {
        return yearEventIs;
    }

    public int getHourEventIs() {
        return hourEventIs;
    }

    public int getMinuteEventIs() {
        return minuteEventIs;
    }

    public int getDayOfMonthCreation() {
        return dayOfMonthCreation;
    }

    public int getMonthCreation() {
        return monthCreation;
    }

    public int getYearCreation() {
        return yearCreation;
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setIdClubLinked(int idClubLinked) {
        this.idClubLinked = idClubLinked;
    }

    public void setDayOfMonthEventIs(int dayOfMonthEventIs) {
        this.dayOfMonthEventIs = dayOfMonthEventIs;
    }

    public void setMonthEventIs(int monthEventIs) {
        this.monthEventIs = monthEventIs;
    }

    public void setYearEventIs(int yearEventIs) {
        this.yearEventIs = yearEventIs;
    }

    public void setHourEventIs(int hourEventIs) {
        this.hourEventIs = hourEventIs;
    }

    public void setMinuteEventIs(int minuteEventIs) {
        this.minuteEventIs = minuteEventIs;
    }

    public void setDayOfMonthCreation(int dayOfMonthCreation) {
        this.dayOfMonthCreation = dayOfMonthCreation;
    }

    public void setMonthCreation(int monthCreation) {
        this.monthCreation = monthCreation;
    }

    public void setYearCreation(int yearCreation) {
        this.yearCreation = yearCreation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(idEvent);
        parcel.writeString(title);
        parcel.writeString(detail);
        parcel.writeString(nameClubLinked);
        parcel.writeInt(idClubLinked);
    }
}
