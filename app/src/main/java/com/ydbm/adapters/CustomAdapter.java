package com.ydbm.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ydbm.R;
import com.ydbm.ui.CheckableLinearLayout;


import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<String> array) {
        data = array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
       // final String current = data.get(i).getName();

        if (view == null) {
            view = inflater.inflate(R.layout.inquiry_item_row_add_member, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.item_name);
        //    viewHolder.dharamshala_name = view.findViewById(R.id.dharamshala_name);


            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

    /*    viewHolder.name.setText(current);
        if (data.get(i).getDs_name() != null && data.get(i).getDs_name().length()>0) {
            viewHolder.dharamshala_name.setText("("+data.get(i).getDs_name()+")");
        }
        if(data.get(i).getUsertype().equals("1")){
            viewHolder.dharamshala_name.setText("(admin)");
        }
        else if(data.get(i).getUsertype().equals("2")){
            viewHolder.dharamshala_name.setText("(copy message to)");
        }*/

        return view;
    }

    private class ViewHolder {
        CheckableLinearLayout checkableLayout;

        TextView name, dharamshala_name;
    }

}