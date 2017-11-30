package com.example.lenovo.telnetclient.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created on 2017/11/16.
 *
 * @author GuoJiaming
 */

public class OltCommand implements Serializable, Parcelable {
    private static final long serialVersionUID = 1773241648289938215L;
    private String name;
    private String command;
    private String manufacturers;

    public OltCommand() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(String manufacturers) {
        this.manufacturers = manufacturers;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.command);
        dest.writeString(this.manufacturers);
    }

    private OltCommand(Parcel in) {
        this.name = in.readString();
        this.command = in.readString();
        this.manufacturers = in.readString();
    }

    public static final Parcelable.Creator<OltCommand> CREATOR = new Parcelable
            .Creator<OltCommand>() {
        @Override
        public OltCommand createFromParcel(Parcel source) {return new OltCommand(source);}

        @Override
        public OltCommand[] newArray(int size) {return new OltCommand[size];}
    };
}
