package com.davis.kangpinhui.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by davis on 16/5/20.
 */
@DatabaseTable(tableName = "searchhistroy")
public class SearchHistroy {
    @DatabaseField(id = true)
    private Long id;
    /**
     * id
     */
    @DatabaseField
    private String key;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
