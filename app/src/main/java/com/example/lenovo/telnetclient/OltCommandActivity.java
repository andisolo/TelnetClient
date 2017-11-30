package com.example.lenovo.telnetclient;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.example.lenovo.telnetclient.beans.OltCommand;
import com.example.lenovo.telnetclient.fillblankview.AnswerRange;
import com.example.lenovo.telnetclient.fillblankview.FillBlankView;
import com.example.lenovo.telnetclient.utlis.MyBaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 2017/11/16.
 *
 * @author GuoJiaming
 */

public class OltCommandActivity extends MyBaseActivity {
    @BindView(R.id.command_info)
    FillBlankView commandInfo;
    private static final String COMMAND = "参数";
    private List<OltCommand> list;
    private Intent reIntent;
    private Bundle data;
    private int position;
    private int fragmentPageNo;
    private String command;
    private List<AnswerRange> answerRangeList;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olt_command);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        fragmentPageNo = intent.getIntExtra("fragmentPageNo", -1);
        position = intent.getIntExtra("position", -1);
        list =  intent.getParcelableArrayListExtra("command");
        answerRangeList = new ArrayList<>();
        command = list.get(position).getCommand();
        parameterFilter();
        reIntent = new Intent();
        data = new Bundle();
        commandInfo.setData(command, answerRangeList);
        commandInfo.addOnLayoutChangeListener((View view, int i, int i1, int i2, int i3, int i4,
                                               int i5, int i6, int i7) -> {
            OltCommand oltCommand = list.get(position);
            oltCommand.setCommand(commandInfo.getTextString());
            list.set(position, oltCommand);
            data.putInt("fragmentPageNo", fragmentPageNo);
            data.putSerializable(String.valueOf(fragmentPageNo), (Serializable) list);
            reIntent.putExtras(data);
            setResult(1, reIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(0);
    }

    private void parameterFilter() {
        int i = 0;
        while (command.indexOf(COMMAND, i) != -1 && i < command.length()) {
            int j = command.indexOf(COMMAND, i) + COMMAND.length();
            answerRangeList.add(new AnswerRange(j - COMMAND.length(), j));
            i = j;
        }
    }
}
