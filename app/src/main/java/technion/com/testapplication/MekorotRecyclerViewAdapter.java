package technion.com.testapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 23/12/2017.
 */
public class MekorotRecyclerViewAdapter
        extends RecyclerView.Adapter<MekorotRecyclerViewAdapter.MekorotViewHolder> {

    private ArrayList<MakorModel> mMekorotList;

    public MekorotRecyclerViewAdapter(ArrayList<MakorModel> mekorot) {
        mMekorotList = mekorot;
    }

    public int getMekorotSize() {
        return mMekorotList.size();
    }

    @Override
    public MekorotViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_row_mekorot, parent, false);
        return new MekorotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MekorotViewHolder holder, int position) {
        final MakorModel makorModel = mMekorotList.get(position);
        holder.mTitle.setText(makorModel.getMakorName());
        holder.mAuthor.setText(makorModel.getMakorAuthor());
        holder.mText.setText(makorModel.getMakorText());
    }

    @Override
    public int getItemCount() {
        return mMekorotList == null ? 0 : mMekorotList.size();
    }

    public class MekorotViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mTitle;
        private TextView mAuthor;
        private TextView mText;

        private MekorotViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.makor_name);
            mAuthor = (TextView) itemView.findViewById(R.id.makor_author);
            mText = (TextView) itemView.findViewById(R.id.makor_text);
        }
    }
}
