package technion.com.testapplication.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.data_manage.Cacheable;
import technion.com.testapplication.models.PasukModel;

/**
 * Created by tomerlevinson on 16/12/2017.
 * Used in order to fetch psukim list of a certain parasha or perek.
 */
public class FetchPsukimTask extends AsyncTask<String, Void, ArrayList<PasukModel>> {
    private Fragment mPsukimFrag;
    private ProgressDialog mProgressDialog;
    private Runnable mOnComplete;
    private Cacheable mCaller;
    private static final String NUM_OF_REFERENCES_REGEX = "^";

    public FetchPsukimTask(Fragment frag, Runnable onComplete, Cacheable caller) {
        mPsukimFrag = frag;
        mProgressDialog = new ProgressDialog(frag.getContext());
        mOnComplete = onComplete;
        mCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage(
                mPsukimFrag.getContext().getResources().getString(R.string.please_wait_he));
        this.mProgressDialog.show();
    }

    @Override
    protected ArrayList<PasukModel> doInBackground(String... params) {
        ArrayList<PasukModel> queryResults = new ArrayList<>();
        try
        {
            QueryEngineHTTP psukimQuery = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT, params[0]);

            try
            {
                ResultSet resultSet = psukimQuery.execSelect();
                while (resultSet.hasNext())
                {
                    QuerySolution rb = resultSet.nextSolution();
                    String numOfMekorot = rb.get("numOfMekorot").toString();
                    numOfMekorot = numOfMekorot.substring(0, numOfMekorot.indexOf(NUM_OF_REFERENCES_REGEX));
                    String pasukLabel = rb.get(JBSQueries.PASUK_LABEL).toString();
                    int indexOfSecondWord = pasukLabel.indexOf(" ") + 1;
                    String perekParashaOfPasuk = pasukLabel.substring(indexOfSecondWord);
                    PasukModel pasukModel = new PasukModel(
                            rb.get(JBSQueries.PASUK_TEXT).toString() + " (" + numOfMekorot + ")", perekParashaOfPasuk);
                    pasukModel.setUri(rb.get(JBSQueries.PASUK).toString());
                    queryResults.add(pasukModel);
                }
            } finally
            {
                psukimQuery.close();
            }

        } catch (Exception err)
        {
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<PasukModel> pasukModelList) {
        super.onPostExecute(pasukModelList);
        if (mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
        mCaller.setData(pasukModelList);
        if (mOnComplete != null)
        {
            mOnComplete.run();
        }
    }
}
