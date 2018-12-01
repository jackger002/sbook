package com.jackger.demo.sbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EditPostActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtPrice, edtDescription;
    private TextView txtGallery, txtCamera;
    private Button btnSave, btnCancel;
    private ImageView imgBook, imgBack;

    private Book book;

    private DatabaseReference mDatabase;

    private String k;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //Ánh xạ view
        AnhXaView();

        //Lấy dữ liệu user gửi từ LoginActivity
        Bundle bundle = getIntent().getExtras();
        book = (Book) bundle.getSerializable("book");

        //Gán giá trị
        SetView();

        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Bắt sự kiện click igmBack
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditPostActivity.this, InfoUserMainActivity.class);
                startActivity(intent);
            }
        });

        //Bắt sự kiện click btnCancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditPostActivity.this, InfoUserMainActivity.class);
                startActivity(intent);
            }
        });

        mDatabase.child("BOOKS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Book book1 = dataSnapshot.getValue(Book.class);

                if (book1.getTitle().equals(book.getTitle())
                        && book1.getEmailuser().equals(UserMain.getEmail())) {

                    k = dataSnapshot.getKey().toString();
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

        //Bắt sự kiện click chọn btnSave
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!k.equals("")) {

                    //Kiểm tra dữ liệu đầu vào
                    if (edtTitle.getText().toString().equals("")
                            || edtAuthor.getText().toString().equals("")
                            || edtDescription.getText().toString().equals("")
                            || edtPrice.getText().toString().equals("")) {

                        Toast.makeText(EditPostActivity.this, "Dữ liệu lỗi. Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();

                    } else {

                        mDatabase.child("BOOKS").child(k).child("title").setValue(edtTitle.getText().toString());
                        mDatabase.child("BOOKS").child(k).child("author").setValue(edtAuthor.getText().toString());
                        mDatabase.child("BOOKS").child(k).child("description").setValue(edtDescription.getText().toString());
                        mDatabase.child("BOOKS").child(k).child("price").setValue(Integer.valueOf(edtPrice.getText().toString()));

                        Toast.makeText(EditPostActivity.this, "Cập nhập bài viết thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void SetView() {

        edtTitle.setText(book.getTitle());
        edtAuthor.setText(book.getAuthor());
        edtPrice.setText(String.valueOf(book.getPrice()));
        edtDescription.setText(book.getDescription());
        Picasso.get().load(book.getLinkimage()).into(imgBook);
    }

    private void AnhXaView() {

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtAuthor = (EditText) findViewById(R.id.edtAuthor);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
//        txtGallery = (TextView) findViewById(R.id.txtGallery);
//        txtCamera = (TextView) findViewById(R.id.txtCamera);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        imgBook = (ImageView) findViewById(R.id.imgBook);
        imgBack = (ImageView) findViewById(R.id.imgBack);
    }
}
