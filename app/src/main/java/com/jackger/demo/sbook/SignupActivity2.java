package com.jackger.demo.sbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class SignupActivity2 extends AppCompatActivity {

    private ImageView imgAvatar, imgBack;
    private Button btnGallery, btnCamera, btnSignup;

    private User user;

    final int REQUEST_CODE_IMAGE = 1;

    final int IMAGE_PICK_CODE = 1000;
    final int PERMISSION_CODE = 1001;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        //Lấy dữ liệu user gửi từ màn hình signup
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            user = (User) bundle.getSerializable("user");
        }

        //Ánh xạ view
        AnhXaView();


        //Kết nối firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://sbook-6be8c.appspot.com");


        //Kết nối firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Bắt sự kiện click button camera
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);

            }
        });

        //Bắt sự kiện click chọn Gallery
        btnGallery.setOnClickListener(new View.OnClickListener() {
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

        //Bắt sự kiện click chọn button Đăng ký
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tạo biến calendar lưu tên ảnh tránh bị trùng lặp
                Calendar calendar = Calendar.getInstance();

                //Tạo tên ảnh khi lưu storage
                StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

                // Get the data from an ImageView as bytes
                imgAvatar.setDrawingCacheEnabled(true);
                imgAvatar.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
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

                                //Gán linkAvatar vào user
                                user.setLinkavatar(url);

                                //Thêm user vào database
                                mDatabase.child("USERS").push().setValue(user, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                        if (databaseError == null) {

                                            Toast.makeText(SignupActivity2.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                            //Gán giá trị cho UserMain
                                            UserMain.setUsername(user.getUsername());
                                            UserMain.setPhone(user.getPhone());
                                            UserMain.setEmail(user.getEmail());
                                            UserMain.setAddress(user.getAddress());
                                            UserMain.setPassword(user.getPassword());
                                            UserMain.setLinkavatar(user.getLinkavatar());

                                            //Chuyển đến màn hình Home
                                            Intent intent = new Intent(SignupActivity2.this, HomeActivity.class);//
                                            startActivity(intent);


                                        } else {

                                            Toast.makeText(SignupActivity2.this, "Đăng ký thất bại. Vui lòng đăng ký lại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                    }
                });

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Chuyển về màn hình Login
                Intent intent = new Intent(SignupActivity2.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    //Pick image from Gallery
    private void PickImageGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //Ánh xạ view
    private void AnhXaView() {

        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnSignup = (Button) findViewById(R.id.btnSignup);
    }

    //Xử lý kết quả ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //camera
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {

            //Get ảnh chụp bằng camera dưới dạng bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //Hiển thị ảnh vừa chụp lên imgAvatar
            imgAvatar.setImageBitmap(bitmap);
        }

        //gallery
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {

            //Hiển thị ảnh chọn lên imgAvatar
            imgAvatar.setImageURI(data.getData());
        }

        super.onActivityResult(requestCode, resultCode, data);
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
}
