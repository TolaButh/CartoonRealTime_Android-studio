package tolabuth.CartoonBookRealtime;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tolabuth.CartoonBookRealtime.Adapter.CartoonBookAdapter;
import tolabuth.CartoonBookRealtime.Adapter.SearchAdapter;
import tolabuth.CartoonBookRealtime.model.Cartoon;

public class SearchFragment extends Fragment {
    private AutoCompleteTextView edtSearch;
    private MaterialButton btnSearch;
    private RecyclerView rcvSearch;
    private DatabaseReference ref;
    private List<Cartoon>cartoons;
    private CartoonBookAdapter adapter;
    private SearchAdapter searchAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container, false);
        ref  = FirebaseDatabase.getInstance().getReference("cartoonbook");
        cartoons = new ArrayList<Cartoon>();
        matchView(view);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search= edtSearch.getText().toString();
                Query query = ref.orderByChild("author").startAt(search).endAt(search+"~");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartoons.clear();
                        for (DataSnapshot data: snapshot.getChildren()){
                            Cartoon cartoon = data.getValue(Cartoon.class);
                            cartoons.add(cartoon);
                        }
                        adapter = new CartoonBookAdapter(getContext(), cartoons);
                        rcvSearch.setAdapter(adapter);
                        rcvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        edtSearch.setText("");

    //hide keyboard
//        edtSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideKeyboard(view);
//            }
//        });
        return view;
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void matchView(View view) {
        edtSearch = view.findViewById(R.id.edt_search);
        btnSearch = view.findViewById(R.id.btn_search);
        rcvSearch = view.findViewById(R.id.rcv_search);

    }
}
