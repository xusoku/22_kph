package com.davis.kangpinhui.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by davis on 16/5/18.
 */
public class Shop implements Parcelable {

    public String addtime;
    public String edittime;
    public String slock;
    public String id;
    public String shopname;
    public String address;
    public String tel;
    public String picurl;
    public String context;
    public String addressid;
    public String lng;
    public String lat;
    public String hotarea;
    public String center;
    public String polygon;

    protected Shop(Parcel in) {
        addtime = in.readString();
        edittime = in.readString();
        slock = in.readString();
        id = in.readString();
        shopname = in.readString();
        address = in.readString();
        tel = in.readString();
        picurl = in.readString();
        context = in.readString();
        addressid = in.readString();
        lng = in.readString();
        lat = in.readString();
        hotarea = in.readString();
        center = in.readString();
        polygon = in.readString();
    }

    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addtime);
        dest.writeString(edittime);
        dest.writeString(slock);
        dest.writeString(id);
        dest.writeString(shopname);
        dest.writeString(address);
        dest.writeString(tel);
        dest.writeString(picurl);
        dest.writeString(context);
        dest.writeString(addressid);
        dest.writeString(lng);
        dest.writeString(lat);
        dest.writeString(hotarea);
        dest.writeString(center);
        dest.writeString(polygon);
    }
}
