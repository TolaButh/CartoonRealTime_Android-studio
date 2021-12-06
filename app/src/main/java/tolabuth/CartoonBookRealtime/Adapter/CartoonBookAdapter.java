package tolabuth.CartoonBookRealtime.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tolabuth.CartoonBookRealtime.MainActivity;
import tolabuth.CartoonBookRealtime.R;
import tolabuth.CartoonBookRealtime.UpdateActivity;
import tolabuth.CartoonBookRealtime.model.Cartoon;

public class CartoonBookAdapter extends RecyclerView.Adapter<CartoonBookAdapter.CartoonBookHoldView> {
    private Context context;
    private List<Cartoon>cartoons;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage mstorage;
    public CartoonBookAdapter(Context context, List<Cartoon> cartoons) {
        this.context = context;
        this.cartoons = cartoons;
    }

    @NonNull
    @Override
    public CartoonBookHoldView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartoon_book_item, parent, false);
        return new CartoonBookHoldView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartoonBookHoldView holder, @SuppressLint("RecyclerView") int position) {
        Cartoon cartoon = cartoons.get(position);
        holder.tvCartoonName.setText(cartoon.getBookname());
        holder.tvAuthorName.setText(cartoon.getAuthor());
        holder.tvIssueNo.setText(String.valueOf(cartoon.getIssue()));
        holder.tvPublisherName.setText(cartoon.getPublisher());
        String name = cartoon.getBookname();
        Picasso.get().load(cartoon.getImageUrl()).fit().centerCrop().into(holder.imgvProfile);

        holder.imgvMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_event, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_update:
//                                final DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
//                                        .setContentHolder(new ViewHolder(R.layout.activity_update))
//                                        .setExpanded(true, 750)
//                                        .create();
//                               // dialogPlus.show();
//                                dialogPlus.show();
//                                View view1 = dialogPlus.getHolderView();
//                               // ImageView imageUpdate = view1.findViewById(R.id.imgv_update_cartoon);
//                                //set Image from gallery and Camera
//                                EditText edtBookname = view1.findViewById(R.id.edt_update_name);
//                                EditText edtAuthor =view1.findViewById(R.id.edt_update_author);
//                                EditText edtIssue = view1.findViewById(R.id.edt_update_issue);
//                                EditText edtpublisher = view1.findViewById(R.id.edt_update_publisher);
//                                Button btnUpdate = view1.findViewById(R.id.btn_UpdateCartoon);
//
//                                edtpublisher.setText(cartoon.getPublisher());
//                                edtAuthor.setText(cartoon.getAuthor());
//                                edtBookname.setText(cartoon.getBookname());
//                                edtIssue.setText(String.valueOf(cartoon.getIssue()));
//                                //Picasso.get().load(cartoon.getImageUrl()).fit().centerInside().into(imageUpdate);
//                                btnUpdate.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Map<String, Object>map = new HashMap<>();
//                                        map.put("bookname", edtBookname.getText().toString());
//                                        map.put("author", edtAuthor.getText().toString());
//                                        map.put("issue",Integer.parseInt(edtIssue.getText().toString()));
//                                        map.put("publisher",edtpublisher.getText().toString());
//
//                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cartoonbook");
//                                        //String key  = ref.child("cartoonbook").getKey();
//                                        Query query = ref.orderByChild("bookname").equalTo(name);
//                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                for (DataSnapshot data:snapshot.getChildren()){
//                                                    data.getRef().updateChildren(map);
//                                                }
//                                                Toast.makeText(context, "Update is Successful",Toast.LENGTH_SHORT).show();
//                                                dialogPlus.dismiss();
//                                            }
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//                                            }
//                                        });
//                                    }
//                                });

//                                View view1 = LayoutInflater.from(context).inflate(R.layout.cartoon_book_item, null);
//                                Intent intent  = new Intent(view1.getContext(), UpdateActivity.class);
                            Intent intent = new Intent(context.getApplicationContext() , UpdateActivity.class);
                            intent.putExtra("bookname",cartoon.getBookname());
                            intent.putExtra("author", cartoon.getAuthor());
                            intent.putExtra("issue", cartoon.getIssue());
                            intent.putExtra("publisher", cartoon.getPublisher());
                            intent.putExtra("imageUrl",cartoon.getImageUrl());
                            context.startActivity(intent);

                                break;

                            case R.id.menu_delete:
                                View view = LayoutInflater.from(context).inflate(R.layout.cartoon_book_item, null);
                                AlertDialog.Builder builder  =new AlertDialog.Builder(context);
                                builder.setTitle("Are you sure?");
                                builder.setIcon(R.drawable.ic_wairning);
                                builder.setMessage("Delete cartoon from your firebase database.");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("cartoonbook");


                                        Query query = ref.orderByChild("bookname").equalTo(name);

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot data : snapshot.getChildren()){
                                                    data.getRef().removeValue();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        Toast.makeText(view.getContext(), "Delete Successfully!!!", Toast.LENGTH_SHORT).show();
 //test  image
//
//                                        storageReference = FirebaseStorage.getInstance().getReference("images");
//                                        databaseReference = FirebaseDatabase.getInstance().getReference("cartoonbook");
//                                Cartoon cartoon1 = cartoons.get(position);
//                                String selectkey = cartoon1.getkey();
//                                Toast.makeText(view.getContext(), "Delete item successfull!!!"+selectkey,Toast.LENGTH_SHORT).show();
//                                StorageReference imageRef = mstorage.getReferenceFromUrl(cartoon.getImageUrl());
//                                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                    databaseReference.child(selectkey).removeValue();
//                                    Toast.makeText(view.getContext(), "Delete item successfull!!!",Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(view.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.create().show();
                                break;

                            default:
                                throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                        }
                        return false;


                    }

                });
                popupMenu.show();

            }
        });
    }



    @Override
    public int getItemCount() {
        return cartoons.size();
    }

    public class    CartoonBookHoldView extends RecyclerView.ViewHolder {
        TextView tvCartoonName;
        TextView tvAuthorName;
        TextView tvPublisherName;
        TextView tvIssueNo;
        ImageView imgvMore,imgvProfile;


        public CartoonBookHoldView(@NonNull View itemView) {
            super(itemView);
            tvCartoonName = itemView.findViewById(R.id.tv_cartoon_book_name);
            tvAuthorName = itemView.findViewById(R.id.tv_autor_name);
            tvIssueNo = itemView.findViewById(R.id.tv_issue_no);
            tvPublisherName = itemView.findViewById(R.id.tv_publisher);
            imgvMore = itemView.findViewById(R.id.imgv_more_menu);
            imgvProfile = itemView.findViewById(R.id.img_cartoon_book);
        }
    }
}
