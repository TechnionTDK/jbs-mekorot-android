package technion.com.testapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import technion.com.testapplication.R;
import technion.com.testapplication.activities.MakorFavoriteView;
import technion.com.testapplication.fragments.FavoritesTab;
import technion.com.testapplication.utils.FontUtils;
import technion.com.testapplication.utils.PreferencesUtils;

/**
 * Created by tomerlevinson on 03/03/2018.
 * Favorites tab recycler view adapter.
 */
public class FavoritesRecyclerViewAdapter
        extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoritesViewHolder> {

    private Context mContext;
    private HashMap<String, Set<String>> mFavorites;
    FavoritesTab.FavoritesChangeListener mCallback;
    private List<Pair<String, Pair<String, String>>> mFavoritesPairs = new ArrayList<>();

    public FavoritesRecyclerViewAdapter(Context context, HashMap<String, Set<String>> favorites,
                                        FavoritesTab.FavoritesChangeListener callback) {
        mContext = context;
        mFavorites = favorites;
        mCallback = callback;
        Iterator it = mFavorites.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Set<String> pairSet = (Set<String>) pair.getValue();
            String makorName = "";
            String makorUri = "";
            String makorText = "";
            // Check for name
            if (pairSet.toArray()[0].toString().contains(mContext.getResources().getString(
                    R.string.favorites_name_prefix))) {
                makorName = pairSet.toArray()[0].toString().substring(7);
            } else if (pairSet.toArray()[1].toString().contains(mContext.getResources().getString(
                    R.string.favorites_name_prefix))) {
                makorName = pairSet.toArray()[1].toString().substring(7);
            } else if (pairSet.toArray()[2].toString().contains(mContext.getResources().getString(
                    R.string.favorites_name_prefix))) {
                makorName = pairSet.toArray()[2].toString().substring(7);
            }
            // Check for text
            if (pairSet.toArray()[0].toString().contains(mContext.getResources().getString(
                    R.string.favorites_text_prefix))) {
                makorText = pairSet.toArray()[0].toString().substring(7);
            } else if (pairSet.toArray()[1].toString().contains(mContext.getResources().getString(
                    R.string.favorites_text_prefix))) {
                makorText = pairSet.toArray()[1].toString().substring(7);
            } else if (pairSet.toArray()[2].toString().contains(mContext.getResources().getString(
                    R.string.favorites_text_prefix))) {
                makorText = pairSet.toArray()[2].toString().substring(7);
            }
            // Check for uri
            if (pairSet.toArray()[0].toString().contains(mContext.getResources().getString(
                    R.string.favorites_uri_prefix))) {
                makorUri = pairSet.toArray()[0].toString().substring(6);
            } else if (pairSet.toArray()[1].toString().contains(mContext.getResources().getString(
                    R.string.favorites_uri_prefix))) {
                makorUri = pairSet.toArray()[1].toString().substring(6);
            } else if (pairSet.toArray()[2].toString().contains(mContext.getResources().getString(
                    R.string.favorites_uri_prefix))) {
                makorUri = pairSet.toArray()[2].toString().substring(6);
            }
            mFavoritesPairs.add(Pair.create(makorUri, Pair.create(makorName, makorText)));
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_row_favorites, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoritesViewHolder holder, final int position) {
        final Pair<String, Pair<String, String>> favoritesPair = mFavoritesPairs.get(position);
        holder.mTitle.setText(
                favoritesPair.second.first);
        holder.mText.setText(favoritesPair.second.second);

        // Set makor style if exists
        FontUtils.setTextFont(holder.mText, mContext);

        // Set makor font size
        FontUtils.setTextSize(holder.mText, mContext);

        View.OnClickListener clickListenerForEverythingButFav = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makorDetailViewIntent = new Intent(mContext, MakorFavoriteView.class);
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_text),
                        holder.mText.getText());
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_title),
                        holder.mTitle.getText());
                makorDetailViewIntent.putExtra(
                        mContext.getResources().getString(R.string.makor_uri),
                        favoritesPair.first);
                mContext.startActivity(makorDetailViewIntent);
            }
        };
        holder.mText.setOnClickListener(clickListenerForEverythingButFav);
        holder.mTitle.setOnClickListener(clickListenerForEverythingButFav);
        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtils.deleteStoredDataByKey(
                        mContext.getResources().getString(R.string.favorites_file_name),
                        favoritesPair.first, mContext);
                mFavoritesPairs.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mFavoritesPairs.size());
                notifyDataSetChanged();
                mCallback.setFavoriteTabResultsNum(mFavoritesPairs.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFavoritesPairs == null ? 0 : mFavoritesPairs.size();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mTitle;
        private TextView mText;
        private ImageView mLikeButton;

        private FavoritesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.makor_name);
            mText = (TextView) itemView.findViewById(R.id.makor_text);
            mLikeButton = (ImageView) itemView.findViewById(R.id.favorite_icon);
        }
    }
}