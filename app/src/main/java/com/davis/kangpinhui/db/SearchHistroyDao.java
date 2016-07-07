package com.davis.kangpinhui.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davis on 16/5/20.
 */
public class SearchHistroyDao {


    private Context context;
    private Dao<SearchHistroy, Integer> keyDaoOpe;
    private DatabaseHelper helper;

    public SearchHistroyDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
            keyDaoOpe = helper.getDao(SearchHistroy.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 增加一个用户
     *
     * @param key
     */
    public void add(SearchHistroy key) {
        try {
            keyDaoOpe.createIfNotExists(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新用户信息
     *
     * @param key
     */
    public void update(SearchHistroy key) {
        try {
            keyDaoOpe.update(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单用户用户信息
     * @return
     */
    public SearchHistroy getSinglekey() {
        try {
            return keyDaoOpe.queryForId(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除某个用户
     * @param key
     */
    public void remove(SearchHistroy key) {
        try {
            keyDaoOpe.delete(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除某个用户
     */
    public void removeall() {
        try {
            keyDaoOpe.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<SearchHistroy> getKeyList() {
        try {
            return keyDaoOpe.queryBuilder().orderBy("id", false).limit(6).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<SearchHistroy>();
    }
}
