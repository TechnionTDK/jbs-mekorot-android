package technion.com.testapplication.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.activities.MekorotActivity;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 18/12/2017.
 */
public class FetchMekorotByScoreTask extends AsyncTask<String, Void, ArrayList<MakorModel>> {
    private Activity mActivity;
    private ProgressDialog mProgressDialog;

    public FetchMekorotByScoreTask(Activity activity) {
        mActivity = activity;
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage(mActivity.getResources().getString(R.string.please_wait_he));
        this.mProgressDialog.show();
    }

    @Override
    protected ArrayList<MakorModel> doInBackground(String... params) {
        ArrayList<MakorModel> queryResults = new ArrayList<>();
        try {
            QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT, params[0]);

            try {
                ResultSet resultSet = queryEngineHTTP.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    MakorModel makorModel = new MakorModel(
                            rb.get(JBSQueries.MAKOR_NAME).toString(),
                            rb.get(JBSQueries.MAKOR_NAME).toString(),
                            rb.get(JBSQueries.MAKOR_TEXT).toString());
                    queryResults.add(makorModel);
                }
            } finally {
                queryEngineHTTP.close();
            }


        } catch (Exception err) {
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<MakorModel> mekorotModels) {
        super.onPostExecute(mekorotModels);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mActivity instanceof MekorotActivity) {
            ((MekorotActivity) mActivity).setRecyclerViewAdapter(mekorotModels);
        }
    }
}
