package com.davis.kangpinhui.model;

import java.io.Serializable;

/**
 * Created by davis on 16/5/18.
 */
public class Topic<T> implements Serializable {

    public String id;
    public String title;
    public String picurl;
    public T list;
}
