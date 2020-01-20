package com.zph.androiddelay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    JsonOperation jsonOperation;
    String TAG = "zph";
    Handler mHandler;
    int num = 15;

    TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonOperation = new  JsonOperation(this);
        mTime = findViewById(R.id.textView1);

        setBtns();

        mHandler = new Handler();
        //delays();
    }

    void setBtns() {

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //VerifyResultForServer();

                jsonOperation.dataSave();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                jsonOperation.dataGet();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                jsonOperation.dataRemove();
            }
        });
    }

    void delays() {
        Runnable r = new Runnable() {

            @Override
            public void run() {

                //do something
                Log.e(TAG, "run: " + num--);
                mTime.setText("00:00:" + String.format("%02d", num));
                openMusic();

                if(num > 0) {
                    //每隔1s循环执行run方法
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        mHandler.postDelayed(r, 100);//延时100毫秒
    }


    public  void  openMusic(){
        RingtoneManager rm=new RingtoneManager(getApplicationContext());//初始化 系统声音
        Uri uri = rm.getDefaultUri(rm.TITLE_COLUMN_INDEX);//获取系统声音路径
        Ringtone mRingtone = rm.getRingtone(getApplicationContext(), uri);//通过Uri 来获取提示音的实例对象
        mRingtone.play();//播放:
    }

    public void setVibrators() {

//        //设置震动
//        Vibrator vibrator = getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);
//        vibrator.vibrate(500);//震动时长 ms
    }




    private void VerifyResultForServer() {

        class VerifyResultForServerHandler extends Handler {
            @Override
            public void handleMessage(Message msgs) {
                if (msgs.arg1 != 2) {
                    return;
                }

                String result = (String) msgs.obj;
                log_d("VerifyResultForServer z005: " + result);

                String status;
                JSONObject jo;
                try {
                    jo = new JSONObject(result);
                    status = jo.getString("status");
                    log_d("VerifyResultForServer z006: " + result);
                } catch (Exception e) {
                    log_e(e.toString());
                    return;
                }

                if (status.equals("0")) {
                    SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("zph", "no").apply();
                } else {
                    log_e("VerifyResultForServerHandler: status: " + status);
                }

            }
        }

        VerifyResultForServerHandler _handler = new VerifyResultForServerHandler();
        toVerifyResultForServer(_handler);
    }
    private void toVerifyResultForServer(final Handler handler) {

        //final String mCheckUrl = "http://112.124.201.87:10003/v1/sdk/check/googlecheck";
        final String mCheckUrl = "http://www.appsflyer.com?apps=\"aap\"";
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String result;
                try {
                    loading(true);
                    showProgressDialog("start...");

                    result = Util.HttpPosts(mCheckUrl, "zph_test");

                    loading(false);
                    cancelProgressDialog();

                    msg.obj = result;
                    msg.arg1 = 2;
                    log_d("verifyToServer00: " + result);
                } catch (Exception e) {
                    log_e("verifyToServer01: " + e.toString());

                    loading(false);
                    cancelProgressDialog();
                    //TODO
                    mHandler.postDelayed(this, 1000);//延时100毫秒

                    return;
                }
                handler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
    }


    //加载框
    public void showProgressDialog(String msg) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    //取消加载框
    public void cancelProgressDialog() {

        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }



    void log_d(String msg) {
        Log.d("zph", "log--> " + msg);
    }
    void log_e(String msg) {
        Log.e("zph", "log--> " + msg);
    }
    void loading(boolean isLoading) {
        log_e(isLoading + " ");
    }
}
