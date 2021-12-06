package tolabuth.CartoonBookRealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tolabuth.CartoonBookRealtime.Adapter.CartoonBookAdapter;
import tolabuth.CartoonBookRealtime.model.Cartoon;

public class UpdateActivity extends AppCompatActivity {
    EditText ediUpdateBookName, editUpdateAuthor, editUpdateIssue, editUpdatePublisher;
    Button btnUpdate;
    ImageView imgUpdate;
    private DatabaseReference ref;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int CAMERA_REQUEST_CODE = 103;
    private static final int STORAGE_REQUEST_CODE = 104;
    private static final int IMAGE_PICK_CAMERA_CODE = 105;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 106;
    private StorageTask storageTask;
    String[] cameraPermission ;
    String[] storagePermission;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();
        String bookname = intent.getStringExtra("bookname");
        String author = intent.getStringExtra("author");
        int issue = intent.getIntExtra("issue",0);
        String publisher = intent.getStringExtra("publisher");
        String imgPath = intent.getStringExtra("imageUrl");
        matchview();
        //string permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        //end string permission
        ediUpdateBookName.setText(bookname);
        editUpdateAuthor.setText(author);
        editUpdateIssue.setText(String.valueOf(issue));
        editUpdatePublisher.setText(publisher);
        Picasso.get().load(imgPath).fit().centerCrop().into(imgUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object>map = new HashMap<>();
                map.put("bookname", ediUpdateBookName.getText().toString());
                map.put("author", editUpdateAuthor.getText().toString());
                map.put("issue", Integer.parseInt(editUpdateIssue.getText().toString()));
                map.put("publisher",editUpdatePublisher.getText().toString());



                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cartoonbook");
                storageReference = FirebaseStorage.getInstance().getReference("images");
                databaseReference = FirebaseDatabase.getInstance().getReference("cartoonbook");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cartoonbook");
                                        //String key  = ref.child("cartoonbook").getKey();
                Query query = ref.orderByChild("bookname").equalTo(bookname);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren()){
                            data.getRef().updateChildren(map);
                        }

                        Toast.makeText(UpdateActivity.this, "Update Cartoon Successfully!!!!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpdateActivity.this, "Error: "+error,Toast.LENGTH_SHORT).show();

                    }
                });
                Intent intent1 = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();


            }

        });


        //start hide keyboard
        ediUpdateBookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        editUpdateIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        editUpdatePublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        editUpdateAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        //end hide keyboard
        //update image book to firebase
        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGalleryCamera();
            }
        });


    }

    private void clearText() {
        editUpdatePublisher.setText("");
        editUpdateIssue.setText("");
        editUpdateAuthor.setText("");
        ediUpdateBookName.setText("");
        ediUpdateBookName.requestFocus();
        imgUpdate.setImageResource(R.drawable.books);
    }

    private void pickImageFromGalleryCamera() {
        String [] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //camera click
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        //permission already
                        pickFromCamera();
                    }
                }else if (!checkStoragePermission()){
                    //check storage
                    requestStoragePermission();
                }else {
                    //permission already
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    //pick image from camera
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");
        // put image Uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //image to open camera for image
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    //request Storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(UpdateActivity.this,storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        //check storage permission
        boolean resutl = ActivityCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  resutl;

    }

    //request Camera Permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(UpdateActivity.this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    //check camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    // if permission it true
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && StorageAccept){
                        // both permission allowed
                        pickFromCamera();
                    }else {
                        Toast.makeText(UpdateActivity.this, "Camera && Storage is permission required",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAccepted){
                        //storage permission allowed
                        pickFromGallery();
                    }else {
                        Toast.makeText(UpdateActivity.this, "Storage is permission required ...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            else  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    imgUpdate.setImageURI(resultUri);
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    Toast.makeText(UpdateActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    //hide keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)UpdateActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void matchview() {
        ediUpdateBookName = findViewById(R.id.edt_update_name);
        editUpdateAuthor = findViewById(R.id.edt_update_author);
        editUpdateIssue = findViewById(R.id.edt_update_issue);
        editUpdatePublisher = findViewById(R.id.edt_update_publisher);
        btnUpdate = findViewById(R.id.btn_UpdateCartoon);
        imgUpdate = findViewById(R.id.imgv_update_cartoon);
    }
}