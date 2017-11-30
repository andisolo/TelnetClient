package com.example.lenovo.telnetclient.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created on 2017/11/15.
 *
 * @author GuoJiaming
 */

public class Command implements Serializable, Parcelable {
    private static final long serialVersionUID = -5327722060649458353L;
    private String name;
    private String command;

    public Command(){}

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

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.command);
    }

    private Command(Parcel in) {
        this.name = in.readString();
        this.command = in.readString();
    }

    public static final Parcelable.Creator<Command> CREATOR = new Parcelable.Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel source) {return new Command(source);}

        @Override
        public Command[] newArray(int size) {return new Command[size];}
    };
}
