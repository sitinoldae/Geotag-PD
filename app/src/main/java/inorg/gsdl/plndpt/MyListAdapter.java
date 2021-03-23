package inorg.gsdl.plndpt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plndpt.R;

import java.util.List;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final List<User> listdata;


    // RecyclerView recyclerView;
    public MyListAdapter(List<User> listdata) {
        this.listdata = listdata;

        for (User ss : listdata) {
            Log.d("mylist", "" + ss.getProject() + " " + ss.getTimestamp());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User myListData = listdata.get(position);
        holder.textView.setText(myListData.getProject());
        holder.imageView.setText(myListData.getTimestamp());
        holder.relativeLayout.setOnClickListener(view -> {

//                Intent intent = new Intent(view.getContext(), TrackIssueActivity.class);
//                view.getContext().startActivity(intent);

        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView imageView;
        public TextView textView;
        public LinearLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
