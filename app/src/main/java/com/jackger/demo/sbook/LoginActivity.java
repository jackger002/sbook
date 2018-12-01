package com.jackger.demo.sbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private CheckBox checkboxRememberPassword;
    private Button btnLogin;
    private TextView txtSignup, txtForgetPassword;

    private LocalDatabase database;
    private ArrayList<Address> arrayAddress;

    private DatabaseReference mDatabase;
    private ArrayList<User> arrayUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ánh xạ view
        AnhXaView();

        //Load data
        GetAddressData();


        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Tạo arrayUser
        arrayUser = new ArrayList<>();

        //Lấy dữ liệu user
        GetUserData();

        //Xử lý nhấn button Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Nếu dữ liệu user chưa lấy được, lấy lại lần nữa
                if (arrayUser.size() == 0) {

                    //Lấy dữ liệu user
                    GetUserData();

                }

                //dữ liệu user đã lấy thành công
                if (arrayUser.size() > 0) {

                    //Lấy dữ liệu từ form login
                    String username = edtUsername.getText().toString();
                    String password = edtPassword.getText().toString();

                    //Kiểm tra đăng nhập
                    int ckLogin = CheckLogin(username, password, arrayUser);

                    //Đăng nhập đúng
                    if (CheckLogin(username, password, arrayUser) != -1) {

                        //Gán giá trị cho UserMain
                        UserMain.setUsername(arrayUser.get(ckLogin).getUsername());
                        UserMain.setPhone(arrayUser.get(ckLogin).getPhone());
                        UserMain.setEmail(arrayUser.get(ckLogin).getEmail());
                        UserMain.setAddress(arrayUser.get(ckLogin).getAddress());
                        UserMain.setPassword(arrayUser.get(ckLogin).getPassword());
                        UserMain.setLinkavatar(arrayUser.get(ckLogin).getLinkavatar());

                        //Chuyển đến màn hình Home
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    } else {

                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();

                        //Xóa hết dữ liệu người dùng nhập trong form
                        edtUsername.setText("");
                        edtPassword.setText("");
                    }

                } else {

                    Toast.makeText(LoginActivity.this, "Dữ liệu đang được tải! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Xử lý khi chọn button Đăng ký
        txtSignup.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                //Chuyến đến màn hình Đăng ký
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
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

    //Ánh xạ view
    private void AnhXaView() {

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        checkboxRememberPassword = (CheckBox) findViewById(R.id.checkboxRememberPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtSignup = (TextView) findViewById(R.id.txtSignup);
        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);
    }

    //Lấy dữ liệu địa chỉ từ local database
    private void GetAddressData() {

        //Tạo mảng địa chỉ
        arrayAddress = new ArrayList<>();

        //Chọn local database
        //Nếu database chưa được tạo hệ thống sẽ tự động tạo
        database = new LocalDatabase(this, "sbook.sqlite", null, 1);

        //Tạo bảng địa chỉ
        database.QueryData("CREATE TABLE IF NOT EXISTS DIACHI(ID INTEGER PRIMARY KEY AUTOINCREMENT, TENDIACHI VARCHAR(50))");

        //Dữ liệu bảng địa chỉ tạo 1 lần duy nhất
        //Nếu đã có dữ liệu thì ko thêm nữa
        //Tạo hàm kiểm tra bảng địa chỉ đã có dữ liệu chưa
        Cursor dataAddress2 = database.GetData("SELECT * FROM DIACHI");
        int ck = 0;
        while (dataAddress2.moveToNext()) {
            ck++;
        }

        //Nếu chưa có dữ liệu thì thêm dữ liệu
        if (ck == 0) {
            //Thêm dữ liệu vào bảng địa chỉ
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TẤT CẢ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'AN GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BÀ RỊA-VŨNG TÀU')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BẠC LIÊU')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BẮC KẠN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BẮC GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BẮC NINH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BẾN TRE')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BÌNH DƯƠNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BÌNH ĐỊNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BÌNH PHƯỚC')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'BÌNH THUẬN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'CÀ MAU')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'CAO BẰNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'CẦN THƠ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐÀ NẴNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐẮK LẮK')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐẮK NÔNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐIỆN BIÊN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐỒNG NAI')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'ĐỒNG THÁP')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'GIA LAI ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÀ GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÀ NAM')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÀ NỘI')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÀ TÂY')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÀ TĨNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HẢI DƯƠNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HẢI PHÒNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HÒA BÌNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TP HCM')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HẬU GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'HƯNG YÊN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'KHÁNH HÒA')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'KIÊN GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'KON TUM')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'LAI CHÂU')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'LÀO CAI')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'LẠNG SƠN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'LÂM ĐỒNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'LONG AN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'NAM ĐỊNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'NGHỆ AN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'NINH BÌNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'NINH THUẬN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'PHÚ THỌ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'PHÚ YÊN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'QUẢNG BÌNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'QUẢNG NAM')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'QUẢNG NGÃI')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'QUẢNG NINH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'QUẢNG TRỊ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'SÓC TRĂNG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'SƠN LA')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TÂY NINH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'THÁI BÌNH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'THÁI NGUYÊN')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'THANH HÓA')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'THỪA THIÊN - HUẾ')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TIỀN GIANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TRÀ VINH')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'TUYÊN QUANG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'VĨNH LONG')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'VĨNH PHÚC')");
            database.QueryData("INSERT INTO DIACHI VALUES(null, 'YÊN BÁI')");
        }

        //Lấy dữ liệu từ bảng địa chỉ
        Cursor dataAddress = database.GetData("SELECT * FROM DIACHI");
        while (dataAddress.moveToNext()) {

            //Tạo địa chỉ
            Address address = new Address(dataAddress.getInt(0), dataAddress.getString(1));

            //Thêm địa chỉ vào mảng địa chỉ
            arrayAddress.add(address);
        }
    }

    //Check Login
    //Trả về vị trí của user trong mảng
    private int CheckLogin(String email, String password, ArrayList<User> users) {

        int i = 0;
        for (User user : users) {

            if (email.equals(user.getEmail()) && password.equals(user.getPassword())) {
                return i; //login success
            }

            i++;
        }
        return -1; //login fail
    }

}
