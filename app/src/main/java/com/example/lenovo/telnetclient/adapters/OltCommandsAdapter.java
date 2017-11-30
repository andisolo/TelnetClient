package com.example.lenovo.telnetclient.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.telnetclient.OltCommandActivity;
import com.example.lenovo.telnetclient.R;
import com.example.lenovo.telnetclient.beans.OltCommand;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 2017/11/16.
 *
 * @author GuoJiaming
 */

public class OltCommandsAdapter extends RecyclerView.Adapter<OltCommandsAdapter.ViewHolder> {
    private List<OltCommand> list;
    private ViewGroup parent;
    private int fragmentPageNo;
    private Activity activity;

    public OltCommandsAdapter(List<OltCommand> list, int fragmentPageNo) {
        this.list = list;
        this.fragmentPageNo = fragmentPageNo;
    }

    public void refreshList(List<OltCommand> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_for_olt_commands, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OltCommandsAdapter adapter = this;
        holder.itemForCommandInfo.setText(list.get(position).getCommand());
        holder.itemForCommandName.setText(list.get(position).getName());
        holder.itemForCommandSyl.setText(list.get(position).getManufacturers());
        holder.itemView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("命令操作").setMessage("请选择").setCancelable(true);
            builder.setPositiveButton("执行命令", (dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.putExtra("ret", holder.itemForCommandInfo.getText().toString());
                activity = (Activity) parent.getContext();
                activity.setResult(1, intent);
                activity.finish();
            });
            builder.setNegativeButton("编辑命令", (dialogInterface, i) -> {
                Intent intent = new Intent(parent.getContext(), OltCommandActivity.class);
                intent.putParcelableArrayListExtra("command", (ArrayList<? extends Parcelable>)
                        list);
                intent.putExtra("position", position);
                intent.putExtra("fragmentPageNo", fragmentPageNo);
                Activity activity = (Activity) parent.getContext();
                activity.startActivityForResult(intent, 1);
            });
            builder.setNeutralButton("删除命令", (dialogInterface, i) -> {
                list.remove(position);
                adapter.notifyDataSetChanged();
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_for_command_name)
        TextView itemForCommandName;
        @BindView(R.id.item_for_command_info)
        TextView itemForCommandInfo;
        @BindView(R.id.item_for_command_syl)
        TextView itemForCommandSyl;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
