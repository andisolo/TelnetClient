package com.example.lenovo.telnetclient.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.telnetclient.R;

import java.util.List;

/**
 * Created on 2017/11/3.
 *
 * @author GuoJiaming
 */

public class ActivityItemAdapter extends RecyclerView.Adapter<ActivityItemAdapter.ViewHolder> {
    private List<String> list;
    private ViewPager viewPager;

    public ActivityItemAdapter(List<String> list,ViewPager viewPager) {
        this.list = list;
        this.viewPager= viewPager;
    }

    @Override
    public ActivityItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R
                .layout.item_for_rcv, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityItemAdapter.ViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
        holder.textView.setOnClickListener(view -> viewPager.setCurrentItem(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ViewHolder(View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.item_for_rcv_text_01);
        }
    }
}
