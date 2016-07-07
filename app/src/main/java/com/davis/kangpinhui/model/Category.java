package com.davis.kangpinhui.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by davis on 16/5/18.
 */
public class Category implements Serializable{
    public String id;
    public String name;
    public String picurl;
    public boolean isOnclick=false;
    public ArrayList<Category> clist=new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
