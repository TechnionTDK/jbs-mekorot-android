package technion.com.testapplication.data_manage;


import android.support.v4.app.Fragment;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.async.FetchPsukimTask;
import technion.com.testapplication.models.PasukModel;

public class PsukimForParashaPerek extends Cacheable {

    private String mParashaUri;
    private ArrayList<PasukModel> mPsukim;
    private Fragment mOwnerFrag;

    public PsukimForParashaPerek(String parashaUri, Fragment ownerFrag) {
        this.mParashaUri = parashaUri;
        mPsukim = null;
        mOwnerFrag = ownerFrag;
    }

    @Override
    public String getKey() {
        return "PSUKIM_FOR_PARASHA_" + mParashaUri;
    }

    @Override
    public ArrayList<PasukModel> getData() {
        return mPsukim;
    }

    @Override
    public void setData(Object data) {
        mPsukim = (ArrayList<PasukModel>) data;
    }

    @Override
    public void FetchDataAsync(Runnable onComplete) {
        String psukimByParashaQuery = JBSQueries.getAllPsukimFromParashaQuery(mParashaUri);
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(mOwnerFrag, onComplete, this);
        fetchPsukimTask.execute(psukimByParashaQuery);
    }
}
