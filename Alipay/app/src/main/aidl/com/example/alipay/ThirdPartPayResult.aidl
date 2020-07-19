// ThirdPartPayResult.aidl
package com.example.alipay;


interface ThirdPartPayResult {

    void paySuccess();

    void payFailed(in int errorCode,String msg);
}
