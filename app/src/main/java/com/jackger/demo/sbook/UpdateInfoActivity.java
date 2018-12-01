package com.jackger.demo.sbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UpdateInfoActivity extends AppCompatActivity {

    private EditText edtFullname, edtPhone;
    private Spinner spinnerAddress;
    private Button btnSave;
    private ImageView imgBack;

    private LocalDatabase database;
    private ArrayList<Address> arrayAddress;
    private AddressAdapter addressAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        //Ánh xạ view
        AnhXaView();

        //Gán giá trị vào view
        SetView();


        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Xử lý khi nhấn chọn button Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Lấy dữ liệu user
                mDatabase.child("USERS").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        User user = dataSnapshot.getValue(User.class);

                        if (user.getEmail().equals(UserMain.getEmail())) {

                            Address address = (Address) spinnerAddress.getSelectedItem();

                            //Kiểm tra dữ liêu nhập
                            if (edtFullname.getText().equals("")
                                    || edtPhone.getText().equals("")
                                    || address.getAddressName().equals("")) {

                                Toast.makeText(UpdateInfoActivity.this, "Dữ liệu lỗi. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                            } else {

                                String id = dataSnapshot.getKey().toString();

                                mDatabase.child("USERS").child(id).child("username").setValue(edtFullname.getText().toString());
                                mDatabase.child("USERS").child(id).child("phone").setValue(edtPhone.getText().toString());
                                mDatabase.child("USERS").child(id).child("address").setValue(address.getAddressName().toString());

                                Toast.makeText(UpdateInfoActivity.this, "Cập nhập thông tin thành công!", Toast.LENGTH_SHORT).show();

                                //Gán giá trị cho UserMain
                                UserMain.setUsername(edtFullname.getText().toString());
                                UserMain.setPhone(edtPhone.getText().toString());
                                UserMain.setAddress(address.getAddressName().toString());

                                //Chuyển về màn hình info
                                Intent intent = new Intent(UpdateInfoActivity.this, InfoUserMainActivity.class);
                                startActivity(intent);

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
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UpdateInfoActivity.this, InfoUserMainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void SetView() {

        edtFullname.setText(UserMain.getUsername());
        edtPhone.setText(UserMain.getPhone());
    }

    private void AnhXaView() {

        edtFullname = (EditText) findViewById(R.id.edtFullname);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        spinnerAddress = (Spinner) findViewById(R.id.spinnerAddress);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        SetAddress();
    }

    //Lấy vị trí address user trgon list
    private void SetAddress() {

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

        //Vị trí địa chỉ trong mảng địa chỉ
        int vt = 0;

        //Tìm vị trí địa chỉ trong mảng địa chỉ
        for (int i = 0; i < arrayAddress.size(); i++) {

            //Kiểm tra địa chỉ giống nhau
            if (arrayAddress.get(i).getAddressName().trim().toUpperCase().equals(UserMain.getAddress().trim().toUpperCase())) {

                vt = i;
                break;
            }
        }

        //Đổ dữ liệu từ adapter vào spiner
        spinnerAddress.setAdapter(addressAdapter);

        //Chọn mặc định trong spinner là HCM (vị trí 29 trong mảng địa chỉ)
        spinnerAddress.setSelection(vt);
    }

}
