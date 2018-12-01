package com.jackger.demo.sbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Post> arrayPost;

    public PostAdapter(Context context, ArrayList<Post> arrayPost) {
        this.context = context;
        this.arrayPost = arrayPost;
    }

    public PostAdapter() {
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
        TextView txtUsername, txtAddress;
        Button btnContact;
        TextView txtTitleAuthor;
        ImageView imgBook;
        ImageView icHeart, icComment, icShare, icMenu;
        TextView txtHeart, txtComment, txtShare;
        TextView txtDescription, txtPrice;
        TextView txtState;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_post, null);

            holder.txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.txtTitleAuthor = (TextView) convertView.findViewById(R.id.txtTitleAuthor);
            holder.txtHeart = (TextView) convertView.findViewById(R.id.txtHeart);
            holder.txtComment = (TextView) convertView.findViewById(R.id.txtComment);
            holder.txtShare = (TextView) convertView.findViewById(R.id.txtShare);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            holder.txtState = (TextView) convertView.findViewById(R.id.txtState);
            holder.btnContact = (Button) convertView.findViewById(R.id.btnContact);
            holder.imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);
            holder.imgBook = (ImageView) convertView.findViewById(R.id.imgBook);
            holder.icHeart = (ImageView) convertView.findViewById(R.id.icHeart);
            holder.icComment = (ImageView) convertView.findViewById(R.id.icComment);
            holder.icShare = (ImageView) convertView.findViewById(R.id.icShare);
            holder.icMenu = (ImageView) convertView.findViewById(R.id.icMenu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final Post post = arrayPost.get(position);

        holder.txtUsername.setText(post.getUser().getUsername());
        holder.txtAddress.setText(post.getUser().getAddress());
        holder.txtTitleAuthor.setText(post.getBook().getTitle() + " - " + post.getBook().getAuthor());
        holder.txtComment.setText("6,969");
        holder.txtHeart.setText("6,969");
        holder.txtShare.setText("6,969");
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

        //Bắt sự kiện chọn button Liên hệ
        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm dialog từ màn hình HomeActivity
                    ((HomeActivity) context).DialogLienHe(post.getUser().getPhone());
                } catch (Exception e) {

                }
            }
        });

        //Bắt sự kiện click vào hình sách
        holder.imgBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm PickBookImage từ màn hình HomeActivity
                    ((HomeActivity) context).PickBookImage(post);

                } catch (Exception e) {

                    try {

                        //Gọi hàm PickBookImage từ màn hình HomeActivity
                        ((InfoActivity) context).PickBookImage(post);

                    } catch (Exception e2) {

                    }

                }
            }
        });

        //Bắt sự kiện click ImageAvatar
        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm PickBookImage từ màn hình HomeActivity
                    ((HomeActivity) context).PickUserAvatar(post.getUser());

                } catch (Exception e) {

                }

            }
        });

        //Bắt sự kiện click username
        holder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Gọi hàm PickBookImage từ màn hình HomeActivity
                    ((HomeActivity) context).PickUsername(post.getUser());
                } catch (Exception e) {

                }

            }
        });

        //Bắt sự kiện click menu
        holder.icMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (post.getUser().getEmail().equals(UserMain.getEmail())) {

                        //
                        ((InfoUserMainActivity) context).DialogMenu(post.getBook());
                    }

                } catch (Exception e) {


                }
            }
        });

        return convertView;
    }
}
