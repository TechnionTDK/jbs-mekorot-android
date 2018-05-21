package technion.com.testapplication.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.R;
import technion.com.testapplication.models.ResultModel;

public class ResultsCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ResultModel> mResults;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    public ResultsCollectionPagerAdapter(FragmentManager fm, ArrayList<ResultModel> results) {
        super(fm);
        mResults = results;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ResultObjectFragment();
        Bundle args = new Bundle();
        args.putString(ResultObjectFragment.ARG_TEXT, mResults.get(i).Text);
        args.putInt(ResultObjectFragment.ARG_POSITION, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mResults == null ? 0 : mResults.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mResults.get(position).Title;
    }

    public void setData(ArrayList<ResultModel> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    public static class ResultObjectFragment extends Fragment {
        public static final String ARG_TEXT = "result_text";
        public static final String ARG_POSITION = "result_position";
        public TextView MakorTextView;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_result, container, false);
            Bundle args = getArguments();
            assert args != null;
            MakorTextView = rootView.findViewById(R.id.makor_text);
            MakorTextView.setText(args.getString(ARG_TEXT));
            return rootView;
        }
    }
}
