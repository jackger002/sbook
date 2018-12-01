package com.jackger.demo.sbook;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private ImageView imgCover, imgAvatar, imgBack;
    private TextView txtAddress, txtUsername, txtMore;
    private Button btnContact;
    private ListView lvPost;

    private User user;

    private DatabaseReference mDatabase;
    private ArrayList<User> arrayUser;
    private ArrayList<Book> arrayBook;
    private ArrayList<Post> arrayPost;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Ánh xạ view
        AnhXaView();

        //Lấy dữ liệu user gửi từ LoginActivity
        final Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        //Gán giá trị vào view
        SetView(user);

        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Tạo mảng sách
        arrayBook = new ArrayList<>();

        //Tạo mảng post
        arrayPost = new ArrayList<>();

        //Lấy dữ liệu post
        mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Lấy dữ liệu book
                Book book1 = dataSnapshot.getValue(Book.class);

                //Xét điều kiện sách được chọn là sách do user đăng
                if (book1.getEmailuser().equals(user.getEmail())) {

                    //Thêm sách vào mảng sách
                    arrayBook.add(book1);

                    //Tạo post
                    Post post1 = new Post(book1, user);

                    //Thêm post1 vào mảng post
                    arrayPost.add(post1);

                    //Tạo mảng post2 đảo thứ tự từ mảng post1
                    ArrayList<Post> arrayPostReverse = new ArrayList<>();
                    for (int i = arrayPost.size() - 1; i >= 0; i--) {

                        arrayPostReverse.add(arrayPost.get(i));
                    }

                    //Tạo postAdapter từ mảng post đảo ngược
                    postAdapter = new PostAdapter(InfoActivity.this, arrayPostReverse);

                    //Đặt lại kích thước chiều dài listview
                    ViewGroup.LayoutParams params = lvPost.getLayoutParams();
                    params.height = arrayPost.size() * (int) getResources().getDimension(R.dimen.lvPost_height);
                    lvPost.setLayoutParams(params);
                    lvPost.requestLayout();

                    //Đổ dữ liệu từ adapter ra listview
                    lvPost.setAdapter(postAdapter);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Bắt sự kiện click chọn button Liên hệ
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(InfoActivity.this);
                dialog.setContentView(R.layout.item_dialog_phone);

                TextView txtPhone = dialog.findViewById(R.id.txtPhone);
                Button btnGoiNgay = dialog.findViewById(R.id.btnCall);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                txtPhone.setText(user.getPhone());

                //Bắt sự kiện chọn button Gọi ngay
                btnGoiNgay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                //Bắt sự kiện hủy
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //Bắt sự kiện chọn icon back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InfoActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });


    }

    //Gán giá trị vào view
    private void SetView(User user) {

        Picasso.get().load(user.getLinkavatar()).into(imgCover);
        Picasso.get().load(user.getLinkavatar()).into(imgAvatar);
        txtUsername.setText(user.getUsername());
        txtAddress.setText(user.getAddress());
    }

    //Ánh xạ view
    private void AnhXaView() {

        imgCover = (ImageView) findViewById(R.id.imageCover);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtMore = (TextView) findViewById(R.id.txtMore);
        btnContact = (Button) findViewById(R.id.btnContact);
        lvPost = (ListView) findViewById(R.id.lvPost);
    }

    //Xử lý nhấn chọn book image
    public void PickBookImage(Post post) {

        Intent intent = new Intent(this, BookDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        bundle.putString("backActivity", "home");
        intent.putExtras(bundle);

        startActivity(intent);

    }
}
