package com.jackger.demo.sbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> arrayUser;

    public UserAdapter() {
    }

    public UserAdapter(Context context, ArrayList<User> arrayUser) {

        this.context = context;
        this.arrayUser = arrayUser;
    }

    @Override
    public int getCount() {
        return arrayUser.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtUsername, txtAddress;
        LinearLayout linearLayoutUser;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_user, null);

            holder.imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);
            holder.txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.linearLayoutUser = (LinearLayout) convertView.findViewById(R.id.linearLayoutUser);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final User user = arrayUser.get(position);

        holder.txtUsername.setText(user.getUsername());
        holder.txtAddress.setText(user.getAddress());
        Picasso.get().load(user.getLinkavatar()).into(holder.imgAvatar);

        //bắt sự kiện click linnearLayout
        holder.linearLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm PickLinearLayoutUser từ SearchActivity
                    ((SearchActivity) context).PickLinearLayoutUser(user);

                } catch (Exception e) {


                }

            }
        });

        return convertView;
    }
}
