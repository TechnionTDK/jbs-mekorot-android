package technion.com.testapplication;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class PsukimRecyclerViewAdapter extends RecyclerView.Adapter<PsukimRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<PasukModel> mPsukim;

    public PsukimRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mPsukim = psukim;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PasukModel pasukModel = mPsukim.get(position);
        holder.mTextView.setText(pasukModel.getText());
        holder.mView.setBackgroundColor(pasukModel.isSelected() ? Color.CYAN : Color.WHITE);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasukModel.setSelected(!pasukModel.isSelected());
                holder.mView.setBackgroundColor(pasukModel.isSelected() ? Color.CYAN : Color.WHITE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPsukim == null? 0: mPsukim.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mTextView;

        private MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.pasuk_text);
        }
    }

}
