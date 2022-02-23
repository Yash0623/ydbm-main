package com.ydbm.models;

import java.util.ArrayList;

public class InventoryRowMain {
    String ds_id;
    String ds_name;

    ArrayList<InventoryRow> list;

    public String getDs_id() {
        return ds_id;
    }

    public void setDs_id(String ds_id) {
        this.ds_id = ds_id;
    }

    public String getDs_name() {
        return ds_name;
    }

    public void setDs_name(String ds_name) {
        this.ds_name = ds_name;
    }

    public ArrayList<InventoryRow> getList() {
        return list;
    }

    public void setList(ArrayList<InventoryRow> list) {
        this.list = list;
    }
}
