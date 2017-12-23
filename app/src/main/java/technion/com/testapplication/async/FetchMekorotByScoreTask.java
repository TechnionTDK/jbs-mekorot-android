package technion.com.testapplication.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Pair;

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
public class FetchMekorotByScoreTask extends AsyncTask<String, Void, Pair<ArrayList<MakorModel>,ArrayList<String>>> {
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
    protected Pair<ArrayList<MakorModel>, ArrayList<String>> doInBackground(String... params) {
        ArrayList<MakorModel> sortedMekorot = new ArrayList<>();
        ArrayList<String> bookSubjects = new ArrayList<>();
        try {
            QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT, params[0]);
            QueryEngineHTTP queryEngineHTTPCategories = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT, params[1]);
            try {
                ResultSet resultSet = queryEngineHTTP.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    MakorModel makorModel = new MakorModel(
                            rb.get(JBSQueries.MAKOR_NAME).toString(),
                            rb.get(JBSQueries.MAKOR_NAME).toString(),
                            rb.get(JBSQueries.MAKOR_TEXT).toString());
                    sortedMekorot.add(makorModel);
                }
            } finally {
                queryEngineHTTP.close();
            }
            try {
                ResultSet resultSet = queryEngineHTTPCategories.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    String bookSubjectUri = rb.get(JBSQueries.BOOK_SUBJECT).toString();
                    String bookSubject = bookSubjectUri.substring(bookSubjectUri.lastIndexOf("/") + 1);
                    bookSubjects.add(bookSubject);
                }
            } finally {
                queryEngineHTTPCategories.close();
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
        Pair<ArrayList<MakorModel>, ArrayList<String>> queryResultsPair = Pair.create(sortedMekorot, bookSubjects);
        return queryResultsPair;
    }

    @Override
    protected void onPostExecute(Pair<ArrayList<MakorModel>, ArrayList<String>> mekorotModelCategoryPair) {
        super.onPostExecute(mekorotModelCategoryPair);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        ArrayList<MakorModel> mekorotModels = mekorotModelCategoryPair.first;
        ArrayList<String> mekorotCategories = mekorotModelCategoryPair.second;
        if (mActivity instanceof MekorotActivity) {
            ((MekorotActivity) mActivity).setRecyclerViewAdapter(mekorotModels, mekorotCategories);
        }
    }
}
