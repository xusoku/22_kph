package com.davis.kangpinhui.model;

import java.io.Serializable;

/**
 * Created by davis on 16/5/18.
 */
public class Recharge implements Serializable {

    public String itgrechargeid;
    public String iuserid;
    public String saccount;
    public String fmoney;
    public String schargenumber;
    public String saddress;
    public String sconsignee;
    public String smobile;
    public String stype;  //0：未付款  3：已付款成功  6：已取消
    public String srechargetype;
    public String daddtime;
    public String dedittime;
    public String sinvoice;
    public String sremark;
    public String spaytype;
    public String slock;
    public String stradeno;

}
