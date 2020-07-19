package com.example.alipay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class payActivity extends AppCompatActivity {

    private static final String TAG = "payActivity";
    private boolean isBind;
    private PayService.payAction payAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        doBindService();
        initView();

    }

    /*
    * 初始化界面
    * */
    private void initView() {
        Intent intent = getIntent();
        String orderInfo = intent.getStringExtra(Constants.KEY_BILL_INFO);
        final float payMoney = intent.getFloatExtra(Constants.KEY_PAY_MONEY,0);
        TextView orderInfoText = findViewById(R.id.orderInfo);
        Log.d(TAG, "initView -- orderInfo --> "+orderInfo);
        orderInfoText.setText("账单："+orderInfo);
        TextView payMoneyText = findViewById(R.id.payMoney);
        payMoneyText.setText("金额："+String.valueOf(payMoney)+"元");
        final EditText password = findViewById(R.id.password);
        Button payButton = findViewById(R.id.pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交金额
                String passwordText = password.getText().toString().trim();
                if ("123456".equals(passwordText) && payAction != null) {
                    payAction.pay(payMoney);
                    Toast.makeText(payActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(TAG, "支付完成");
                }else{
                    Toast.makeText(payActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    * 点击返回键，用户取消支付
    * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        payAction.userCancelPay();
    }

    /*
     * 绑定服务
     * */
    private void doBindService() {
        Intent intent = new Intent(this, PayService.class);
        isBind = bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取binder的实现类，支付类
            payAction = (PayService.payAction) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            payAction = null;
        }
    };

    /*解绑*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind && mConnection != null) {
            isBind = false;
            unbindService(mConnection);
            mConnection = null;
        }
    }
}
