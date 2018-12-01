package com.jackger.demo.sbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class NewPostActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtPrice, edtDescription;
    private TextView txtGallery, txtCamera;
    private Button btnOK;
    private ImageView imgBook, imgBack;

    final int REQUEST_CODE_IMAGE = 1;

    final int IMAGE_PICK_CODE = 1000;
    final int PERMISSION_CODE = 1001;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //Ánh xạ view
        AnhXaView();

        //Xử lý nhận ảnh gửi từ màn hình camera
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            byte[] byteArray = extras.getByteArray("picture");

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            //Đổ ảnh vào ImageView
            imgBook.setImageBitmap(bitmap);
        }


        //Kết nối firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://sbook-6be8c.appspot.com");


        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Xử lý click txtGallery
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {

                        PickImageGallery();
                    }
                } else {

                    PickImageGallery();

                }

            }
        });

        //Xử lý chọn txtCamera
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);

            }
        });

        //Xử lý khi click button OK
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kiểm tra dữ liệu đầu vào
                if (edtTitle.getText().toString().equals("")
                        || edtAuthor.getText().toString().equals("")
                        || edtDescription.getText().toString().equals("")
                        || edtPrice.getText().toString().equals("")) {

                    Toast.makeText(NewPostActivity.this, "Dữ liệu bài viết lỗi. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                } else {

                    //Disable form
                    DisableForm(false);

                    //Tạo Book lấy dữ liệu từ form
                    final Book book = new Book();
                    book.setTitle(edtTitle.getText().toString());
                    book.setAuthor(edtAuthor.getText().toString());
                    book.setDescription(edtDescription.getText().toString());
                    book.setPrice(Integer.valueOf(edtPrice.getText().toString()));
                    book.setLinkimage("null");
                    book.setState(false);
                    book.setEmailuser(UserMain.getEmail());


                    //Tạo biến calendar lưu tên ảnh tránh bị trùng lặp
                    Calendar calendar = Calendar.getInstance();

                    //Tạo tên ảnh khi lưu storage
                    StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

                    // Get the data from an ImageView as bytes
                    imgBook.setDrawingCacheEnabled(true);
                    imgBook.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgBook.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
//                        Toast.makeText(SignupActivity2.this, "Upload ảnh lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
//                        Toast.makeText(SignupActivity2.this, "Upload ảnh thành công", Toast.LENGTH_SHORT).show();

                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //Lấy link ảnh vừa up lên storage
                                    String url = uri.toString();

//                                Log.d("XXX", url + "");
//
//                                imgAvatar.setImageResource(R.drawable.ic_user);
//                                Picasso.get().load(url).into(imgAvatar);

                                    //Gán ảnh vừa uploaf vào linkimagebook
                                    book.setLinkimage(url);

                                    //Thêm user vào database
                                    mDatabase.child("BOOKS").push().setValue(book, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                            if (databaseError == null) {

                                                Toast.makeText(NewPostActivity.this, "Đăng bài viết thành công", Toast.LENGTH_SHORT).show();


                                                //Chuyển đến màn hình Home
                                                Intent intent = new Intent(NewPostActivity.this, HomeActivity.class);//
                                                startActivity(intent);


                                            } else {

                                                Toast.makeText(NewPostActivity.this, "Đăng bài viết thất bại. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                                                //Enable Form
                                                DisableForm(true);
                                            }
                                        }
                                    });

                                }
                            });

                        }
                    });

                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NewPostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AnhXaView() {

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtAuthor = (EditText) findViewById(R.id.edtAuthor);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        txtGallery = (TextView) findViewById(R.id.txtGallery);
        txtCamera = (TextView) findViewById(R.id.txtCamera);
        btnOK = (Button) findViewById(R.id.btnOK);
        imgBook = (ImageView) findViewById(R.id.imgBook);
        imgBack = (ImageView) findViewById(R.id.imgBack);
    }

    //Pick image from Gallery
    private void PickImageGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //Gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickImageGallery();
                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Xử lý kết quả ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //camera
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {

            //Get ảnh chụp bằng camera dưới dạng bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //Hiển thị ảnh vừa chụp lên imgAvatar
            imgBook.setImageBitmap(bitmap);
        }

        //gallery
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {

            //Hiển thị ảnh chọn lên imgAvatar
            imgBook.setImageURI(data.getData());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    private void DisableForm(boolean b) {

        edtTitle.setEnabled(b);
        edtAuthor.setEnabled(b);
        edtPrice.setEnabled(b);
        edtDescription.setEnabled(b);
    }
}
