package technion.com.testapplication.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.models.CategoryModel;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 18/12/2017.
 * This class is intended for fetching mekorot by task.
 */
public class FetchMekorotByScoreTask
        extends AsyncTask<String, Void, Pair<ArrayList<MakorModel>, ArrayList<CategoryModel>>> {
    private Fragment mFragment;
    private boolean mShouldFilter;
    private ProgressDialog mProgressDialog;
    private static final String NUM_OF_REFERENCES_REGEX = "^";
    private static final String BOOK_URI_SEPARATOR = "/";

    public FetchMekorotByScoreTask(Fragment fragment, boolean shouldFilter) {
        mShouldFilter = shouldFilter;
        mFragment = fragment;
        mProgressDialog = new ProgressDialog(mFragment.getContext());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage(
                mFragment.getContext().getResources().getString(R.string.please_wait_he));
        this.mProgressDialog.show();
    }

    @Override
    protected Pair<ArrayList<MakorModel>, ArrayList<CategoryModel>> doInBackground(
            String... params) {
        ArrayList<MakorModel> sortedMekorot = new ArrayList<>();
        ArrayList<CategoryModel> bookSubjects = new ArrayList<>();
        try {
            QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                    params[0]);
            QueryEngineHTTP queryEngineHTTPCategories = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                    params[1]);
            try {
                ResultSet resultSet = queryEngineHTTP.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    String numOfPsukimString;
                    String numOfPsukimLiteralAsString;
                    String makorName = rb.get(JBSQueries.MAKOR_NAME).toString();
                    String makorText = rb.get(JBSQueries.MAKOR_TEXT).toString();
                    String makorUri;
                    if (mShouldFilter) {
                        numOfPsukimLiteralAsString = rb.get(
                                JBSQueries.NUM_OF_PSUKIM_AS_SUM).toString();
                        numOfPsukimString = numOfPsukimLiteralAsString.substring(0,
                                numOfPsukimLiteralAsString.indexOf(NUM_OF_REFERENCES_REGEX));
                        makorUri = rb.get(JBSQueries.MAKOR_SOURCE_URI).toString();
                    } else {
                        numOfPsukimLiteralAsString = rb.get(
                                JBSQueries.NUM_OF_PSUKIM).toString();
                        numOfPsukimString = numOfPsukimLiteralAsString.substring(0,
                                numOfPsukimLiteralAsString.indexOf(NUM_OF_REFERENCES_REGEX));

                        makorUri = rb.get(JBSQueries.MAKOR).toString();
                    }
                    MakorModel makorModel = new MakorModel(
                            makorName,
                            makorName,
                            makorText,
                            makorUri,
                            numOfPsukimString);
                    sortedMekorot.add(makorModel);
                }
            } finally {
                queryEngineHTTP.close();
            }
            try {
                ResultSet resultSet = queryEngineHTTPCategories.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    String bookSubjectUri = rb.get(JBSQueries.CATEGORY).toString();
                    String referenceNumNodeAsString = rb.get(
                            JBSQueries.CATEGORY_REFERENCE_NUM).toString();
                    String bookReferenceNum = referenceNumNodeAsString.substring(0,
                            referenceNumNodeAsString.indexOf(
                                    NUM_OF_REFERENCES_REGEX));
                    String bookSubject = bookSubjectUri.substring(
                            bookSubjectUri.lastIndexOf(BOOK_URI_SEPARATOR) + 1);
                    CategoryModel categoryModel = new CategoryModel(bookSubject, bookReferenceNum);
                    bookSubjects.add(categoryModel);
                }
            } finally {
                queryEngineHTTPCategories.close();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        Pair<ArrayList<MakorModel>, ArrayList<CategoryModel>> queryResultsPair = Pair.create(
                sortedMekorot,
                bookSubjects);
        return queryResultsPair;
    }

    @Override
    protected void onPostExecute(
            Pair<ArrayList<MakorModel>, ArrayList<CategoryModel>> mekorotModelCategoryPair) {
        super.onPostExecute(mekorotModelCategoryPair);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        ArrayList<MakorModel> mekorotModels = mekorotModelCategoryPair.first;
        ArrayList<CategoryModel> mekorotCategories = mekorotModelCategoryPair.second;
        if (mFragment instanceof MekorotTab) {
            ((MekorotTab) mFragment).setRecyclerViewAdapter(mekorotModels, mekorotCategories);
        }
    }
}
