package technion.com.testapplication.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        extends
        AsyncTask<String, Void, Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>>> {
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
    protected Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>> doInBackground(
            String... params) {
        HashMap<String, MakorModel> sortedMekorot = new HashMap<>();
        ArrayList<CategoryModel> bookSubjects = new ArrayList<>();
        ArrayList<String> mekorotUris = new ArrayList<>();
        HashMap<String, String> mekorotUrisAuthors = new HashMap<>();
        try {
            QueryEngineHTTP sortedMekorotQuery = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                    params[0]);
            QueryEngineHTTP queryEngineHTTPCategories = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                    params[1]);
            try {
                ResultSet resultSet = sortedMekorotQuery.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    String numOfPsukimString;
                    String makorName = rb.get(JBSQueries.MAKOR_NAME).toString();
                    String makorText = rb.get(JBSQueries.MAKOR_TEXT).toString();
                    String makorUri;
                    String numOfPsukimLiteralAsString = rb.get(
                            JBSQueries.NUM_OF_PSUKIM).toString();
                    numOfPsukimString = numOfPsukimLiteralAsString.substring(0,
                            numOfPsukimLiteralAsString.indexOf(NUM_OF_REFERENCES_REGEX));
                    if (mShouldFilter) {
                        makorUri = rb.get(JBSQueries.MAKOR_SOURCE_URI).toString();
                    } else {
                        makorUri = rb.get(JBSQueries.MAKOR).toString();
                    }
                    mekorotUris.add(makorUri);
                    MakorModel makorModel = new MakorModel(
                            makorName,
                            makorText,
                            makorUri,
                            numOfPsukimString);
                    sortedMekorot.put(makorUri, makorModel);
                }
            } finally {
                sortedMekorotQuery.close();
            }
            String mekorotAuthorsQuery = JBSQueries.getMekorotAuthors(mekorotUris);
            QueryEngineHTTP mekorotAuthorsSelect = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                    mekorotAuthorsQuery);
            try {
                ResultSet resultSet = mekorotAuthorsSelect.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution rb = resultSet.nextSolution();
                    String makorUri = rb.get(JBSQueries.MAKOR).toString();
                    RDFNode authorNode = rb.get(JBSQueries.AUTHOR);
                    String authorFull = "";
                    String authorName = "";
                    if (authorNode != null) {
                        authorFull = authorNode.toString();
                        String author = authorFull.substring(authorFull.lastIndexOf("-") + 1);
                        String[] authorNameArray = author.split("_");
                        authorName = "";
                        for (int i=0; i < authorNameArray.length; i++) {
                            if (i != authorNameArray.length - 1) {
                                authorName += authorNameArray[i] + " ";
                            } else {
                                authorName += authorNameArray[i];
                            }
                        }
                    }
                    mekorotUrisAuthors.put(makorUri, authorName);
                }
            } finally {
                mekorotAuthorsSelect.close();
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
        Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>> queryResultsPair = Pair.create(
                sortedMekorot,
                bookSubjects);
        Iterator it = mekorotUrisAuthors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String makorUri = (String) pair.getKey();
            String makorAuthor = (String) pair.getValue();
            if (sortedMekorot.get(makorUri) != null) {
                MakorModel makorModelForAuthor = sortedMekorot.get(makorUri);
                if (!makorAuthor.equals("")) {
                    makorModelForAuthor.setMakorAuthor(makorAuthor);
                }
                sortedMekorot.put(makorUri, makorModelForAuthor);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return queryResultsPair;
    }

    @Override
    protected void onPostExecute(
            Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>> mekorotModelCategoryPair) {
        super.onPostExecute(mekorotModelCategoryPair);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        HashMap<String, MakorModel> mekorotModels = mekorotModelCategoryPair.first;
        ArrayList<CategoryModel> mekorotCategories = mekorotModelCategoryPair.second;
        if (mFragment instanceof MekorotTab) {
            ((MekorotTab) mFragment).setRecyclerViewAdapter(mekorotModels, mekorotCategories);
        }
    }
}
