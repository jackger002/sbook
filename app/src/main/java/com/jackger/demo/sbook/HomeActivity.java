package com.jackger.demo.sbook;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    private Spinner spinnerAddress;
    private ListView lvPost;
    private ImageView icHome, icNewPost, icCamera, icUser, icMenu;
    private LinearLayout linearLayout;

    private LocalDatabase database;
    private ArrayList<Address> arrayAddress;
    private AddressAdapter addressAdapter;

    private DatabaseReference mDatabase;
    private ArrayList<User> arrayUser;
    private ArrayList<Book> arrayBook;
    private ArrayList<Post> arrayPost;
    private PostAdapter postAdapter;

    private Address addressSelected;

    private final int POST_COUNT_20 = 20;
    private final int POST_COUNT_100 = 100;

    final int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Ánh xạ view
        AnhXaView();

        //Load data địa chỉ đổ vào spinnerAddress
        GetAddressData();

        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Tạo mảng user
        arrayUser = new ArrayList<>();

        //Lấy dữ liệu user từ firebase
        GetUserData();

        //Xử lý khi người dùng thay đổi địa chỉ tìm kiếm
        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Kiểm tra dữ liệu user đã lấy thành công chưa, nếu chưa lấy lại
                if (arrayUser.size() == 0) {

                    //Lấy dữ liệu user
                    GetUserData();
                }

                //Tạo mảng book
                arrayBook = new ArrayList<>();

                //Tạo mảng post
                arrayPost = new ArrayList<>();

                //Lấy dữ liệu từ spinner address
                addressSelected = (Address) spinnerAddress.getSelectedItem();

                //Kiểm tra địa chỉ người dùng chọn
                if (addressSelected.getAddressName().trim().equals("TẤT CẢ")) {

                    //Lấy dữ liệu book
                    mDatabase.child("BOOKS").limitToLast(POST_COUNT_20).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            //Lấy dữ liệu book
                            Book book = dataSnapshot.getValue(Book.class);

                            //Tạo mảng book
                            arrayBook.add(book);

                            //Tạo post
                            for (User user : arrayUser) {

                                if (user.getEmail().equals(book.getEmailuser())) {

                                    //Tạo post
                                    Post post = new Post(book, user);

                                    //Thêm post vào mảng post
                                    arrayPost.add(post);

                                    //Tạo mảng post2 đảo thứ tự từ mảng post1
                                    ArrayList<Post> arrayPostReverse = new ArrayList<>();
                                    for (int i = arrayPost.size() - 1; i >= 0; i--) {

                                        arrayPostReverse.add(arrayPost.get(i));
                                    }

                                    //Tạo postAdapter từ mảng post đảo ngược
                                    postAdapter = new PostAdapter(HomeActivity.this, arrayPostReverse);

                                    //Đổ dữ liệu từ adapter ra listview
                                    lvPost.setAdapter(postAdapter);


                                }
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


                } else {

                    //Lấy dữ liệu book
                    mDatabase.child("BOOKS").limitToLast(POST_COUNT_100).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            //Lấy dữ liệu book
                            Book book = dataSnapshot.getValue(Book.class);

                            //Tạo mảng book
                            arrayBook.add(book);

                            //Tạo post
                            for (User user : arrayUser) {

                                if (user.getEmail().equals(book.getEmailuser())
                                        && user.getAddress().trim().equals(addressSelected.getAddressName().trim())) {

                                    //Tạo post
                                    Post post = new Post(book, user);

                                    //Thêm post vào mảng post
                                    arrayPost.add(post);

                                    //Tạo mảng post2 đảo thứ tự từ mảng post1
                                    ArrayList<Post> arrayPostReverse = new ArrayList<>();
                                    for (int i = arrayPost.size() - 1; i >= 0; i--) {

                                        arrayPostReverse.add(arrayPost.get(i));
                                    }

                                    //Tạo postAdapter từ mảng post đảo ngược
                                    postAdapter = new PostAdapter(HomeActivity.this, arrayPostReverse);

                                    //Đổ dữ liệu từ adapter ra listview
                                    lvPost.setAdapter(postAdapter);


                                }
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Xử lý sự kiện chọn icHome
        icHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                startActivity(getIntent());
            }
        });

        //Xủ lý chon button Info
        icUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển đến màn hình InfoUserMain
                Intent intent = new Intent(HomeActivity.this, InfoUserMainActivity.class);
                startActivity(intent);

            }
        });

        //Xử lý click chọn icon Menu
        icMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển đến màn hình Menu
                Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });

        //Xử lý chọn icon NewPost
        icNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển đến màn hình NewPost
                Intent intent = new Intent(HomeActivity.this, NewPostActivity.class);
                startActivity(intent);

            }
        });

        //Xử lý khi chọn icon camera
        icCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);

            }
        });

        //Xử lý sự kiện tìm kiếm
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    //Lấy dữ liệu user từ firebase
    private void GetUserData() {

        mDatabase.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);
                arrayUser.add(user);
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

    //Lấy dữ liệu địa chỉ từ local database
    private void GetAddressData() {

        //Tạo mảng địa chỉ
        arrayAddress = new ArrayList<>();

        //Tạo địa chỉ adapter
        addressAdapter = new AddressAdapter(this, arrayAddress);

        //Chọn local database
        database = new LocalDatabase(this, "sbook.sqlite", null, 1);

        //Lấy dữ liệu từ bảng địa chỉ
        Cursor dataAddress = database.GetData("SELECT * FROM DIACHI");
        while (dataAddress.moveToNext()) {

            //Tạo địa chỉ
            int id = dataAddress.getInt(0);
            String addressName = dataAddress.getString(1);

            //Thêm đối tượng địa chỉ vào mảng địa chỉ
            arrayAddress.add(new Address(id, addressName));

        }

        //Đổ dữ liệu từ adapter vào spiner
        spinnerAddress.setAdapter(addressAdapter);

        //Chọn mặc định trong spinner là TẤT CẢ (vị trí 0 trong mảng địa chỉ)
        spinnerAddress.setSelection(0);
    }

    //Ánh xạ view
    private void AnhXaView() {

        spinnerAddress = (Spinner) findViewById(R.id.spinnerAddress);
        lvPost = (ListView) findViewById(R.id.lvPost);
        icHome = (ImageView) findViewById(R.id.icHome);
        icNewPost = (ImageView) findViewById(R.id.icNewPost);
        icCamera = (ImageView) findViewById(R.id.icCamera);
        icUser = (ImageView) findViewById(R.id.icUser);
        icMenu = (ImageView) findViewById(R.id.icMenu);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
    }

    //Tạo dialog liên hệ
    public void DialogLienHe(String phone) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog_phone);

        //Trong dialog sẽ hiển thị phone và button gọi ngay
        final TextView txtPhone = dialog.findViewById(R.id.txtPhone);
        Button btnGoiNgay = dialog.findViewById(R.id.btnCall);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        //Get phone
        txtPhone.setText(phone);

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

    //Xử lý nhấn chọn book image
    public void PickBookImage(Post post) {

        Intent intent = new Intent(this, BookDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        bundle.putString("backActivity", "home");
        intent.putExtras(bundle);

        startActivity(intent);

    }

    //Xử lý nhấn chọn avatar user
    public void PickUserAvatar(User user) {

        Intent intent = new Intent(this, InfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //Xử lý khi nhấn chọn
    public void PickUsername(User user) {

        Intent intent = new Intent(this, InfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //Xử lý lưu ảnh vào ImageView trong màn hình Bài viết mới
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {

            //Ảnh sau khi chụp sẽ được xử lý về dạng bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //Chuyển ảnh từ dạng bitmap sang byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Mảng byte thông tin ảnh
            byte[] byteArray = stream.toByteArray();

            //Gọi màn hình Bài viết mới
            Intent intent = new Intent(HomeActivity.this, NewPostActivity.class);

            //Gửi kèm mảng byte thông tin ảnh
            intent.putExtra("picture", byteArray);

            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
