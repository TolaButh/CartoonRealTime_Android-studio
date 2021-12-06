package tolabuth.CartoonBookRealtime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tolabuth.CartoonBookRealtime.Adapter.CartoonBookAdapter;
import tolabuth.CartoonBookRealtime.model.Cartoon;

public class HomeFragment extends Fragment {
    private List<Cartoon>cartoons;
    private CartoonBookAdapter adapter;
    private DatabaseReference reference;
    private RecyclerView rcvCartoon;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container, false);
        cartoons = new ArrayList<Cartoon>();
        reference = FirebaseDatabase.getInstance().getReference("cartoonbook");
        matchView(view);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartoons.clear();
                for (DataSnapshot data: snapshot.getChildren()){
                    Cartoon cartoon = data.getValue(Cartoon.class);
                    cartoons.add(cartoon);

                }
                adapter = new CartoonBookAdapter(getContext(), cartoons);
                rcvCartoon.setAdapter(adapter);
                rcvCartoon.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    private void matchView(View view) {
        rcvCartoon  = view.findViewById(R.id.rcv_cartoon_book);
    }
}
