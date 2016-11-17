package com.example.wenhuikuang.resturantcheckin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenhuikuang on 11/16/16.
 */

public class restaurantAdapter extends BaseAdapter {

    private Context mContext;
    private List<restaurant> mRestaruant;

    public restaurantAdapter(Context mContext, List<restaurant> mRestaruant)
    {
        this.mContext = mContext;
        this.mRestaruant = mRestaruant;
    }

    @Override
    public int getCount(){
        return mRestaruant.size();

    }

    @Override
    public Object getItem(int position) {
        return mRestaruant.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        LayoutInflater layoutInflater =(LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.row_layout,parent,false);
        TextView tx_name = (TextView)row.findViewById(R.id.tx_name);

        tx_name.setText(mRestaruant.get(position).getName());

        return row;
//        View row;
//        row = convertView;
//        RestaurantHolder restaurantHolder;
//        if (row == null)
//        {
//            LayoutInflater layoutInflater =(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            row = layoutInflater.inflate(R.layout.row_layout,parent,false);
//            restaurantHolder = new RestaurantHolder();
//            restaurantHolder.tx_name = (TextView)row.findViewById(R.id.tx_name);
//        }
//        else{
//            restaurantHolder = (RestaurantHolder)row.getTag();
//        }
//        restaurant rest = (restaurant)this.getItem(position);
//        restaurantHolder.tx_name.setText(rest.getName());
//        return row;
    }
    static class RestaurantHolder{
        TextView tx_name;

    }
}
