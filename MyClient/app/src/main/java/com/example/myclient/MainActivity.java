package com.example.myclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alipay.ThirdPartPayAction;
import com.example.alipay.ThirdPartPayResult;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView payCount;
    private Button payButton;
    private AliServiceConnection connection;
    private ThirdPartPayAction thirdPartPayAction;
    private boolean isBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定服务器，现实中就是绑定阿里服务器
        bindAliService();

        initView();
        setListener();
    }

    /*绑定阿里服务器*/
    private void bindAliService() {
        //因为是绑定项目外的服务器，所以使用隐式绑定
        Intent intent = new Intent();
        intent.setAction("com.example.alipay.THIRD_PART_PAY_ACTION");
        intent.setPackage("com.example.alipay");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        connection = new AliServiceConnection();
        isBind = bindService(intent, connection, BIND_AUTO_CREATE);
    }


    private class AliServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected --> "+name);
            //连接成功，获取实体类
            thirdPartPayAction = ThirdPartPayAction.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //连接失败
            Log.d(TAG, "onServiceDisconnected --> "+name);
        }
    }

    /*按钮点击事件，进行充值*/
    private void setListener() {
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行充值
                if (thirdPartPayAction != null) {
                    try {
                        thirdPartPayAction.requestPay("黄焖鸡米饭",100.00f,new CallBack());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private class CallBack extends ThirdPartPayResult.Stub{

        @Override
        public void paySuccess() throws RemoteException {
            Log.d(TAG, "paySuccess...");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    payCount.setText("100");
                    Toast.makeText(MainActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void payFailed(int errorCode, String msg) throws RemoteException {
            Log.d(TAG, "payFailed...");
            Toast.makeText(MainActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
        }

    }

    /*界面初始化*/
    private void initView() {
        payCount = findViewById(R.id.pay_count_tv);
        payButton = findViewById(R.id.pay_btn);

    }

    /*解绑服务*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind && connection != null) {
            Log.d(TAG, "unbind service...");
            unbindService(connection);
            isBind = false;
            connection = null;
        }
    }
}
