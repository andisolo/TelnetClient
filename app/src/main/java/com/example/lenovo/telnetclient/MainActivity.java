package com.example.lenovo.telnetclient;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.telnetclient.adapters.CommandsAdapter;
import com.example.lenovo.telnetclient.beans.Command;
import com.example.lenovo.telnetclient.networks.MyNetWorks;
import com.example.lenovo.telnetclient.utlis.ActivityMng;
import com.example.lenovo.telnetclient.utlis.MyBaseActivity;
import com.example.lenovo.telnetclient.utlis.MyToast;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.commons.net.telnet.TelnetClient;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lenovo
 */
public class MainActivity extends MyBaseActivity {
    private static final int MAX_SIZE = 80;
    private static final int MINI_SIZE = 20;
    private static final String CHA_SET_TYPE = "GBK";
    private static final String TAG = "main";
    @BindView(R.id.main_text)
    TextView mainText;
    @BindView(R.id.main_edt)
    TextView mainEdt;
    @BindView(R.id.main_text01)
    TextView mainText01;
    @BindView(R.id.main_text02)
    RoundedImageView mainText02;
    @BindView(R.id.main_keyboard)
    KeyboardView mainKeyboard;
    @BindView(R.id.main_scrollV)
    ScrollView mainScrollV;
    @BindView(R.id.main_quick_in)
    TextView mainQuickIn;
    private TelnetClient telnetClient;
    private String[] ipAndPort;
    private MyNetWorks netWorks;
    private boolean sendUserNameAndPassword;
    private boolean upOrLow;
    private String username;
    private String password;
    private Keyboard numberKeyboard;
    private Keyboard symKeyboard;
    private Keyboard keyboard;
    private File commands;
    private List<Command> list;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sendUserNameAndPassword = false;
        upOrLow = false;
        saveUserInfo();
        commands = getFileStreamPath("commands");
        netWorks = new MyNetWorks();
        telnetClient = MyNetWorks.getTelnetClient();
        getInfo();
        mainText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mainText.clearFocus();
                scrollToBottom(mainScrollV, mainText);
            }
        });
        keyboard = new Keyboard(this, R.xml.m_keyborad);
        numberKeyboard = new Keyboard(this, R.xml.m_keyboard_mun);
        symKeyboard = new Keyboard(this, R.xml.m_keyboard_symbol);
        mainKeyboard.setKeyboard(keyboard);
        mainKeyboard.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int i) {}

            @Override
            public void onRelease(int i) {}

            @Override
            public void onKey(int i, int[] ints) {
                if (telnetClient != null && telnetClient.isConnected()) {
                    switch (i) {
                        case -2:
                            mainKeyboard.setKeyboard(numberKeyboard);
                            break;
                        case -7:
                            mainKeyboard.setKeyboard(symKeyboard);
                            break;
                        case -8:
                            mainKeyboard.setKeyboard(keyboard);
                            break;
                        case -3:
                            netWorks.writeData("\r\n");
                            break;
                        case -5:
                            netWorks.writeData("\b");
                            break;
                        case -1:
                            upOrLow = !upOrLow;
                            if (upOrLow) {
                                mainKeyboard.setBackgroundColor(Color.WHITE);
                            } else {
                                mainKeyboard.setBackgroundColor(Color.BLACK);
                            }
                            break;
                        case -11:
                            MainActivity.this.finish();
                            break;
                        case -12:
                            final String[] items = new String[]{"跳转到OLT列表", "CTRL_C", "CTRL_K"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity
                                    .this);
                            builder.setCancelable(true);
                            builder.setTitle("辅助输入功能");
                            builder.setItems(items, (dialogInterface, j) -> {
                                switch (j) {
                                    case 0:
                                        Intent intent = new Intent(MainActivity.this,
                                                OltListActivity.class);
                                        startActivityForResult(intent, 1);
                                        break;
                                    case 1:
                                        netWorks.writeData("\003");
                                        break;
                                    case 2:
                                        netWorks.writeData("\013");
                                        break;
                                    default:
                                }
                            });
                            builder.setCancelable(true);
                            builder.create().show();
                            break;
                        default:
                            if (upOrLow) {
                                netWorks.writeData(new String(new byte[]{(byte) i}).toUpperCase());
                            } else {
                                netWorks.writeData((byte) i);
                            }
                            break;
                    }
                } else {
                    MyToast.showShort(MainActivity.this, "链接已经断开");
                }

            }

            @Override
            public void onText(CharSequence charSequence) {}

            @Override
            public void swipeLeft() {}

            @Override
            public void swipeRight() {}

            @Override
            public void swipeDown() {}

            @Override
            public void swipeUp() {}
        });
        mainText.setOnClickListener(view -> mainKeyboard.setVisibility(View.GONE));
        mainEdt.setOnClickListener(view -> mainKeyboard.setVisibility(View.VISIBLE));
        mainQuickIn.setOnClickListener(view -> {
            if (telnetClient != null && telnetClient.isConnected() && commands != null) {
                saveCommands();
            }
        });
        mainQuickIn.setOnLongClickListener(view -> {
            ClipboardManager cbm = (ClipboardManager) getSystemService(Context
                    .CLIPBOARD_SERVICE);
            ClipData clipData = cbm.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                if (telnetClient != null && telnetClient.isConnected()) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        netWorks.writeData(clipData.getItemAt(i).getText().toString());
                    }
                } else {
                    MyToast.showShort(MainActivity.this, "链接已经断开");
                }
            }
            return true;
        });
    }


    private void saveUserInfo() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        username = bundle.getString("username");
        password = bundle.getString("password");
        boolean checkIn = bundle.getBoolean("check");
        ipAndPort = bundle.getString("ipAndPort").split(" ");
        if (!"".equals(username) && !"".equals(password) && checkIn) {
            sendUserNameAndPassword = true;
        }
    }

    private void saveCommands() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("脚本编辑器").setMessage("请选择......" + "\n\n长按脚本删除......(删除后请点击保存)");
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout
                .commans_dialog, null);
        builder.setView(view).setCancelable(true);
        RecyclerView recyclerView = view.findViewById(R.id.main_dialog_recV);
        EditText editTextName = view.findViewById(R.id.main_dialog_edt_for_name);
        EditText editTextCommand = view.findViewById(R.id.main_dialog_edt_for_command);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(commands);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            list = (List<Command>) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        builder.setNegativeButton("新增", (dialogInterface, i) -> {
            if (!"".equals(editTextName.getText().toString()) && !"".equals(editTextCommand
                    .getText().toString())) {
                Command newCommand = new Command();
                newCommand.setName(editTextName.getText().toString());
                newCommand.setCommand(editTextCommand.getText().toString());
                list.add(newCommand);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(commands);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream
                            (fileOutputStream);
                    objectOutputStream.writeObject(list);
                    MyToast.showShort(MainActivity.this, "保存成功");
                    fileOutputStream.flush();
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                MyToast.showShort(MainActivity.this, "脚本不能为空");
            }
        });
        builder.setPositiveButton("保存", (dialogInterface, i) -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(commands);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream
                        (fileOutputStream);
                objectOutputStream.writeObject(list);
                MyToast.showShort(MainActivity.this, "保存成功");
                fileOutputStream.flush();
                objectOutputStream.flush();
                objectOutputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AlertDialog alertDialog = builder.create();
        CommandsAdapter adapter = new CommandsAdapter(list, netWorks, alertDialog);
        recyclerView.setAdapter(adapter);
        alertDialog.show();
        view.addOnLayoutChangeListener((view1, i, i1, i2, i3, i4, i5, i6, i7) -> {
            int height = view1.getHeight();
            int needHeight = 700;
            if (height > needHeight) {
                view1.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                        .MATCH_PARENT, needHeight));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.main_text01)
    public void onViewClicked() {
        if (telnetClient != null && telnetClient.isConnected()) {
            netWorks.writeData("\t");
        } else {
            reboot();
        }
    }

    private void reboot() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode + "----" + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mainText.getTextSize() < MAX_SIZE) {
                    mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainText.getTextSize() + 2);
                } else {
                    MyToast.showShort(MainActivity.this, "不能更大了");
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mainText.getTextSize() > MINI_SIZE) {
                    mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainText.getTextSize() - 2);
                } else {
                    MyToast.showShort(MainActivity.this, "不能更小了");
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("请确认是否退出系统").setMessage("真的要退出吗")
                        .setPositiveButton("确认", (dialog, which) ->
                                ActivityMng.removeAllActivity());
                alertDialog.setCancelable(true);
                alertDialog.show();
                break;
            case KeyEvent.KEYCODE_MENU:
                final String[] items = new String[]{"跳转到OLT列表", "CTRL_C", "CTRL_K"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("辅助输入功能");
                builder.setItems(items, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            Intent intent = new Intent(MainActivity.this, OltListActivity.class);
                            startActivityForResult(intent, 1);
                            break;
                        case 1:
                            netWorks.writeData("\003");
                            break;
                        case 2:
                            netWorks.writeData("\013");
                            break;
                        default:
                    }
                });
                builder.setCancelable(true);
                builder.create().show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1 && telnetClient != null && telnetClient
                .isConnected()) {
            netWorks.writeData(data.getStringExtra("ret"));
        } else if (requestCode == 1 && resultCode == 1) {
            MyToast.showShort(MainActivity.this, "链接已经断开，请重新链接");
        }
    }

    private void getInfo() {
        netWorks.getData(new Subscriber<String>() {
            String err = "IP地址不合法";
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                mainText.setText(s);
                if (sendUserNameAndPassword) {
                    mainText02.setColorFilter(Color.GREEN);
                    mainText01.setText("补全");
                    netWorks.writeData(username + "\r" + password + "" + "\r");
                    sendUserNameAndPassword = false;
                }
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT)
                        .show();
                mainText02.setColorFilter(Color.RED);
                mainText01.setText("重连");
                if (err.equals(t.getLocalizedMessage())) {
                    finish();
                }
            }

            @Override
            public void onComplete() {}
        }, CHA_SET_TYPE, ipAndPort);
    }

    Handler handler = new Handler(message -> false);

    private void scrollToBottom(ScrollView scrollView, View view) {
        handler.post(() -> {
            if (scrollView == null || view == null) {
                return;
            }
            int offset = view.getMeasuredHeight() - scrollView.getMeasuredHeight();
            if (offset < 0) {
                offset = 0;
            }
            scrollView.scrollTo(0, offset);
        });
    }
}


