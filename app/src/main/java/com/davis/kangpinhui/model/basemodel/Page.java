package com.davis.kangpinhui.model.basemodel;

/**
 * Created by davis on 16/5/18.
 */
public class Page<T> {

    public int iTotalRecords;
    public int iPageSize;
    public int iCurrentPage;
    public int iTotalPage;
    public String className;
    public T list;

}
