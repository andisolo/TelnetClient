package com.example.lenovo.telnetclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.telnetclient.utlis.MyBaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lenovo
 */
public class LoginActivity extends MyBaseActivity {

    @BindView(R.id.login_ip)
    EditText loginIp;
    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_checkbox)
    CheckBox loginCheckbox;
    @BindView(R.id.login_login_btn)
    TextView loginLoginBtn;
    private File userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userInfo = getFileStreamPath("user_info");
        String userInfo = readUserInfo();
        if (userInfo != null) {
            String[] split = userInfo.split("\\|\\|\\|");
            if (split[0] != null) {
                loginUsername.setText(split[0]);
            }
            if (split.length > 1 && split[1] != null) {
                loginPassword.setText(split[1]);
            }
        }
    }

    private String readUserInfo() {
        if (userInfo != null) {
            StringBuilder builder = new StringBuilder();
            try {
                FileInputStream inputStream = new FileInputStream(userInfo);
                byte[] bytes = new byte[1024];
                int retData;
                while ((retData = inputStream.read(bytes)) != -1) {
                    builder.append(new String(bytes, 0, retData, "UTF-8"));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
        return null;

    }

    private void saveUserInfo() {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(userInfo);
            fileOutputStream.write((loginUsername.getText().toString() + "|||" + loginPassword
                    .getText().toString()).getBytes("UTF-8"));
            fileOutputStream.flush();
            Toast.makeText(this, "用户名密码已经保存", Toast.LENGTH_SHORT).show();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.login_login_btn)
    public void onViewClicked() {
        if (loginCheckbox.isChecked()) {
            saveUserInfo();
        } else {
            if (userInfo.isFile()) {
                userInfo.delete();
                Toast.makeText(this, "用户名密码已经删除", Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ipAndPort", loginIp.getText().toString());
        bundle.putString("username", loginUsername.getText().toString());
        bundle.putString("password", loginPassword.getText().toString());
        bundle.putBoolean("check",loginCheckbox.isChecked());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
