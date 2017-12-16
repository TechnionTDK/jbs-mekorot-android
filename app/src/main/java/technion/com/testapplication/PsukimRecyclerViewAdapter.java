package technion.com.testapplication;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class PsukimRecyclerViewAdapter extends RecyclerView.Adapter<PsukimRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<PasukModel> mPsukim;

    private boolean allItemsClicked = false;

    public PsukimRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mPsukim = psukim;
    }

    public void clickOnAllItems(boolean setSelected) {
        for (PasukModel pasukModel: mPsukim) {
            pasukModel.setSelected(setSelected);
        }
        notifyDataSetChanged();
        allItemsClicked = setSelected;
    }

    public boolean getAreAllItemsClicked() {
        return allItemsClicked;
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
        if (pasukModel.isSelected()) {
            holder.mImageView.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasukModel.setSelected(!pasukModel.isSelected());
                holder.mView.setBackgroundColor(pasukModel.isSelected() ? Color.CYAN : Color.WHITE);
                if (pasukModel.isSelected()) {
                    holder.mImageView.setImageResource(R.drawable.ic_check_box_black_24dp);
                } else {
                    holder.mImageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
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
        private ImageView mImageView;

        private MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.pasuk_text);
            mImageView = (ImageView) itemView.findViewById(R.id.pasuk_selection);
        }
    }

}
