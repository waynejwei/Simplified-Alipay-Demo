package com.example.alipay;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

public class PayService extends Service {

    private static final String TAG = "PayService";
    private ThirdPartPayImpl thirdPartPay;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onBind -- action ---> "+action);
        if (action != null && "com.example.alipay.THIRD_PART_PAY_ACTION".equals(action)) {
            //第三方模拟支付宝支付
            thirdPartPay = new ThirdPartPayImpl();
            return thirdPartPay;
        }
        return new payAction();
    }


    /*
     * 实现ThirdPartPayAction接口
     * */
    private class ThirdPartPayImpl extends ThirdPartPayAction.Stub {

        private ThirdPartPayResult mCallBack;

        @Override
        public void requestPay(String orderInfo, float payMoney, ThirdPartPayResult callBack) throws RemoteException {

            mCallBack = callBack;
            //第三方发起请求，打开支付界面
            Intent intent = new Intent();
            intent.setClass(PayService.this, payActivity.class);
            intent.putExtra(Constants.KEY_BILL_INFO,orderInfo);
            intent.putExtra(Constants.KEY_PAY_MONEY,payMoney);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//新任务
            startActivity(intent);
        }

        public void paySuccess(){
            if (mCallBack != null) {
                try {
                    mCallBack.paySuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void payFailed(int errorCode,String msg){
            if (mCallBack != null) {
                try {
                    mCallBack.payFailed(errorCode,msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    * 支付动作
    * */
    public class payAction extends Binder{

        public void pay(float money){
            Log.d(TAG, "pay -- money -->"+money);
            //支付的方法
            if (thirdPartPay != null) {
                thirdPartPay.paySuccess();
            }
        }

        public void userCancelPay(){
            Log.d(TAG, "userCancelPay...");
            //用户点击取消
            if (thirdPartPay != null) {
                thirdPartPay.payFailed(1,"用户点击取消支付");
            }
        }
    }
}
