package com.example.wenhuikuang.resturantcheckin;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wenhuikuang on 11/18/16.
 */

public class customerAdapter extends BaseAdapter {
    private Context mContext;
    private List<customerInfo> customerInfos;
    LayoutInflater layoutInflater;
    public customerAdapter(Context mContext, List<customerInfo> customerInfos)
    {
        this.mContext = mContext;
        this.customerInfos = customerInfos;
    }
    @Override
    public int getCount() {
        return customerInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return customerInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        layoutInflater =(LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.row_layout_customer,parent,false);
        TextView tx_name = (TextView)row.findViewById(R.id.tx_name);
        TextView tx_size = (TextView)row.findViewById(R.id.tx_size);

        tx_name.setText(customerInfos.get(position).getName());
        tx_size.setText(Integer.toString(customerInfos.get(position).getSize()));

        return row;
    }
}
