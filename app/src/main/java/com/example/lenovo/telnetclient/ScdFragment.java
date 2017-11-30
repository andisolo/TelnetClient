package com.example.lenovo.telnetclient;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.lenovo.telnetclient.adapters.OltCommandsAdapter;
import com.example.lenovo.telnetclient.beans.OltCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 2017/11/7.
 *
 * @author GuoJiaming
 * @date 2017/11/7.
 */


public class ScdFragment extends Fragment {
    @BindView(R.id.main_fragment_sch_text)
    AutoCompleteTextView mainFragmentSchText;
    @BindView(R.id.main_fragment_sch_btn)
    TextView mainFragmentSchBtn;
    @BindView(R.id.main_fragment_rcv)
    RecyclerView mainFragmentRcv;
    Unbinder unbinder;
    private List<OltCommand> oltCommands;
    private OltCommandsAdapter adapter;
    private int fragmentPageNo;
    private int position;
    private List<String> factoredList = Arrays.asList("华为", "中兴", "卡特", "烽火");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oltCommands = new ArrayList<>();
        List<String> searchList = new ArrayList<>();
        Bundle bundle = getArguments();
        assert bundle != null;
        fragmentPageNo = bundle.getInt("fragmentPageNo");
        OltListActivity activity = (OltListActivity) getActivity();
        assert activity != null;
        for (OltCommand oltCommand : activity.loadData()) {
            if (oltCommand.getManufacturers().equals(factoredList.get(fragmentPageNo - 1))) {
                oltCommands.add(oltCommand);
                searchList.add(oltCommand.getName());
                searchList.add(oltCommand.getCommand());
            }
        }
        mainFragmentRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new OltCommandsAdapter(oltCommands, fragmentPageNo);
        mainFragmentRcv.setAdapter(adapter);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout
                .simple_list_item_1, searchList);
        mainFragmentSchText.setAdapter(arrayAdapter);
        mainFragmentSchBtn.setOnClickListener(view1 -> {
            for (int i = 0; i < oltCommands.size(); i++) {
                if (mainFragmentSchText.getText().toString().equals(oltCommands.get(i).getCommand
                        ()) || mainFragmentSchText.getText().toString().equals(oltCommands.get(i)
                        .getCommand())) {
                    mainFragmentRcv.scrollToPosition(i);
                    position = i;
                    new Thread(runnable).start();
                    return;
                }
            }
        });
    }


    public void refreshData(List<OltCommand> oltCommands) {
        this.oltCommands = oltCommands;
        if (adapter != null) {
            adapter.refreshList(this.oltCommands);
        }
    }

    Handler handler = new Handler(message -> {
        switch (message.what) {
            case 0:
                mainFragmentRcv.getLayoutManager().findViewByPosition(position)
                        .setBackgroundColor(Color.RED);
                break;
            case 1:
                mainFragmentRcv.getLayoutManager().findViewByPosition(position)
                        .setBackgroundColor(Color.BLACK);
                break;
            default:
        }
        return true;
    });
    Runnable runnable = () -> {
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);
        message = new Message();
        message.what = 1;
        handler.sendMessageDelayed(message, 2000);
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        fragmentPageNo = -1;
    }
}
