package com.jackger.demo.sbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AddressAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Address> arrayAddress;

    public AddressAdapter(Context context, ArrayList<Address> arrayAddress) {
        this.context = context;
        this.arrayAddress = arrayAddress;
    }

    @Override
    public int getCount() {
        return arrayAddress.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayAddress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView txtAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_address, null);

            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        Address address = arrayAddress.get(position);
        holder.txtAddress.setText(address.getAddressName());

        return convertView;
    }
}
