package tolabuth.CartoonBookRealtime;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.getSystemServiceName;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import tolabuth.CartoonBookRealtime.model.Cartoon;

public class AddFragment extends Fragment {
    private EditText edtBookName, edtBookAuthor, edtBookIssue, edtBookPublisher;
    private MaterialButton btnAddBook;
    private DatabaseReference ref;
    private ImageView imageProfile;
    //about firebase
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int CAMERA_REQUEST_CODE = 103;

    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 12345;
    private final int CAMERA_PICTURE_REQUEST_CODE = 56789;
    private StorageTask storageTask;

    String[] cameraPermission;
    String[] storagePermission;
    private Uri filePath = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        // ref = FirebaseDatabase.getInstance().getReference("cartoonbook");
        storageReference = FirebaseStorage.getInstance().getReference("images");
        databaseReference = FirebaseDatabase.getInstance().getReference("cartoonbook");

        matchView(view);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookname = edtBookName.getText().toString();
                String author = edtBookAuthor.getText().toString();
                String publisher = edtBookPublisher.getText().toString();
                String strIssue = edtBookIssue.getText().toString();
                if (bookname.isEmpty() || author.isEmpty() || publisher.isEmpty() || strIssue.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill data full", Toast.LENGTH_LONG).show();
                    edtBookName.requestFocus();

                } else {
//                        int issue = Integer.parseInt(strIssue);
//                        String id = ref.push().getKey();
//                        Cartoon cartoon = new Cartoon(bookname, author, publisher,issue);
//                        ref.child(id).setValue(cartoon);
//                        clearText();

                    //start test add
                    UploadFilesWithData();
                }
            }
        });
        //chose image from gallery and camera
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickDialog();
            }
        });
        //hide keyboad editText

        edtBookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        edtBookAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        edtBookIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        edtBookPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });
        //emd key bod


        return view;
    }

    private void imagePickDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Image");
        builder.setMessage("Please select an option");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCameraPermission();
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectFromGallery();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_GALLERY_CODE);

    }

    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults[1] ==PackageManager.PERMISSION_GRANTED){
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_PICTURE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode  ==  PICK_IMAGE_GALLERY_CODE && resultCode == RESULT_OK) {
            if(data == null || data.getData() == null)
                return;

            try {
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageProfile.setImageBitmap(bitmap);
            }catch (Exception e) {

            }
        } else if(requestCode == CAMERA_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap  = (Bitmap)extras.get("data");
            imageProfile.setImageBitmap(bitmap);
            filePath =getImageUri(getActivity().getApplicationContext(), bitmap);

        
        }
    }
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
        return Uri.parse(path);
    }
    private void UploadFilesWithData() {

        if (filePath != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() +
                    ".jpeg");
            fileReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String mUrl = uri.toString();
                            String strIssue = edtBookIssue.getText().toString();
                            int issue = Integer.parseInt(strIssue);
                            Cartoon cartoon = new Cartoon(edtBookName.getText().toString(), edtBookAuthor.getText().toString(),
                                    edtBookPublisher.getText().toString(),issue, mUrl);
                            String id = databaseReference.push().getKey();
                            databaseReference.child(id).setValue(cartoon);
                            Toast.makeText(getContext(), "Uploading Successfully!! ", Toast.LENGTH_SHORT).show();
                            clearText();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());


                }
            });


        } else {
            Toast.makeText(getContext(), "No file Selected", Toast.LENGTH_SHORT).show();
        }

    }

//hide keyboard
public void hideKeyboard(View view) {
    InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}



    private void clearText() {
        edtBookPublisher.setText("");
        edtBookName.setText("");
        edtBookIssue.setText("");
        edtBookAuthor.setText("");
        edtBookName.requestFocus();
        imageProfile.setImageResource(R.drawable.books);
    }


    private void matchView(View view) {
        edtBookAuthor = view.findViewById(R.id.edt_book_author);
        edtBookIssue = view.findViewById(R.id.edt_book_issue);
        edtBookName = view.findViewById(R.id.edt_book_name);
        edtBookPublisher = view.findViewById(R.id.edt_book_publisher);
        btnAddBook = view.findViewById(R.id.btn_addCartoon);
        imageProfile =view.findViewById(R.id.imgv_add_cartoon);

    }
}
