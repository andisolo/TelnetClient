package com.example.lenovo.telnetclient.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created on 2017/11/3.
 *
 * @author GuoJiaming
 */

public class OpticalLineTerminal implements Serializable, Parcelable {
    private static final long serialVersionUID = 1778303447695853320L;
    private String equipmentName;
    private String equipmentManufacturers;
    private String equipmentType;
    private String equipmentIPAddress;
    private String branchOffice;
    private String accessServerIP;
    private String accessServerPort;

    public OpticalLineTerminal() {}
    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentManufacturers() {
        return equipmentManufacturers;
    }

    public void setEquipmentManufacturers(String equipmentManufacturers) {
        this.equipmentManufacturers = equipmentManufacturers;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentIPAddress() {
        return equipmentIPAddress;
    }

    public void setEquipmentIPAddress(String equipmentIPAddress) {
        this.equipmentIPAddress = equipmentIPAddress;
    }

    public String getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

    public String getAccessServerIP() {
        return accessServerIP;
    }

    public void setAccessServerIP(String accessServerIP) {
        this.accessServerIP = accessServerIP;
    }

    public String getAccessServerPort() {
        return accessServerPort;
    }

    public void setAccessServerPort(String accessServerPort) {
        this.accessServerPort = accessServerPort;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.equipmentName);
        dest.writeString(this.equipmentManufacturers);
        dest.writeString(this.equipmentType);
        dest.writeString(this.equipmentIPAddress);
        dest.writeString(this.branchOffice);
        dest.writeString(this.accessServerIP);
        dest.writeString(this.accessServerPort);
    }

    private OpticalLineTerminal(Parcel in) {
        this.equipmentName = in.readString();
        this.equipmentManufacturers = in.readString();
        this.equipmentType = in.readString();
        this.equipmentIPAddress = in.readString();
        this.branchOffice = in.readString();
        this.accessServerIP = in.readString();
        this.accessServerPort = in.readString();
    }

    public static final Parcelable.Creator<OpticalLineTerminal> CREATOR = new Parcelable
            .Creator<OpticalLineTerminal>() {
        @Override
        public OpticalLineTerminal createFromParcel(Parcel source) {return new OpticalLineTerminal(source);}

        @Override
        public OpticalLineTerminal[] newArray(int size) {return new OpticalLineTerminal[size];}
    };
}
