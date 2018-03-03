package technion.com.testapplication.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.R;
import technion.com.testapplication.models.PasukModel;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class PsukimRecyclerViewAdapter
        extends RecyclerView.Adapter<PsukimRecyclerViewAdapter.PsukimViewHolder> {

    private ArrayList<PasukModel> mPsukim;
    private ArrayList<String> mSelectedUris = new ArrayList<>();

    private boolean allItemsClicked = false;

    public PsukimRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mPsukim = psukim;
    }

    public void clickOnAllItems(boolean setSelected) {
        mSelectedUris.clear();
        for (PasukModel pasukModel : mPsukim) {
            pasukModel.setSelected(setSelected);
            if (setSelected) {
                mSelectedUris.add(pasukModel.getUri());
            }
        }
        notifyDataSetChanged();
        allItemsClicked = setSelected;
    }

    public ArrayList<String> getAllPsukimUris() {
        return mSelectedUris;
    }

    public boolean getAreAllItemsClicked() {
        return allItemsClicked;
    }

    @Override
    public PsukimViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_row_psukim, parent, false);
        return new PsukimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PsukimViewHolder holder, int position) {
        final PasukModel pasukModel = mPsukim.get(position);
        String pasukText = pasukModel.getText();
        String pasukLabel = pasukModel.getLabel();
        String pasukCombined = pasukLabel + " " + pasukText;
        SpannableString spannableString=  new SpannableString(pasukCombined);
        spannableString.setSpan(new RelativeSizeSpan(0.60f), 0, pasukLabel.length(), 0);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, pasukLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.mTextView.setText(spannableString);
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
                String pasukUri = pasukModel.getUri();
                if (pasukModel.isSelected()) {
                    holder.mImageView.setImageResource(R.drawable.ic_check_box_black_24dp);
                    mSelectedUris.add(pasukUri);
                } else {
                    holder.mImageView.setImageResource(
                            R.drawable.ic_check_box_outline_blank_black_24dp);
                    mSelectedUris.remove(pasukUri);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPsukim == null ? 0 : mPsukim.size();
    }

    public class PsukimViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mTextView;
        private ImageView mImageView;

        private PsukimViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.pasuk_text);
            mImageView = (ImageView) itemView.findViewById(R.id.pasuk_selection);
        }
    }

}
