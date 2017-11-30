package com.example.lenovo.telnetclient.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lenovo.telnetclient.R;
import com.example.lenovo.telnetclient.beans.OpticalLineTerminal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 2017/11/3.
 *
 * @author GuoJiaming
 */

public class OltAdapter extends RecyclerView.Adapter<OltAdapter.ViewHolder> {
    private List<OpticalLineTerminal> list;
    private ViewGroup viewGroup;

    public OltAdapter(List<OpticalLineTerminal> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_for_olt_list, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemForName.setText(list.get(position).getEquipmentName());
        holder.itemForManufacturer.setText(list.get(position).getEquipmentManufacturers());
        holder.itemForType.setText(list.get(position).getEquipmentType());
        holder.itemForIp.setText(list.get(position).getEquipmentIPAddress());
        holder.itemForOffice.setText(list.get(position).getBranchOffice());
        holder.itemForBrasIp.setText(list.get(position).getAccessServerIP());
        holder.itemForBrasPort.setText(list.get(position).getAccessServerPort());
        holder.itemView.setOnClickListener((View view) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
            View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .item_for_alter_dialog, viewGroup, false);
            ListView listView = view1.findViewById(R.id.dialog_listV);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(viewGroup.getContext(), android.R
                    .layout.simple_list_item_1);
            adapter.add("ping测试");
            adapter.add("telnet");
            listView.setAdapter(adapter);
            builder.setView(view1);
            builder.setCancelable(true);
            builder.setTitle("快捷操作").setMessage("请选择。。。").show();
            listView.setOnItemClickListener((adapterView, view2, i, l) -> {
                Activity activity = (Activity) viewGroup.getContext();
                Intent intent = new Intent();
                switch (i) {
                    case 0:
                        intent.putExtra("ret", "ping " + holder.itemForIp.getText().toString() +
                                "\r");
                        activity.setResult(1, intent);
                        activity.finish();
                        break;
                    case 1:
                        intent.putExtra("ret", "telnet " + holder.itemForIp.getText().toString() +
                                "\r");
                        activity.setResult(1, intent);
                        activity.finish();
                        break;
                    default:
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_for_name)
        TextView itemForName;
        @BindView(R.id.item_for_manufacturer)
        TextView itemForManufacturer;
        @BindView(R.id.item_for_type)
        TextView itemForType;
        @BindView(R.id.item_for_ip)
        TextView itemForIp;
        @BindView(R.id.item_for_office)
        TextView itemForOffice;
        @BindView(R.id.item_for_bras_ip)
        TextView itemForBrasIp;
        @BindView(R.id.item_for_bras_port)
        TextView itemForBrasPort;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
