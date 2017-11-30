package com.example.lenovo.telnetclient.networks;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;

/**
 * Created on 2017/11/11.
 *
 * @author GuoJiaming
 */

public class NetWorks implements TelnetNotificationHandler {
    private static final String TAG = "networkRec";

    @Override
    public void receivedNegotiation(int negotiationCode, int optionCode) {

        String command;
        switch (negotiationCode) {
            case TelnetNotificationHandler.RECEIVED_DO:
                command = "DO";
                break;
            case TelnetNotificationHandler.RECEIVED_DONT:
                command = "DONT";
                break;
            case TelnetNotificationHandler.RECEIVED_WILL:
                command = "WILL";
                break;
            case TelnetNotificationHandler.RECEIVED_WONT:
                command = "WONT";
                break;
            case TelnetNotificationHandler.RECEIVED_COMMAND:
                command = "COMMAND";
                break;
            default:
                command = Integer.toString(negotiationCode);
                break;
        }
        Log.i(TAG, "receivedNegotiation: " + command +"-->"+ TelnetOption
                .getOption(optionCode));
    }
}
