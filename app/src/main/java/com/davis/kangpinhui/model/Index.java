package com.davis.kangpinhui.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by davis on 16/5/18.
 */
public class Index implements Serializable {
    public ArrayList<Banner> bannerList=new ArrayList<>();
    public ArrayList<Productlist> recommandList=new ArrayList<Productlist>();

    public static class Productlist extends  Banner{

        public String id;
        public ArrayList<Product> list=new ArrayList<>();
    }
}
