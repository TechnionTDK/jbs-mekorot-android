package technion.com.testapplication.data_manage;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.async.FetchMekorotByScoreTask;

public class MekorotForPsukim extends Cacheable {
    private ArrayList<String> mPsukimList;
    private ArrayList<Object> mMekorotModelCategoryArray;
    private String mMekorotQuery;
    private Fragment mOwnerFrag;
    private boolean mShouldFilter;

    public MekorotForPsukim(ArrayList<String> mPsukimList, Fragment mOwnerFrag, boolean shouldFilter, String mekorotQuery) {
        this.mPsukimList = mPsukimList;
        this.mOwnerFrag = mOwnerFrag;
        mShouldFilter = shouldFilter;
        mMekorotQuery = mekorotQuery;
    }

    @Override
    public String getKey() {
        return "MEKOROT_FOR_PSUKIM_" + mMekorotQuery.toString().hashCode() * (mShouldFilter ? -1 : 1);
    }

    @Override
    public ArrayList<Object> getData() {
        return mMekorotModelCategoryArray;
    }

    @Override
    public void setData(Object data) {
        mMekorotModelCategoryArray = (ArrayList<Object>) data;
    }

    @Override
    public void FetchDataAsync(Runnable onComplete) {
        String categoriesQuery = JBSQueries.getCategoriesByPsukimWithReferenceNumber(
                mPsukimList);
        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(mOwnerFrag, mShouldFilter, this, onComplete);
        fetchMekorotByScoreTask.execute(mMekorotQuery, categoriesQuery);
    }
}
