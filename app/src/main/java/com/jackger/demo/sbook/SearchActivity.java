package com.jackger.demo.sbook;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private ImageView imgBack;
    private SearchView searchView;
    private Spinner spinnerAddress;
    private ListView lvPostSearch;
    private Button btnTitle, btnAuthor, btnUser;

    private LocalDatabase database;
    private ArrayList<Address> arrayAddress;
    private AddressAdapter addressAdapter;

    private DatabaseReference mDatabase;
    private ArrayList<User> arrayUser;
    private ArrayList<Book> arrayBook;
    private ArrayList<Post> arrayPost;
    private PostSearchAdapter postSearchAdapter;

    private ArrayList<User> arrayUserSearch;
    private UserAdapter userAdapter;

    private Address addressSelected;

    //Mặc định tìm kiếm theo tiêu đề sách
    private int checkSearch = 1;

    //Từ khóa tìm kiếm
    private String key = "";

    //Đánh dấu keySearch
    private boolean checKeySearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Ánh xạ view
        AnhXaView();

        //Load data địa chỉ đổ vào spinnerAddress
        GetAddressData();

        //Lấy dữ liệu gửi từ màn hình trước
        try {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                String s = (String) bundle.getSerializable("key");
                key = removeAccent(s);

                //Đánh dấu có keySearch
                checKeySearch = true;
            }

        } catch (Exception e) {

        }

        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Tạo mảng
        arrayUser = new ArrayList<>();
        arrayBook = new ArrayList<>();

        //Lấy dữ liệu user
        GetUserData();

        //Lấy dữ liệu Book
        GetBookData();

        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Lấy dữ liệu địa chỉ từ spinnerAddress
                addressSelected = (Address) spinnerAddress.getSelectedItem();

                if (checKeySearch) {

                    //Điền keySearch vào searchView
                    searchView.setQuery(key, true);

                    //đánh dấu checKeySearch
                    checKeySearch = false;

                }

                //Lấy query key từ searchView
                String query = searchView.getQuery().toString();

                //Tìm kiếm post
                SearchPost(query, addressSelected.getAddressName(), checkSearch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Xử lý sự kiện gõ tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //Lấy dữ liệu địa chỉ từ spinnerAddress
                addressSelected = (Address) spinnerAddress.getSelectedItem();

                //Tìm kiếm post
                SearchPost(query, addressSelected.getAddressName(), checkSearch);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Tìm kiếm post
                SearchPost(newText, addressSelected.getAddressName(), checkSearch);

                return false;
            }
        });

        //Xử lý sự kiện click imgBack
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //Xử lý click button
        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnTitle.setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
                btnTitle.setTextColor(getResources().getColor(R.color.colorButtonText));

                btnAuthor.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnAuthor.setTextColor(getResources().getColor(R.color.colorEditText));
                btnUser.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnUser.setTextColor(getResources().getColor(R.color.colorEditText));

                //Tìm kiếm theo tiêu đề sách
                checkSearch = 1;

                //Lấy query key từ searchView
                String query = searchView.getQuery().toString();

                //Tìm kiếm post
                SearchPost(query, addressSelected.getAddressName(), checkSearch);

            }
        });

        btnAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAuthor.setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
                btnAuthor.setTextColor(getResources().getColor(R.color.colorButtonText));

                btnTitle.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnTitle.setTextColor(getResources().getColor(R.color.colorEditText));
                btnUser.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnUser.setTextColor(getResources().getColor(R.color.colorEditText));

                //Tìm kiếm theo tác giả
                checkSearch = 2;

                //Lấy query key từ searchView
                String query = searchView.getQuery().toString();

                //Tìm kiếm post
                SearchPost(query, addressSelected.getAddressName(), checkSearch);

            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnUser.setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
                btnUser.setTextColor(getResources().getColor(R.color.colorButtonText));

                btnAuthor.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnAuthor.setTextColor(getResources().getColor(R.color.colorEditText));
                btnTitle.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
                btnTitle.setTextColor(getResources().getColor(R.color.colorEditText));

                //Tìm kiếm theo user
                checkSearch = 3;

                //Lấy query key từ searchView
                String query = searchView.getQuery().toString();

                //Tìm kiếm post
                SearchPost(query, addressSelected.getAddressName(), checkSearch);

            }
        });
    }

    private void GetBookData() {

        mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Book book = dataSnapshot.getValue(Book.class);
                arrayBook.add(book);
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

    @SuppressLint("ResourceAsColor")
    private void AnhXaView() {

        imgBack = (ImageView) findViewById(R.id.imgBack);
        searchView = (SearchView) findViewById(R.id.searchView);
        spinnerAddress = (Spinner) findViewById(R.id.spinnerAddress);
        lvPostSearch = (ListView) findViewById(R.id.lvPostSearch);
        btnTitle = (Button) findViewById(R.id.btnTitle);
        btnAuthor = (Button) findViewById(R.id.btnAuthor);
        btnUser = (Button) findViewById(R.id.btnUser);

        btnTitle.setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
        btnTitle.setTextColor(getResources().getColor(R.color.colorButtonText));

        btnAuthor.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
        btnAuthor.setTextColor(getResources().getColor(R.color.colorEditText));
        btnUser.setBackgroundColor(getResources().getColor(R.color.colorReadOnly));
        btnUser.setTextColor(getResources().getColor(R.color.colorEditText));

    }

    //Loại bỏ dấu trong từ
    public String removeAccent(String s) {

        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    //Tìm kiếm post theo title và author
    private void SearchPost(String newText, String address, int check) {

        //Người dùng thực hiện tìm kiếm
        if (!newText.trim().equals("")) {

            //Kiểm tra dữ liệu user đã được lấy chưa
            if (arrayUser.size() == 0) {

                GetUserData();
            }

            //Kiểm tra dữ liệu book
            if (arrayBook.size() == 0) {

                GetBookData();
            }

            //Tạo mảng post
            arrayPost = new ArrayList<>();

            //Tìm kiếm theo tiêu đề sách
            if (check == 1) {

                //Địa chỉ là tất cả
                if (address.trim().equals("TẤT CẢ")) {

                    //Duyệt ngược mảng book
                    for (int i = arrayBook.size() - 1; i >= 0; i--) {

                        //Tìm kiếm theo tiêu đề sách
                        if (removeAccent(arrayBook.get(i).getTitle()).toLowerCase().trim().contains(newText.toLowerCase().trim())) {
                            for (User user : arrayUser) {

                                if (arrayBook.get(i).getEmailuser().equals(user.getEmail())) {

                                    Post post = new Post(arrayBook.get(i), user);

                                    arrayPost.add(post);
                                }
                            }
                        }
                    }

                    //Không tìm được cuốn sách nào theo yêu cầu
                    if (arrayPost.size() == 0) {

                        Toast.makeText(this, "Không tìm được tiêu đề sách nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                        return;
                    }

                } else { //Tìm kiếm theo địa chỉ

                    //Duyệt ngược mảng book
                    for (int i = arrayBook.size() - 1; i >= 0; i--) {

                        //Tìm kiếm theo tiêu đề sách
                        if (removeAccent(arrayBook.get(i).getTitle()).toLowerCase().contains(newText.toLowerCase())) {

                            for (User user : arrayUser) {

                                //Kiểm tra email và address
                                if (arrayBook.get(i).getEmailuser().equals(user.getEmail())
                                        && user.getAddress().toLowerCase().trim().equals(address.toLowerCase().trim())) {

                                    Post post = new Post(arrayBook.get(i), user);
                                    arrayPost.add(post);
                                }
                            }
                        }
                    }

                }

                postSearchAdapter = new PostSearchAdapter(SearchActivity.this, arrayPost);
                lvPostSearch.setAdapter(postSearchAdapter);

                //Nếu địa chỉ không có post nào
                if (arrayPost.size() == 0) {

                    Toast.makeText(this, "Không tìm được tiêu đề sách nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                    SearchPost(newText, "TẤT CẢ", check);
                }

            } else {

                //Tìm kiếm theo tên tác giả
                if (check == 2) {

                    //Địa chỉ là tất cả
                    if (address.trim().equals("TẤT CẢ")) {

                        //Duyệt ngược mảng book
                        for (int i = arrayBook.size() - 1; i >= 0; i--) {

                            //Tìm kiếm theo tên tác giả
                            if (removeAccent(arrayBook.get(i).getAuthor()).toLowerCase().trim().contains(newText.toLowerCase().trim())) {

                                for (User user : arrayUser) {

                                    if (arrayBook.get(i).getEmailuser().equals(user.getEmail())) {

                                        Post post = new Post(arrayBook.get(i), user);
                                        arrayPost.add(post);
                                    }
                                }
                            }
                        }

                        //Không tìm được cuốn sách nào theo yêu cầu
                        if (arrayPost.size() == 0) {

                            Toast.makeText(this, "Không tìm được tác giả nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                            return;
                        }

                    } else { //Tìm kiếm theo địa chỉ

                        //Duyệt ngược mảng book
                        for (int i = arrayBook.size() - 1; i >= 0; i--) {

                            //Tìm kiếm theo tên tác giả
                            if (removeAccent(arrayBook.get(i).getAuthor()).toLowerCase().contains(newText.toLowerCase())) {

                                for (User user : arrayUser) {

                                    //Kiểm tra email và address
                                    if (arrayBook.get(i).getEmailuser().equals(user.getEmail())
                                            && user.getAddress().toLowerCase().trim().equals(address.toLowerCase().trim())) {

                                        Post post = new Post(arrayBook.get(i), user);
                                        arrayPost.add(post);
                                    }
                                }
                            }
                        }

                    }

                    postSearchAdapter = new PostSearchAdapter(SearchActivity.this, arrayPost);
                    lvPostSearch.setAdapter(postSearchAdapter);

                    //Nếu địa chỉ không có post nào
                    if (arrayPost.size() == 0) {

                        Toast.makeText(this, "Không tìm được tác giả nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                        SearchPost(newText, "TẤT CẢ", check);
                    }


                } else {

                    //Tìm kiếm theo user
                    if (check == 3) {

                        //tạo mảng userSearch
                        arrayUserSearch = new ArrayList<>();

                        //Địa chỉ là tất cả
                        if (address.trim().equals("TẤT CẢ")) {

                            //Duyệt ngược mảng book
                            for (int i = arrayUser.size() - 1; i >= 0; i--) {

                                //Tìm kiếm theo user
                                if (removeAccent(arrayUser.get(i).getUsername()).toLowerCase().trim().contains(newText.toLowerCase().trim())) {

                                    arrayUserSearch.add(arrayUser.get(i));
                                }
                            }

                            //Không tìm được cuốn sách nào theo yêu cầu
                            if (arrayUserSearch.size() == 0) {

                                Toast.makeText(this, "Không tìm được người dùng nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                                return;
                            }

                        } else { //Tìm kiếm theo địa chỉ

                            //Duyệt ngược mảng book
                            for (int i = arrayUser.size() - 1; i >= 0; i--) {

                                //Tìm kiếm theo tên tác giả
                                if (removeAccent(arrayUser.get(i).getUsername()).toLowerCase().contains(newText.toLowerCase())
                                        && arrayUser.get(i).getAddress().toLowerCase().trim().equals(address.toLowerCase().trim())) {

                                    arrayUserSearch.add(arrayUser.get(i));
                                }
                            }

                        }

                        userAdapter = new UserAdapter(SearchActivity.this, arrayUserSearch);
                        lvPostSearch.setAdapter(userAdapter);

                        //Nếu địa chỉ không có post nào
                        if (arrayUserSearch.size() == 0) {

                            Toast.makeText(this, "Không tìm được người dùng nào theo yêu cầu", Toast.LENGTH_SHORT).show();

                            SearchPost(newText, "TẤT CẢ", check);
                        }


                    }
                }
            }
        }
    }

    public void PickBookImage(Post post) {

        Intent intent = new Intent(this, BookDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        bundle.putSerializable("backActivity", "search");
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //Xử lý khi nhấn chọn
    public void PickLinearLayoutUser(User user) {

        Intent intent = new Intent(this, InfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
