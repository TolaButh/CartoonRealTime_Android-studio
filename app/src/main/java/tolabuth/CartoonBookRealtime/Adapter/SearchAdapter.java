package tolabuth.CartoonBookRealtime.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;

import tolabuth.CartoonBookRealtime.R;
import tolabuth.CartoonBookRealtime.model.Cartoon;

public class SearchAdapter extends FirebaseRecyclerAdapter<Cartoon,SearchAdapter.SearchCartoonHolder> {

     /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchAdapter(@NonNull FirebaseRecyclerOptions<Cartoon> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchCartoonHolder holder, int position, @NonNull Cartoon model) {
        holder.tvAuthorName.setText(model.getAuthor());
        holder.tvCartoonName.setText(model.getBookname());
        holder.tvPublisherName.setText(model.getPublisher());
        holder.tvIssueNo.setText(model.getIssue());

    }

    @NonNull
    @Override
    public SearchCartoonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartoon_book_item,parent, false);
        return new SearchCartoonHolder(view);
    }

    public class SearchCartoonHolder extends RecyclerView.ViewHolder {
        TextView tvCartoonName;
        TextView tvAuthorName;
        TextView tvPublisherName;
        TextView tvIssueNo;
        public SearchCartoonHolder(@NonNull View itemView) {
            super(itemView);
            tvCartoonName = itemView.findViewById(R.id.tv_cartoon_book_name);
            tvAuthorName = itemView.findViewById(R.id.tv_autor_name);
            tvIssueNo = itemView.findViewById(R.id.tv_issue_no);
            tvPublisherName = itemView.findViewById(R.id.tv_publisher);
        }
    }
}
