package com.davis.kangpinhui.model.basemodel;

import java.io.Serializable;

/**
 * Created by davis on 16/5/17.
 */
public class BaseModel<T> implements Serializable{
    public boolean breturn;
    public String errorinfo;
    public String ireturn;
    public T object;

}
