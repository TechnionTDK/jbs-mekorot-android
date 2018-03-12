package technion.com.testapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import technion.com.testapplication.R;
import technion.com.testapplication.activities.MakorDetailView;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.models.MakorModel;
import technion.com.testapplication.utils.FontUtils;
import technion.com.testapplication.utils.PreferencesUtils;

/**
 * Created by tomerlevinson on 23/12/2017.
 * Mekorot tab recycler view adapter.
 */
public class MekorotRecyclerViewAdapter
        extends RecyclerView.Adapter<MekorotRecyclerViewAdapter.MekorotViewHolder> {

    private ArrayList<String> mPsukimUris;
    private ArrayList<MakorModel> mMekorotList;
    private Context mContext;
    MekorotTab.MekorotChangesListener mCallback;

    public MekorotRecyclerViewAdapter(ArrayList<String> psukimUris, ArrayList<MakorModel> mekorot,
                                      Context context, MekorotTab.MekorotChangesListener callback) {
        mPsukimUris = psukimUris;
        mMekorotList = mekorot;
        mContext = context;
        mCallback = callback;
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
    public void onBindViewHolder(final MekorotViewHolder holder, int position) {
        final MakorModel makorModel = mMekorotList.get(position);
        final String makorUri = makorModel.getMakorUri();
        holder.mTitle.setText(
                makorModel.getMakorName() + " (" + makorModel.getNumOfPsukimMentions() + ")");
        // Set Makor author if exists
        if (makorModel.getMakorAuthor() != null) {
            holder.mAuthor.setText(makorModel.getMakorAuthor());
        } else {
            holder.mAuthor.setText("");
            holder.mAuthor.setVisibility(View.GONE);
        }
        holder.mText.setText(makorModel.getMakorText());

        // Set makor style if exists
        FontUtils.setTextFont(holder.mText, mContext);

        // Set makor font size
        FontUtils.setTextSize(holder.mText, mContext);

        View.OnClickListener clickListenerForEverythingButFav = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makorDetailViewIntent = new Intent(mContext, MakorDetailView.class);
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_text),
                        holder.mText.getText());
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_author),
                        holder.mAuthor.getText());
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_title),
                        holder.mTitle.getText());
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_uri),
                        makorUri);
                makorDetailViewIntent.putStringArrayListExtra(
                        mContext.getResources().getString(R.string.psukim_uris_extra),
                        mPsukimUris);
                mContext.startActivity(makorDetailViewIntent);
            }
        };
        holder.mText.setOnClickListener(clickListenerForEverythingButFav);
        holder.mAuthor.setOnClickListener(clickListenerForEverythingButFav);
        holder.mTitle.setOnClickListener(clickListenerForEverythingButFav);
        final Set<String> authorTextUriSet = PreferencesUtils.retrieveStoredStringSet(
                mContext.getResources().getString(R.string.favorites_file_name),
                makorModel.getMakorUri(), mContext);
        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (makorModel.getIsClicked()) {
                    makorModel.setIsClicked(false);
                    holder.mLikeButton.setImageDrawable(
                            mContext.getDrawable(R.drawable.ic_favorite_border_black_18dp));
                    PreferencesUtils.deleteStoredDataByKey(
                            mContext.getResources().getString(R.string.favorites_file_name),
                            makorModel.getMakorUri(),
                            mContext);
                    SharedPreferences favoritesFile = PreferencesUtils.getSharedPreferencesByFileName(
                            mContext.getResources().getString(R.string.favorites_file_name),
                            mContext);
                    if (favoritesFile != null && favoritesFile.getAll() != null) {
                        mCallback.updateFavoritesNum(favoritesFile.getAll().size());
                    }
                } else {
                    makorModel.setIsClicked(true);
                    holder.mLikeButton.setImageDrawable(
                            mContext.getDrawable(R.drawable.ic_favorite_black_18dp));
                    Set<String> newParamSet = new HashSet<>();
                    newParamSet.add(mContext.getResources().getString(
                            R.string.favorites_name_prefix) + makorModel.getMakorName());
                    newParamSet.add(mContext.getResources().getString(
                            R.string.favorites_text_prefix) + makorModel.getMakorText());
                    newParamSet.add(mContext.getResources().getString(
                            R.string.favorites_uri_prefix) + makorModel.getMakorUri());
                    PreferencesUtils.storeStringSet(
                            mContext.getResources().getString(R.string.favorites_file_name),
                            makorModel.getMakorUri(),
                            newParamSet, true, mContext);
                    SharedPreferences favoritesFile = PreferencesUtils.getSharedPreferencesByFileName(
                            mContext.getResources().getString(R.string.favorites_file_name),
                            mContext);
                    if (favoritesFile != null && favoritesFile.getAll() != null) {
                        mCallback.updateFavoritesNum(
                                favoritesFile.getAll().size());
                    }
                }
            }
        });
        if (authorTextUriSet != null && authorTextUriSet.size() > 0) {
            holder.mLikeButton.setImageDrawable(
                    mContext.getDrawable(R.drawable.ic_favorite_black_18dp));
        } else {
            holder.mLikeButton.setImageDrawable(
                    mContext.getDrawable(R.drawable.ic_favorite_border_black_18dp));
        }
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
        private ImageView mLikeButton;

        private MekorotViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.makor_name);
            mAuthor = (TextView) itemView.findViewById(R.id.makor_author);
            mText = (TextView) itemView.findViewById(R.id.makor_text);
            mLikeButton = (ImageView) itemView.findViewById(R.id.favorite_icon);
        }
    }
}
