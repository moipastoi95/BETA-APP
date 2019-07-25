package com.example.george.betamdl;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int idUser;
    private String nameUser;
    private String familyNameUser;
    private String passwordUser;
    private int levelAccess;

    public User(int idUser, String nameUser, String familyNameUser, String passwordUser, int levelAccess) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.familyNameUser = familyNameUser;
        this.passwordUser = passwordUser;
        this.levelAccess = levelAccess;
    }

    protected User(Parcel in) {
        idUser = in.readInt();
        nameUser = in.readString();
        familyNameUser = in.readString();
        passwordUser = in.readString();
        levelAccess = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // getter
    public int getIdUser() {
        return idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public String getFamilyNameUser() {
        return familyNameUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public int getLevelAccess() {
        return levelAccess;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // dans l'ordre
        parcel.writeInt(idUser);
        parcel.writeString(nameUser);
        parcel.writeString(familyNameUser);
        parcel.writeString(passwordUser);
        parcel.writeInt(levelAccess);
    }
}
