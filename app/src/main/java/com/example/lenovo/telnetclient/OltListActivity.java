package com.example.lenovo.telnetclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lenovo.telnetclient.adapters.ActivityItemAdapter;
import com.example.lenovo.telnetclient.adapters.MyVpgAdapter;
import com.example.lenovo.telnetclient.beans.OltCommand;
import com.example.lenovo.telnetclient.beans.OpticalLineTerminal;
import com.example.lenovo.telnetclient.utlis.MyBaseActivity;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lenovo
 */
public class OltListActivity extends MyBaseActivity {
    @BindView(R.id.list_rcv)
    RecyclerView listRcv;
    @BindView(R.id.list_vpg)
    ViewPager listVpg;
    private String[] items = {"首页", "华为", "中兴", "卡特", "烽火"};
    private List<OpticalLineTerminal> opticalLineTerminals;
    private List<OltCommand> oltCommands;
    private List<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olt_list);
        ButterKnife.bind(this);
        ActivityItemAdapter adapter = new ActivityItemAdapter(Arrays.asList(items), listVpg);
        listRcv.setLayoutManager(new GridLayoutManager(this, items.length));
        listRcv.setAdapter(adapter);
        opticalLineTerminals = new ArrayList<>();
        oltCommands = new ArrayList<>();
        try {
            InputStream open = getResources().openRawResource(R.raw.oltquerydata);
            Workbook workbook = WorkbookFactory.create(open);
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            for (int j = 0; j < rowNum; j++) {
                Row row = sheet.getRow(j);
                OpticalLineTerminal opticalLineTerminal = new OpticalLineTerminal();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    switch (i) {
                        case 0:
                            opticalLineTerminal.setEquipmentName(row.getCell(i).toString());
                            break;
                        case 1:
                            opticalLineTerminal.setEquipmentManufacturers(row.getCell(i).toString
                                    ());
                            break;
                        case 2:
                            opticalLineTerminal.setEquipmentType(row.getCell(i).toString());
                            break;
                        case 3:
                            opticalLineTerminal.setEquipmentIPAddress(row.getCell(i).toString());
                            break;
                        case 4:
                            opticalLineTerminal.setBranchOffice(row.getCell(i).toString());
                            break;
                        case 5:
                            opticalLineTerminal.setAccessServerIP(row.getCell(i).toString());
                            break;
                        case 6:
                            opticalLineTerminal.setAccessServerPort(row.getCell(i).toString());
                            break;
                        default:
                    }
                }
                opticalLineTerminals.add(opticalLineTerminal);
            }
            Sheet sheet1 = workbook.getSheetAt(1);
            int sheet1rowNum = sheet1.getLastRowNum();
            for (int i = 0; i < sheet1rowNum; i++) {
                Row row = sheet1.getRow(i);
                OltCommand oltCommand = new OltCommand();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    switch (j) {
                        case 0:
                            oltCommand.setName(row.getCell(j).toString());
                            break;
                        case 1:
                            oltCommand.setCommand(row.getCell(j).toString());
                            break;
                        case 2:
                            oltCommand.setManufacturers(row.getCell(j).toString());
                            break;
                        default:
                    }
                }
                oltCommands.add(oltCommand);
            }
            open.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        fragments = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            if (i == 0) {
                fragments.add(new MainFragment());
            } else {
                fragments.add(new ScdFragment());
            }
        }
        listVpg.setAdapter(new MyVpgAdapter(getSupportFragmentManager(), fragments));

        for (int i = 0; i < items.length; i++) {
            Bundle data = new Bundle();
            if (i == 0) {
                data.putParcelableArrayList("olt", (ArrayList<? extends Parcelable>)
                        opticalLineTerminals);
                fragments.get(i).setArguments(data);
            } else {
                data.putInt("fragmentPageNo", i);
                fragments.get(i).setArguments(data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        opticalLineTerminals.clear();
        setResult(0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1 && data != null) {
            Bundle ret = data.getExtras();
            assert ret != null;
            int fragmentPageNo = ret.getInt("fragmentPageNo");
            oltCommands = (List<OltCommand>) ret.getSerializable(String.valueOf(fragmentPageNo));
            for (int i = 1; i < fragments.size(); i++) {
                ScdFragment fragment = (ScdFragment) fragments.get(i);
                fragment.refreshData(oltCommands);
            }
        }
    }

    public List<OltCommand> loadData() {return this.oltCommands;}
}
