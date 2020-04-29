package com.zph.androiddelay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

class HandlerThreads {

    MainActivity m_act;
    private String mSPTag = "unity";
    private int sendTime;


    HandlerThreads(MainActivity mainActivity) {
        m_act = mainActivity;
    }



    void VerifyResultForServer(String _packageName, String _productId, String _token,final String _orderId) {

        class VerifyResultForServerHandler extends Handler {
            @Override
            public void handleMessage(Message msgs) {
                if (msgs.arg1 == 1) {
                    //m_act.showAlert("Oops!", "Network error when getting reward!", "GetAgain", (DialogInterface dialogInterface, int i) -> toVerifyResultForServer(handler, _packageName, _productId, _token));

                    m_act.toasts("Server Error, Please Try To ReLogin");
                    return;
                } else if(msgs.arg1 != 2) {
                    return;
                }

                String result = (String) msgs.obj;
                m_act.log_d("VerifyResultForServer z005: " + result);

                String status;
                JSONObject jo;
                try {
                    jo = new JSONObject(result);
                    status = jo.getString("status");
                    m_act.log_d("VerifyResultForServer z006: " + result);
                } catch (Exception e) {
                    m_act.log_e(e.toString());
                    m_act.toasts("http_failed: " + e.toString());
                    return;
                }

                if (status.equals("0")) {

                    SharedPreferences sp = m_act.getPreferences(Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    String _data = sp.getString(mSPTag, "no");
                    if(_data.equals("no")) {
                        return;
                    }

                    JSONObject data_jsn;
                    try {
                        data_jsn = new JSONObject(_data);
                        JSONObject orders = data_jsn.getJSONObject("data");
                        for(int i = 0; i < orders.names().length(); i++) {
                            String orderId = orders.names().get(i).toString();
                            if(orderId.equals(_orderId)) {
                                orders.remove(orderId);
                                break;
                            }
                        }
                        if(orders.toString().equals("{}")) {

                            editor.putString(mSPTag, "no").apply();
                        } else {

                            data_jsn.put("data", orders);
                            editor.putString(mSPTag, data_jsn.toString()).apply();
                        }
                    } catch (Exception e) {
                        m_act.log_d("remove order: " + e.toString());
                    }
                } else {
                    m_act.log_e("VerifyResultForServerHandler: status: " + status);
                    m_act.toasts("Server Error：" + status);
                }
            }
        }

        sendTime = 1;
        VerifyResultForServerHandler _handler = new VerifyResultForServerHandler();
        toVerifyResultForServer(_handler, _packageName, _productId, _token);
    }

    private void toVerifyResultForServer(final Handler handler, final String _packageName, final String _productId, final String _token) {

        final String mCheckUrl = "http://112.124.201.87:10003/v1/sdk/check/googlecheck";

//        String param = OrderInfoUtil2_0.buildServerVerifyParam(m_act.mAppid, m_act.mChannelid, _packageName, _productId, _token, true);
//        String param2 = OrderInfoUtil2_0.buildServerVerifyParam(m_act.mAppid, m_act.mChannelid, _packageName, _productId, _token, false);
//        m_act.log_d("VerifyResultForServer: param2: " + param2);
//        String sign = MD5.getMessageDigest((param2 + m_paykey).getBytes());
//        String the_param = param + "&sign=" + sign;
//        m_act.log_d("VerifyResultForServer: param3: " + the_param);

        final String the_param = "zphtest";
        m_act.loading(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                String result;
                try {
                    result = Util.HttpPosts(mCheckUrl, the_param);
                    msg.obj = result;
                    msg.arg1 = 2;
                    m_act.log_d("verifyToServer01: " + result);
                    m_act.loading(false);

                    handler.sendMessage(msg);
                } catch (Exception e) {
                    m_act.log_e("verifyToServer02: " + e.toString());
                    m_act.loading(false);

                    if (sendTime < 3) {
                        sendTime++;
                        try {
                            Thread.sleep(1000);
                        } catch (Exception de) {
                            m_act.log_d(de.toString());
                        }
                        toVerifyResultForServer(handler, _packageName, _productId, _token);
                    } else {
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }
}
