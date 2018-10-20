package technion.com.testapplication.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.util.TimingLogger;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.data_manage.Cacheable;
import technion.com.testapplication.models.CategoryModel;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 18/12/2017.
 * This class is intended for fetching mekorot by score.
 */
public class FetchMekorotByScoreTask
        extends
        AsyncTask<String, Void, Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>>> {
    private Fragment mFragment;
    private Cacheable mCaller;
    private Runnable mOnComplete;
    private boolean mShouldFilter;
    private ProgressDialog mProgressDialog;
    private static final String NUM_OF_REFERENCES_REGEX = "^";
    private static final String BOOK_URI_SEPARATOR = "/";

    public FetchMekorotByScoreTask(Fragment fragment, boolean shouldFilter, Cacheable caller, Runnable onComplete) {
        mShouldFilter = shouldFilter;
        mFragment = fragment;
        mCaller = caller;
        mOnComplete = onComplete;
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
        TimingLogger timings = new TimingLogger("FetchMekorot", "DoInBackground");
        timings.addSplit("fetch began...");
        HashMap<String, MakorModel> sortedMekorot = new HashMap<>();
        ArrayList<String> mekorotUris = new ArrayList<>();
        ArrayList<CategoryModel> categories = new ArrayList<>();
        HashMap<String, String> mekorotUrisAuthors = new HashMap<>();
        try
        {
            timings.addSplit("creating reading mekorot and categories queries...");
            QueryEngineHTTP sortedMekorotQuery = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                                                                     params[0]);
            QueryEngineHTTP queryEngineHTTPCategories = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                                                                            params[1]);
            timings.addSplit("fetching mekorot...");
            try
            {
                ResultSet resultSet = sortedMekorotQuery.execSelect();
                timings.addSplit("query returned.");
                while (resultSet.hasNext())
                {
                    QuerySolution rb = resultSet.nextSolution();
                    String numOfPsukimString;
                    String makorName = rb.get(JBSQueries.MAKOR_NAME).toString();
                    String makorText = rb.get(JBSQueries.MAKOR_TEXT).toString();
                    String makorBook = rb.get(JBSQueries.JBS_BOOK).toString();
                    String makorUri;
                    String numOfPsukimLiteralAsString;
                    if (mShouldFilter)
                    {
                        numOfPsukimLiteralAsString = rb.get(
                                "sum").toString();
                    }
                    else
                    {
                        numOfPsukimLiteralAsString = rb.get(
                                JBSQueries.SCORE).toString();
                    }
                    numOfPsukimString = numOfPsukimLiteralAsString.substring(0,
                                                                             numOfPsukimLiteralAsString.indexOf(NUM_OF_REFERENCES_REGEX));
                    if (mShouldFilter)
                    {
                        makorUri = rb.get(JBSQueries.MAKOR_SOURCE_URI).toString();
                    }
                    else
                    {
                        makorUri = rb.get(JBSQueries.MAKOR).toString();
                    }
                    mekorotUris.add(makorUri);
                    MakorModel makorModel = new MakorModel(
                            makorName,
                            makorText,
                            makorUri,
                            makorBook,
                            numOfPsukimString);
                    sortedMekorot.put(makorUri, makorModel);
                }
            } finally
            {
                timings.addSplit("finished fetching mekorot.");
                sortedMekorotQuery.close();
            }
            String mekorotAuthorsQuery = JBSQueries.getMekorotAuthors(mekorotUris);
            QueryEngineHTTP mekorotAuthorsSelect = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                                                                       mekorotAuthorsQuery);
            timings.addSplit("fetching authors...");
            try
            {
                ResultSet resultSet = mekorotAuthorsSelect.execSelect();
                while (resultSet.hasNext())
                {
                    QuerySolution rb = resultSet.nextSolution();
                    String makorUri = rb.get(JBSQueries.MAKOR).toString();
                    RDFNode authorNode = rb.get(JBSQueries.AUTHOR);
                    String authorFull;
                    StringBuilder authorName = new StringBuilder();
                    if (authorNode != null)
                    {
                        authorFull = authorNode.toString();
                        String author = authorFull.substring(authorFull.lastIndexOf("-") + 1);
                        String[] authorNameArray = author.split("_");
                        authorName = new StringBuilder();
                        for (int i = 0; i < authorNameArray.length; i++)
                        {
                            if (i != authorNameArray.length - 1)
                            {
                                authorName.append(authorNameArray[i]).append(" ");
                            }
                            else
                            {
                                authorName.append(authorNameArray[i]);
                            }
                        }
                    }
                    mekorotUrisAuthors.put(makorUri, authorName.toString());
                }
            } finally
            {
                timings.addSplit("finished fetching authors.");
                mekorotAuthorsSelect.close();
            }
            timings.addSplit("fetching categories...");
            try
            {
                ResultSet resultSet = queryEngineHTTPCategories.execSelect();
                while (resultSet.hasNext())
                {
                    QuerySolution rb = resultSet.nextSolution();
                    String categoryUri = rb.get(JBSQueries.CATEGORY).toString();
                    String bookUri = rb.get(JBSQueries.JBS_BOOK).toString();
                    String referenceNumNodeAsString = rb.get(
                            JBSQueries.CATEGORY_REFERENCE_NUM).toString();
                    String referenceNum = referenceNumNodeAsString.substring(0,
                                                                             referenceNumNodeAsString.indexOf(
                                                                                     NUM_OF_REFERENCES_REGEX));
                    String categoryName = categoryUri.substring(
                            categoryUri.lastIndexOf(BOOK_URI_SEPARATOR) + 1);
                    categories.add(new CategoryModel(categoryName, referenceNum, bookUri));
                }
            } finally
            {
                timings.addSplit("sorting categories...");
                Collections.sort(categories, new Comparator<CategoryModel>() {

                    public int compare(CategoryModel c1, CategoryModel c2) {
                        return Integer.valueOf(c2.getCategoryRefernceNum()) - Integer.valueOf(c1.getCategoryRefernceNum());
                    }
                });
                timings.addSplit("finished fetching categories.");
                queryEngineHTTPCategories.close();
            }
        } catch (Exception err)
        {
            err.printStackTrace();
        }
        timings.addSplit("constructing the fetch results...");
        Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>> queryResultsPair = Pair.create(
                sortedMekorot,
                categories);
        Iterator it = mekorotUrisAuthors.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            String makorUri = (String) pair.getKey();
            String makorAuthor = (String) pair.getValue();
            if (sortedMekorot.get(makorUri) != null)
            {
                MakorModel makorModelForAuthor = sortedMekorot.get(makorUri);
                if (!makorAuthor.equals(""))
                {
                    makorModelForAuthor.setMakorAuthor(makorAuthor);
                }
                sortedMekorot.put(makorUri, makorModelForAuthor);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        timings.addSplit("finished fetching.");
        timings.dumpToLog();
        return queryResultsPair;
    }

    @Override
    protected void onPostExecute(
            Pair<HashMap<String, MakorModel>, ArrayList<CategoryModel>> mekorotModelCategoryPair) {
        super.onPostExecute(mekorotModelCategoryPair);
        if (mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
        ArrayList<Object> mekorotModelCategoryArray = new ArrayList<>();
        mekorotModelCategoryArray.add(0, mekorotModelCategoryPair.first);
        mekorotModelCategoryArray.add(1, mekorotModelCategoryPair.second);
        mCaller.setData(mekorotModelCategoryArray);
        if (mOnComplete != null)
        {
            mOnComplete.run();
        }
    }
}
