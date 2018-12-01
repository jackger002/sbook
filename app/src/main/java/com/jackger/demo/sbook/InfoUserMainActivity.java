package com.jackger.demo.sbook;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InfoUserMainActivity extends AppCompatActivity {

    private ImageView imgCover, imgAvatar, imgBack;
    private TextView txtAddress, txtUsername, txtPhone, txtEmail;
    private ImageView imgUpdateInfo;
    private ListView lvPost;

    private DatabaseReference mDatabase;
    private ArrayList<User> arrayUser;
    private ArrayList<Book> arrayBook;
    private ArrayList<Post> arrayPost;
    private PostAdapter postAdapter;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_main);

        //Ánh xạ view
        AnhXaView();

        //Gán giá trị vào view
        SetView();

        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Tạo mảng
        arrayBook = new ArrayList<>();
        arrayPost = new ArrayList<>();

        //Tạo user từ Usermain
        user = new User();
        user.setUsername(UserMain.getUsername());
        user.setPhone(UserMain.getPhone());
        user.setEmail(UserMain.getEmail());
        user.setAddress(UserMain.getAddress());
        user.setLinkavatar(UserMain.getLinkavatar());
        user.setPassword(UserMain.getPassword());

        //Lấy dữ liệu post của user main
        mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Get book
                Book book = dataSnapshot.getValue(Book.class);

                //Tạo mảng post
                if (book.getEmailuser().equals(user.getEmail())) {

                    //Tạo post
                    Post post = new Post(book, user);

                    //Thêm post vào mảng
                    arrayPost.add(post);

                    //Tạo mảng post2 đảo thứ tự từ mảng post1
                    ArrayList<Post> arrayPostReverse = new ArrayList<>();
                    for (int i = arrayPost.size() - 1; i >= 0; i--) {

                        arrayPostReverse.add(arrayPost.get(i));
                    }

                    //Đặt lại kích thước chiều dài listview
                    ViewGroup.LayoutParams params = lvPost.getLayoutParams();
                    params.height = arrayPost.size() * (int) getResources().getDimension(R.dimen.lvPost_height);
                    lvPost.setLayoutParams(params);
                    lvPost.requestLayout();

                    //Tạo postAdapter
                    postAdapter = new PostAdapter(InfoUserMainActivity.this, arrayPostReverse);

                    //Đổ dữ liệu từ postAdapter ra listview
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

        //Bắt sự kiện chọn icon back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển về màn hình Home
                Intent intent = new Intent(InfoUserMainActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });

        //Bắt sự kiện click chọn imgUpdateInfo
        imgUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InfoUserMainActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SetView() {

        Picasso.get().load(UserMain.getLinkavatar()).into(imgCover);
        Picasso.get().load(UserMain.getLinkavatar()).into(imgAvatar);
        txtUsername.setText(UserMain.getUsername());
        txtUsername.setText(UserMain.getUsername());
        txtPhone.setText(UserMain.getPhone());
        txtEmail.setText(UserMain.getEmail());
        txtAddress.setText(UserMain.getAddress());
    }

    private void AnhXaView() {

        imgCover = (ImageView) findViewById(R.id.imageCover);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgUpdateInfo = (ImageView) findViewById(R.id.imgUpdateInfo);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        lvPost = (ListView) findViewById(R.id.lvPost);
    }

    //Tạo dialog menu
    public void DialogMenu(final Book book) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog_edit_post);

        //Trong dialog sẽ hiển thị phone và button gọi ngay
        TextView txtCheckState = dialog.findViewById(R.id.txtCheckState);
        TextView txtEditPost = dialog.findViewById(R.id.txtEditPost);
        TextView txtDeletePost = dialog.findViewById(R.id.txtDeletePost);

        //Bắt sự kiện chọn button check state
        txtCheckState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tìm key của cuốn sách trong database
                mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Book book1 = dataSnapshot.getValue(Book.class);
                        if (book1.getEmailuser().equals(book.getEmailuser())
                                && book1.getTitle().equals(book.getTitle())) {

                            String k = dataSnapshot.getKey().toString();

                            boolean newState = false;
                            if (book1.isState()) {

                                newState = false;
                            } else {

                                newState = true;
                            }

                            mDatabase.child("BOOKS").child(k).child("state").setValue(newState, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    if (databaseError == null) {

                                        Toast.makeText(InfoUserMainActivity.this, "Cập nhập thành công", Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                        finish();
                                        startActivity(getIntent());
                                    } else {

                                        Toast.makeText(InfoUserMainActivity.this, "Cập nhập thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
            }
        });

        //Bắt sự kiện click chọn txtEditPost
        txtEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InfoUserMainActivity.this, EditPostActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("book", book);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        //Bắt sự kiện chọn txtDeletePost
        txtDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Gọi hàm dialog
                DialogDeleteCofirm(book);

                dialog.dismiss();


            }
        });

        //Show dialog
        dialog.show();
    }

    //Tạo dialog Xác nhận xóa bài viết
    public void DialogDeleteCofirm(final Book book) {

        final Dialog dialog = new Dialog(InfoUserMainActivity.this);
        dialog.setContentView(R.layout.iteam_dialog_delete_post_confirm);

        //Trong dialog sẽ hiển thị phone và button gọi ngay
        Button btnOK = dialog.findViewById(R.id.btnOK);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        //Bắt sự kiện chọn button Xóa
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Book book1 = dataSnapshot.getValue(Book.class);

                        if (book1.getTitle().equals(book.getTitle())
                                && book1.getEmailuser().equals(UserMain.getEmail())) {

                            String k = dataSnapshot.getKey().toString();

                            mDatabase.child("BOOKS").child(k).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    if (databaseError == null) {

                                        Toast.makeText(InfoUserMainActivity.this, "Xóa bài viết thành công", Toast.LENGTH_SHORT).show();

                                        finish();
                                        startActivity(getIntent());
                                    } else {

                                        Toast.makeText(InfoUserMainActivity.this, "Lỗi! Không thể xóa bài viết", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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


            }
        });

        //bắt sự kiện chọn button Hủy
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        //Show dialog
        dialog.show();
    }
}
