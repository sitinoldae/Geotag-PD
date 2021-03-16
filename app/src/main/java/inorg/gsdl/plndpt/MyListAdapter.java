package inorg.gsdl.plndpt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.plndpt.R;

import java.util.List;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private Context context;
    private List<User> listdata;


    // RecyclerView recyclerView;
    public MyListAdapter(Context context, List<User> listdata) {
        this.context = context;
        this.listdata = listdata;

        for (User ss : listdata) {
            Log.d("mylist", "" + ss.getProject() + " " + ss.getTimestamp());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //  LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User myListData = listdata.get(position);
        holder.textView.setText(myListData.getProject());
        holder.imageView.setText(myListData.getTimestamp());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(view.getContext(), TrackIssueActivity.class);
//                view.getContext().startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        public TextView imageView;
        public TextView textView;
        public LinearLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (TextView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (LinearLayout) itemView.findViewById(R.id.relativeLayout);

            context = itemView.getContext();
        }
    }
}
