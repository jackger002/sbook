package com.jackger.demo.sbook;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView imgBook, imgAvatar, imgBack;
    private TextView txtUsername, txtAddress;
    private Button btnContact;
    private TextView txtTitleAuthor, txtPrice, txtDescription, txtState, txtMore;

    private Post post;
    private String backActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        //Ánh xạ view
        AnhXaView();

        //Lấy dữ liệu user gửi từ LoginActivity
        final Bundle bundle = getIntent().getExtras();
        post = (Post) bundle.getSerializable("post");
        backActivity = bundle.getString("backActivity");

        //Gán giá trị vào view
        SetView(post);

        //Bắt sự kiện click chọn button Liên hệ
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tạo dialog liên hệ
                final Dialog dialog = new Dialog(BookDetailActivity.this);
                dialog.setContentView(R.layout.item_dialog_phone);

                //Trogn dialog sẽ có phone và button gọi ngay
                final TextView txtPhone = dialog.findViewById(R.id.txtPhone);
                Button btnGoiNgay = dialog.findViewById(R.id.btnCall);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                //Gán phone vào textview
                txtPhone.setText(post.getUser().getPhone());

                //Bắt sự kiện chọn button Gọi ngay
                btnGoiNgay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent dial = new Intent(Intent.ACTION_DIAL);
                        try {
                            dial.setData(Uri.parse("tel:" + txtPhone.getText()));
                            startActivity(dial);
                        } catch (Exception e) {
                            Log.e("Calling", "" + e.getMessage());
                        }
                        startActivity(dial);

                    }
                });

                //Bắt sự kiện hủy
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                //Show dialog
                dialog.show();
            }
        });

        //Bắt sự kiện chọn icon back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (backActivity.equals("home")) {

                    Intent intent = new Intent(BookDetailActivity.this, HomeActivity.class);
                    startActivity(intent);
                }

                if (backActivity.equals("search")) {

                    Intent intent = new Intent(BookDetailActivity.this, SearchActivity.class);
                    startActivity(intent);

                }

            }
        });

        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BookDetailActivity.this, SearchActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("key", post.getBook().getTitle());
                intent.putExtras(bundle);

                startActivity(intent);


            }
        });

        txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BookDetailActivity.this, InfoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("user", post.getUser());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BookDetailActivity.this, InfoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("user", post.getUser());
                intent.putExtras(bundle);

                startActivity(intent);

            }
        });
    }

    //Gán giá trị vào view
    private void SetView(Post post) {

        txtUsername.setText(post.getUser().getUsername());
        txtAddress.setText(post.getUser().getAddress());
        txtTitleAuthor.setText(post.getBook().getTitle() + " - " + post.getBook().getAuthor());
        txtDescription.setText(post.getBook().getDescription());
        if (Integer.valueOf(post.getBook().getPrice()) == 0) {

            txtPrice.setText("$ FREE $");
        } else {

            txtPrice.setText(post.getBook().getPrice() + " $");
        }
        Picasso.get().load(post.getBook().getLinkimage()).into(imgBook);
        Picasso.get().load(post.getUser().getLinkavatar()).into(imgAvatar);
        if (!post.getBook().isState()) {

            txtState.setVisibility(View.INVISIBLE);
        } else {

            txtState.setVisibility(View.VISIBLE);
        }
    }

    //Ánh xạ view
    private void AnhXaView() {

        imgBook = (ImageView) findViewById(R.id.imgBook);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtTitleAuthor = (TextView) findViewById(R.id.txtTitleAuthor);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtState = (TextView) findViewById(R.id.txtState);
        txtMore = (TextView) findViewById(R.id.txtMore);
        btnContact = (Button) findViewById(R.id.btnContact);
    }
}
