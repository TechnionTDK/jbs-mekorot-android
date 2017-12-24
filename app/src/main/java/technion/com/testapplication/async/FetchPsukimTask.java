package technion.com.testapplication.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.models.PasukModel;
import technion.com.testapplication.activities.PsukimActivity;

/**
 * Created by tomerlevinson on 16/12/2017.
 * Used in order to fetch psukim list of a certain parasha or perek.
 */
public class FetchPsukimTask extends AsyncTask<String, Void, ArrayList<PasukModel>> {
    private Activity mActivity;
    private ProgressDialog mProgressDialog;

    public FetchPsukimTask(Activity activity) {
        mActivity = activity;
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage(
                mActivity.getResources().getString(R.string.please_wait_he));
        this.mProgressDialog.show();
    }

    @Override
    protected ArrayList<PasukModel> doInBackground(String... params) {
        ArrayList<PasukModel> queryResults = new ArrayList<>();
        try {
            com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(
                    params[0]);
            com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, query);

            try {
                ResultSet rs = qexec.execSelect();

                while (rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution();
                    PasukModel pasukModel = new PasukModel(
                            rb.get(JBSQueries.PASUK_TEXT).toString());
                    pasukModel.setUri(rb.get(JBSQueries.PASUK).toString());
                    queryResults.add(pasukModel);
                }
            } finally {
                qexec.close();
            }


        } catch (Exception err) {
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<PasukModel> pasukModelList) {
        super.onPostExecute(pasukModelList);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mActivity instanceof PsukimActivity) {
            ((PsukimActivity) mActivity).setRecyclerViewAdapter(pasukModelList);
        }
    }
}
