package com.jackger.demo.sbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private ImageView imgAvatar, imgBack;
    private EditText edtFullname, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private Spinner spinnerAddress;
    private Button btnContinue;

    private LocalDatabase database;
    private ArrayList<Address> arrayAddress;
    private AddressAdapter addressAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Ánh xạ view
        AnhXaView();

        //Load data đổ vào spinnerAddress
        GetAddressData();


        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Xử lý nhấn chọn button Đăng ký
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Lấy dữ liệu từ spinner address
                Address addressSelected = (Address) spinnerAddress.getSelectedItem();

                //Kiểm tra dữ liêu nhập
                if (edtFullname.getText().toString().equals("")
                        || edtPhone.getText().toString().equals("")
                        || edtEmail.getText().toString().equals("")
                        || addressSelected.getAddressName().equals("")
                        || edtPassword.getText().toString().equals("")
                        || edtConfirmPassword.getText().toString().equals("")
                        || !edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {

                    Toast.makeText(SignupActivity.this, "Dữ liệu đăng ký lỗi. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                } else {

                    //Tạo user lấy dữ liệu từ from đăng ký
                    User user = new User();
                    user.setUsername(edtFullname.getText().toString());
                    user.setPhone(edtPhone.getText().toString());
                    user.setEmail(edtEmail.getText().toString());
                    user.setAddress(addressSelected.getAddressName());
                    user.setPassword(edtPassword.getText().toString());
                    user.setLinkavatar("null");


                    //Tạo bundle gửi dữ liệu user sang màn hình signup2
                    Intent intent = new Intent(SignupActivity.this, SignupActivity2.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });

        //Xử lý nhấn chọn imgBack
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển về màn hình Login
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);


            }
        });
    }

    //Lấy dữ liệu địa chỉ từ local database
    private void GetAddressData() {

        //Tạo mảng địa chỉ
        arrayAddress = new ArrayList<>();

        //Tạo địa chỉ adapter
        addressAdapter = new AddressAdapter(this, arrayAddress);

        //Chọn database
        //Nếu database chưa được tạo hệ thống sẽ tự động tạo
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

        //Chọn mặc định trong spinner là HCM (vị trí 29 trong mảng địa chỉ)
        spinnerAddress.setSelection(30);
    }

    //Ánh xạ view
    private void AnhXaView() {

        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        edtFullname = (EditText) findViewById(R.id.edtFullname);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        spinnerAddress = (Spinner) findViewById(R.id.spinnerAddress);
        btnContinue = (Button) findViewById(R.id.btnContinue);
    }

}
