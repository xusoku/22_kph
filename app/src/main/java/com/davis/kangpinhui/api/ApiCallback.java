package com.davis.kangpinhui.api;


import android.util.Log;

import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.util.ToastUitl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by davis on 16/5/17.
 */
public abstract  class ApiCallback<T> implements Callback<T> {

    public abstract void onSucssce(T t);
    public abstract void onFailure();

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.code()==200){
            BaseModel t= (BaseModel) response.body();
            if(t.breturn){
                onSucssce(response.body());
            }else{
                onFailure();
                ToastUitl.showToast(""+t.errorinfo);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure();
        Log.e("aaa", t.getMessage().toString());
        ToastUitl.showToast("网络异常");
    }
    //    @Override
//    public void onResponse(Response<T> response, Retrofit retrofit) {
//        if(response.code()==200){
//            BaseModel t= (BaseModel) response.body();
//            if(t.breturn){
//                onSucssce(response.body());
//            }else{
//                ToastUitl.showToast(t.errorinfo);
//            }
//
//        }
//    }
//
//    @Override
//    public void onFailure(Throwable t) {
//        onFailure();
//
//        ToastUitl.showToast("网络异常");
//    }
}
