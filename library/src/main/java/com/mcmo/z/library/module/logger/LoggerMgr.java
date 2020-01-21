package com.mcmo.z.library.module.logger;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerMgr {
    private static final String TAG = "LoggerMgr$9087";
    private volatile static LoggerMgr instance;
    private volatile boolean isRunning;
    private StringBuffer mStrBuffer;
    private ExecutorService mFixPool;
    private final int THREAD_NUM = 2;
    private StringBuffer[] strBufferArray = new StringBuffer[2];
    private int mBufferIndex = 0;
    private int mSaveIndex = 1;
    private boolean isWriting;

    private final long THRESHOLD_SAVE_TO_FILE = 100 * 1000;
    private static final String mLogFolderName = "logs";
    private String mLogFileName = "log";
    private String mLogPath = "";

    private LoggerMgr() {
        mFixPool = Executors.newFixedThreadPool(THREAD_NUM);
        for (int i = 0; i < strBufferArray.length; i++) {
            strBufferArray[i] = new StringBuffer();
        }
    }

    public static LoggerMgr getInstance() {
        if (instance == null) {
            synchronized (LoggerMgr.class) {
                if (instance == null) {
                    instance = new LoggerMgr();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mLogPath = getLogPath(context);
        Log.e(TAG, "init: " + mLogPath);
        reset();
    }

    private static String getLogPath(Context context) {
        return context.getApplicationContext().getCacheDir().getAbsolutePath() + File.separator + mLogFolderName;
    }

    public synchronized void start() {
        if (TextUtils.isEmpty(mLogPath)) {
            throw new IllegalStateException("请先调用init方法");
        }
        if (isRunning) {
            Log.e(TAG, "start: 日志记录已经开启");
            return;
        }
        mFixPool.execute(new Runnable() {
            @Override
            public void run() {
                startLogProcess();
            }
        });
    }

    private void reset() {
        mBufferIndex = 0;
        mSaveIndex = 1;
        for (int i = 0; i < strBufferArray.length; i++) {
            strBufferArray[i].delete(0, strBufferArray[i].length());//清空缓存
        }
    }

    private void destroy() {
        isRunning = false;
        if (!isWriting) {
            switchIndex();
            startSaveLog();
        }
    }

    private void switchIndex() {
        int tmp = mBufferIndex;
        mBufferIndex = mSaveIndex;
        mSaveIndex = tmp;
    }

    private void startLogProcess() {
        isRunning = true;
        try {
            Log.e(TAG, "startLogProcess: 开始获取日志");
//            Runtime.getRuntime().exec("logcat -C");
            Process process = Runtime.getRuntime().exec("logcat -v time");
            InputStream is = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String log;
            Log.e(TAG, "startLogProcess: 开始获取.......");
            while ((log = br.readLine()) != null && isRunning) {
                StringBuffer sb = strBufferArray[mBufferIndex];
                if (!log.contains(TAG)) {// TODO: 2019/12/2 测试完成后去掉这个判断
                    sb.append(log);
                    sb.append("\n");
                    if (sb.length() > THRESHOLD_SAVE_TO_FILE) {
                        Log.e(TAG, "startLogProcess: 开始写入:" + sb.length());
                        switchIndex();
                        startSaveLog();
                    }
                }
            }
            br.close();
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSaveLog() {
        isWriting = true;
        mFixPool.execute(new Runnable() {
            @Override
            public void run() {
                saveLogToFile();
            }
        });
    }

    private void saveLogToFile() {
        try {
            String log = strBufferArray[mSaveIndex].toString();
            if (TextUtils.isEmpty(log)) {
                isWriting = false;
                return;
            }
            File folder = new File(mLogPath);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
            File file = new File(folder, mLogFileName);
            boolean fileExist = file.exists() && file.isFile();
            FileWriter writer = new FileWriter(file, fileExist);
            writer.write(log);
            if (fileExist) {
                Log.e(TAG, "saveLogToFile: 追加写入");
            } else {
                Log.e(TAG, "saveLogToFile: 新建写入");
            }
            writer.flush();
            writer.close();
            strBufferArray[mSaveIndex].delete(0, strBufferArray[mSaveIndex].length());
            Log.e(TAG, "saveLogToFile: 写入完成" + strBufferArray[mSaveIndex].length());
            isWriting = false;
            if (!isRunning) {//如果停止运行了就把领一个buffer也保存一下
                saveOtherBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOtherBuffer() {
        try {
            String log = strBufferArray[mBufferIndex].toString();
            if (TextUtils.isEmpty(log)) {
                return;
            }
            File folder = new File(mLogPath);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
            File file = new File(folder, mLogFileName);
            boolean fileExist = file.exists() && file.isFile();
            FileWriter writer = new FileWriter(file, fileExist);
            writer.write(log);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteLog(Context context) {
        String path = getLogPath(context);
        final File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] files = folder.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (files[i] != null)
                            files[i].delete();
                    }
                }
            }).start();
        }
    }
}
