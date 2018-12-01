package com.jackger.demo.sbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostSearchAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Post> arrayPost;

    public PostSearchAdapter(Context context, ArrayList<Post> arrayPost) {
        this.context = context;
        this.arrayPost = arrayPost;
    }

    public PostSearchAdapter() {

    }

    @Override
    public int getCount() {
        return arrayPost.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayPost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtTitleAuthor;
        ImageView imgBook;
        TextView txtDescription, txtPrice;
        TextView txtState;
        LinearLayout bookItem;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_post_search, null);

            holder.txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            holder.txtTitleAuthor = (TextView) convertView.findViewById(R.id.txtTitleAuthor);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            holder.txtState = (TextView) convertView.findViewById(R.id.txtState);
            holder.imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);
            holder.imgBook = (ImageView) convertView.findViewById(R.id.imgBook);
            holder.bookItem = (LinearLayout) convertView.findViewById(R.id.bookItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final Post post = arrayPost.get(position);

        holder.txtUsername.setText(post.getUser().getUsername());
        holder.txtTitleAuthor.setText(post.getBook().getTitle() + " - " + post.getBook().getAuthor());
        holder.txtDescription.setText(post.getBook().getDescription());
        if (Integer.valueOf(post.getBook().getPrice()) == 0) {

            holder.txtPrice.setText("$ FREE $");
        } else {

            holder.txtPrice.setText(post.getBook().getPrice() + " $");
        }
        Picasso.get().load(post.getUser().getLinkavatar()).into(holder.imgAvatar);
        Picasso.get().load(post.getBook().getLinkimage()).into(holder.imgBook);
        if (!post.getBook().isState()) {

            holder.txtState.setVisibility(View.INVISIBLE);
        } else {


            holder.txtState.setVisibility(View.VISIBLE);
        }

        holder.bookItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm PickBookImage từ màn hình Search
                    ((SearchActivity) context).PickBookImage(post);
                } catch (Exception e) {

                }


            }
        });

        return convertView;
    }
}
