package com.example.george.betamdl;

import android.os.Parcel;
import android.os.Parcelable;

public class Club  implements Parcelable {
    private int idClub;
    private String titleClub;
    private String detailsClub;

    // Constructeur
    public Club() {
    }

    public Club(int idClub, String titleClub, String detailsClub){
        this.idClub = idClub;
        this.titleClub = titleClub;
        this.detailsClub = detailsClub;
    }

    protected Club(Parcel in) {
        // dans l'ordre
        idClub = in.readInt();
        titleClub = in.readString();
        detailsClub = in.readString();
    }

    public static final Creator<Club> CREATOR = new Creator<Club>() {
        @Override
        public Club createFromParcel(Parcel in) {
            return new Club(in);
        }

        @Override
        public Club[] newArray(int size) {
            return new Club[size];
        }
    };

    // getter
    public String getTitleClub() {
        return titleClub;
    }

    public String getDetailsClub() {
        return detailsClub;
    }

    public int getIdClub() {
        return idClub;
    }

    // setter
    public void setIdClub(int idClub) {
        this.idClub = idClub;
    }

    public void setTitleClub(String titleClub) {
        this.titleClub = titleClub;
    }

    public void setDetailsClub(String detailsClub) {
        this.detailsClub = detailsClub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dans l'ordre
        dest.writeInt(idClub);
        dest.writeString(titleClub);
        dest.writeString(detailsClub);
    }
}
