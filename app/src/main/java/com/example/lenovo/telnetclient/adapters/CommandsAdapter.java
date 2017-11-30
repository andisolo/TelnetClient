package com.example.lenovo.telnetclient.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.telnetclient.R;
import com.example.lenovo.telnetclient.beans.Command;
import com.example.lenovo.telnetclient.networks.MyNetWorks;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 2017/11/15.
 *
 * @author GuoJiaming
 */

public class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.ViewHolder> {
    private List<Command> list;
    private MyNetWorks myNetWorks;
    private android.support.v7.app.AlertDialog alertDialog;

    public CommandsAdapter(List<Command> list, MyNetWorks myNetWorks, android.support.v7.app.AlertDialog alertDialog) {
        this.list = list;
        this.myNetWorks=myNetWorks;
        this.alertDialog=alertDialog;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_for_commands_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemForCommands.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(view -> {
            myNetWorks.writeData(list.get(position).getCommand());
            if (alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            list.remove(position);
            this.notifyDataSetChanged();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_for_commands)
        TextView itemForCommands;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
