package com.example.lenovo.telnetclient.networks;

import android.support.test.espresso.core.internal.deps.guava.util.concurrent.ThreadFactoryBuilder;
import android.util.Log;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 2017/11/10.
 *
 * @author GuoJiaming
 */

public class MyNetWorks {
    private static final String REGEX_NEW =
            "\\u001B\\[\\dA\\u001B\\[\\d+C\\s*\\u001B\\[\\dA\\u001B" +
                    "\\[\\d+C";
    private static final String REGEX = "\\u0007?\\u001B?\\[(\\d\\d?)[D]";
    private static final String IP_CHECK = "\\d\\d?\\d?\\.\\d\\d?\\d?\\.\\d\\d?\\d?\\.\\d\\d?\\d?";
    private static final String TAG = "my_network";
    private static TelnetClient telnetClient;
    private static ExecutorService singleThreadPool;


    public static TelnetClient getTelnetClient() {
        if (telnetClient == null) {
            telnetClient = new TelnetClient();
            TerminalTypeOptionHandler optionHandler = new TerminalTypeOptionHandler("VT100",
                    false, false, true, true);
            EchoOptionHandler echoOptionHandler = new EchoOptionHandler(true, false, true, false);
            SuppressGAOptionHandler suppressGAOptionHandler = new SuppressGAOptionHandler(true,
                    true, true, true);
            try {
                telnetClient.addOptionHandler(optionHandler);
                telnetClient.addOptionHandler(echoOptionHandler);
                telnetClient.addOptionHandler(suppressGAOptionHandler);
            } catch (InvalidTelnetOptionException e) {
                Log.i(TAG, "getTelnetClient: " + e.getLocalizedMessage());
            } catch (IOException e) {
                Log.i(TAG, "getTelnetClient: " + e.getLocalizedMessage());
            }
            telnetClient.registerNotifHandler(new NetWorks());
        }
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();
        singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        return telnetClient;

    }
    public void getData(Subscriber<String> subscriber, String chaSetName, String... ipAndPort) {
        Flowable<String> flowAble = Flowable.create(e -> {
            String ip = null;
            int port = 0;
            switch (ipAndPort.length) {
                case 0:
                    throw new NullPointerException("IP地址不能为空");
                case 1:
                    Pattern pattern = Pattern.compile(IP_CHECK);
                    Matcher matcher = pattern.matcher(ipAndPort[0]);
                    if (matcher.find()) {
                        ip = ipAndPort[0];
                        port = 23;
                    } else {
                        throw new NullPointerException("IP地址不合法");
                    }
                    break;
                case 2:
                    ip = ipAndPort[0];
                    port = Integer.valueOf(ipAndPort[1]);
                    break;
                default:
            }
            if (telnetClient != null) {
                telnetClient.connect(ip, port);
            } else {
                throw new NullPointerException("TelnetClient未初始化");
            }
            InputStream inputStream = telnetClient.getInputStream();
            int redData;
            byte[] buff = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while (!e.isCancelled() && (redData = inputStream.read(buff)) != -1 &&
                    telnetClient.isConnected()) {
                stringBuilder.append(new String(buff, 0, redData, chaSetName));
                while (e.requested() == 0) {
                    if (e.isCancelled()) {
                        break;
                    }
                }
                stringBuilder = stringFilter(stringBuilder);
                if (stringBuilder.length() < 5000) {
                    e.onNext(stringBuilder.toString());
                } else {
                    e.onNext(stringBuilder.delete(0, stringBuilder.length() - 5000).toString());
                }
            }
            inputStream.close();
            telnetClient.disconnect();
            singleThreadPool.shutdown();
            throw new RuntimeException("telnet 链接已经断开");
        }, BackpressureStrategy.DROP);
        bindObserver(flowAble, subscriber);
    }

    private <T> void bindObserver(Flowable<T> flowAble, Subscriber<T> subscriber) {
        flowAble.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void writeData(String data) {
        Runnable runnable = () -> {
            try {
                telnetClient.getOutputStream().write(data.getBytes("ISO-8859-1"));
                telnetClient.getOutputStream().flush();
            } catch (IOException e) {
                Log.i(TAG, "writeData: "+e.getLocalizedMessage());
            }
        };
        singleThreadPool.execute(runnable);
    }
    public void writeData(byte data){
        Runnable runnable = () -> {
            try {
                telnetClient.getOutputStream().write(data);
                telnetClient.getOutputStream().flush();
            } catch (IOException e) {
                Log.i(TAG, "writeData: "+e.getLocalizedMessage());
            }
        };
        singleThreadPool.execute(runnable);
    }

    private StringBuilder stringFilter(StringBuilder stringBuilder) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(stringBuilder.toString());
        while (matcher.find()) {
            if ((Integer.valueOf(matcher.group(1)) == 39)) {
                String tag = "---- 任意键继续显示 ( 'Q' 键退出 ) ----";
                if (stringBuilder.indexOf(tag) != -1) {
                    stringBuilder.delete(matcher.start() - tag.length(), matcher.end());
                    matcher = pattern.matcher(stringBuilder.toString());
                } else {
                    stringBuilder.delete((matcher.start() - 39), matcher.end());
                    matcher = pattern.matcher(stringBuilder.toString());
                }
            } else {
                stringBuilder.delete(matcher.start() - Integer.valueOf(matcher.group(1)),
                        matcher.end());
                matcher = pattern.matcher(stringBuilder.toString());
            }
        }
        String regexZte = "\b \b";
        while (stringBuilder.indexOf(regexZte) != -1) {
            stringBuilder.delete(stringBuilder.indexOf(regexZte) - 1, stringBuilder.indexOf
                    (regexZte) +
                    regexZte.length());
        }

        pattern = Pattern.compile(REGEX_NEW);
        matcher = pattern.matcher(stringBuilder.toString());
        while (matcher.find()) {
            stringBuilder.delete(matcher.start(), matcher.end());
        }
        return stringBuilder;
    }

}
