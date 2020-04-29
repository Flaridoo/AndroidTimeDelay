package com.zph.androiddelay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class JsonOperation {

    private MainActivity mact;
    private  String mSPTag = "info";

    JsonOperation(MainActivity activity) {

        mact = activity;
    }


    void dataSave() {

        List<String> orders = new ArrayList<>();
        orders.add("sg123");
        orders.add("sg465");
        orders.add("sg815");

        SharedPreferences sp = mact.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        JSONObject data_total = new JSONObject();
        JSONObject data_orders = new JSONObject();
        for(int i = 0; i < orders.size(); i++) {

            JSONObject data_order = new JSONObject();
            try {
                data_order.put("packageName", "com.pp.cc");
                data_order.put("productId", "ios_33");
                data_order.put("token", "DFSADFFGH");
                data_orders.put(orders.get(i), data_order);
                data_total.put(mSPTag, data_orders);
            } catch (Exception e) {
                mact.log_d("dataSave(): " + e.toString());
                return;
            }
        }
        mact.log_d("dataSave(): json: " + data_total.toString());
        editor.putString(mSPTag, data_total.toString()).apply();
    }

    void dataGet() {

        SharedPreferences sp = mact.getPreferences(Activity.MODE_PRIVATE);
        String _data = sp.getString(mSPTag, "no");
        mact.log_d("dataGet(): " + _data);

        try {
            JSONObject data_orders = new JSONObject(_data);
            JSONObject data_in = data_orders.getJSONObject(mSPTag);
            mact.log_d(data_in.toString());

            Iterator<String> iterator = data_in.keys();
            mact.log_d("data_in.length(): " + data_in.length());

            for(int i = 0; i < data_in.names().length(); i++) {
                Object name = data_in.names().get(i);
                mact.log_d("name: " + name.toString());
                mact.log_d("name: " + data_in.getString(name.toString()));
            }

            data_in.names().length();
        } catch (Exception e) {
            mact.log_e("dataGet(): " + e.toString());
            return;
        }
    }

    void dataRemove() {

        SharedPreferences sp = mact.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String _data = sp.getString(mSPTag, "no");
        mact.log_d("dataRemove(): " + _data);

        JSONObject data_jsn;
        try {
            data_jsn = new JSONObject(_data);
            JSONObject orders = data_jsn.getJSONObject(mSPTag);

            for(int i = 0; i < orders.names().length();) {

                String name = orders.names().get(i).toString();
                mact.log_d("name: " + name);

                if(name.equals("sg815")) {
                    mact.log_d("Remove: sg815");
                    orders.remove("sg815");

                }else if(name.equals("sg123")) {
                    mact.log_d("Remove: sg123");
                    orders.remove("sg123");

                }else if(name.equals("sg465")) {
                    mact.log_d("Remove: sg465");
                    orders.remove("sg465");
                } else {
                    i++;
                }
            }
            data_jsn.put(mSPTag, orders);
        } catch (Exception e) {
            mact.log_e("dataRemove(): " + e.toString());
            return;
        }

        mact.log_d("final data: " + data_jsn.toString());
        editor.putString(mSPTag, data_jsn.toString()).apply();
    }

    void dataPut() {
        SharedPreferences sp = mact.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String _data = sp.getString(mSPTag, "no");
        mact.log_d("dataPut(): " + _data);

        JSONObject data_jsn;
        JSONObject _orders = new JSONObject();
        JSONObject _order = new JSONObject();
        try {
            data_jsn = new JSONObject(_data);

            JSONObject temp1 = data_jsn.getJSONObject(mSPTag);
            for(int i = 0; i < temp1.names().length(); i++) {
                Object name = temp1.names().get(i);
                mact.log_d(name.toString() + ": " + temp1.getString(name.toString()));
                JSONObject temp2 = temp1.getJSONObject(name.toString());

                _order.put("packageName", temp2.getString("packageName"));
                _order.put("productId", temp2.getString("productId"));
                _order.put("token", temp2.getString("token"));
                _orders.put(name.toString(), _order);
            }

            _order.put("packageName", "newName");
            _order.put("productId", "newID");
            _order.put("token", "newToken");
            _orders.put("sg911", _order);

            data_jsn.put(mSPTag, _orders);
        } catch (Exception e) {
            mact.log_e("dataPut(): " + e.toString());
            return;
        }

        mact.log_d("final data: " + data_jsn.toString());
        editor.putString(mSPTag, data_jsn.toString()).apply();
    }


}
