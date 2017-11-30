package com.example.lenovo.telnetclient;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.lenovo.telnetclient.adapters.FilterAdapter;
import com.example.lenovo.telnetclient.adapters.OltAdapter;
import com.example.lenovo.telnetclient.beans.OpticalLineTerminal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 2017/11/3.
 *
 * @author GuoJiaming
 */

public class MainFragment extends Fragment {
    @BindView(R.id.main_fragment_rcv)
    RecyclerView mainFragmentRcv;
    Unbinder unbinder;
    @BindView(R.id.main_fragment_sch_text)
    AutoCompleteTextView mainFragmentSchText;
    @BindView(R.id.main_fragment_sch_btn)
    TextView mainFragmentSchBtn;
    List<String> ipAndNameList;
    private List<OpticalLineTerminal> list;
    private Integer mPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        list = bundle.getParcelableArrayList("olt");
        OltAdapter adapter = new OltAdapter(list);
        mainFragmentRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainFragmentRcv.setAdapter(adapter);
        ipAndNameList = new ArrayList<>();
        for (OpticalLineTerminal opticalLineTerminal : list) {
            ipAndNameList.add(opticalLineTerminal.getEquipmentIPAddress());
            ipAndNameList.add(opticalLineTerminal.getEquipmentName());
        }
        FilterAdapter<String> arrayAdapter = new FilterAdapter<>(getActivity(), android.R.layout
                .simple_list_item_1, ipAndNameList);
        mainFragmentSchText.setAdapter(arrayAdapter);
        mainFragmentSchText.setOnClickListener(view12 -> mainFragmentSchText.setText(""));
        mainFragmentSchBtn.setOnClickListener(view1 -> {
            for (int i = 0; i < list.size(); i++) {
                if ((list.get(i).getEquipmentIPAddress().equals(mainFragmentSchText
                        .getText().toString())) || (list.get(i).getEquipmentName().equals
                        (mainFragmentSchText.getText().toString()))) {
                    mainFragmentRcv.scrollToPosition(i);
                    mPosition = i;
                    new Thread(runnable).start();
                    return;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    Handler handler = new Handler(message -> {
        switch (message.what) {
            case 0:
                mainFragmentRcv.getLayoutManager().findViewByPosition(mPosition)
                        .setBackgroundColor(Color.RED);
                break;
            case 1:
                mainFragmentRcv.getLayoutManager().findViewByPosition(mPosition)
                        .setBackgroundColor(303030);
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
}
