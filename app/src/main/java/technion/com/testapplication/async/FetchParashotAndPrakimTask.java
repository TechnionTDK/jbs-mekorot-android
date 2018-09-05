package technion.com.testapplication.async;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.activities.NavActivity;
import technion.com.testapplication.data_manage.DataManager;
import technion.com.testapplication.models.ParashotAndPrakimAndBooks;

/**
 * Created by tomerlevinson on 15/12/2017.
 * Used in order to fetch all parashot and all prakim.
 */
public class FetchParashotAndPrakimTask extends AsyncTask<String, Void, ParashotAndPrakimAndBooks> {
    private Activity mActivity;
    private AlertDialog mDialog;
    private boolean isError = false;
    public static final String MAGIC_SEPERATOR = "$$$";

    public FetchParashotAndPrakimTask(Activity activity) {
        mActivity = activity;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        final Runnable finishCallingActivity = new Runnable() {
            @Override
            public void run() {
                mActivity.finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.alert_error));
        builder.setMessage(mActivity.getResources().getString(R.string.alert_connection_error_info));
        builder.setNeutralButton(
                mActivity.getResources().getString(R.string.close_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishCallingActivity.run();
                    }
                });

        mDialog = builder.create();
    }

    @Override
    protected ParashotAndPrakimAndBooks doInBackground(String... params) {
        ArrayList<String> queryResultsParashot = new ArrayList<>();
        ArrayList<String> queryResultsPrakim = new ArrayList<>();
        HashMap<String, ArrayList<String>> bookToCategories = new HashMap<>();
        HashMap<String, ArrayList<String>> categoryToBooks = new HashMap<>();
        try
        {
            // Create parashot query
            com.hp.hpl.jena.query.Query parashotQuery = com.hp.hpl.jena.query.QueryFactory.create(
                    params[0]);
            com.hp.hpl.jena.query.QueryExecution parashotQueryExec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, parashotQuery);

            // Create prakim query
            com.hp.hpl.jena.query.Query prakimQuery = com.hp.hpl.jena.query.QueryFactory.create(
                    params[1]);
            com.hp.hpl.jena.query.QueryExecution prakimQueryExec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, prakimQuery);

            // Create books query
            com.hp.hpl.jena.query.Query booksQuery = com.hp.hpl.jena.query.QueryFactory.create(
                    params[2]);
            com.hp.hpl.jena.query.QueryExecution booksQueryExec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, booksQuery);

            // Run parashot query
            try
            {
                ResultSet rs = parashotQueryExec.execSelect();

                while (rs.hasNext())
                {
                    QuerySolution rb = rs.nextSolution();
                    String label = rb.get(JBSQueries.JBS_LABEL).toString();
                    String uri = rb.get(JBSQueries.JBS_URI).toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResultsParashot.add(labelAndUri);
                }
            } finally
            {
                parashotQueryExec.close();
            }

            // Run prakim query
            try
            {
                ResultSet rs = prakimQueryExec.execSelect();

                while (rs.hasNext())
                {
                    QuerySolution rb = rs.nextSolution();
                    String label = rb.get(JBSQueries.JBS_LABEL).toString();
                    String uri = rb.get(JBSQueries.JBS_URI).toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResultsPrakim.add(labelAndUri);
                }
            } finally
            {
                parashotQueryExec.close();
            }

            // Run books query
            try
            {
                DataManager dataManager = new DataManager(mActivity.getApplicationContext());
                bookToCategories = (HashMap<String, ArrayList<String>>) dataManager.getCachedData("BOOK_TO_CATEGORIES");
                categoryToBooks = (HashMap<String, ArrayList<String>>) dataManager.getCachedData("CATEGORY_TO_BOOKS");
                if (bookToCategories == null || categoryToBooks == null)
                {
                    bookToCategories = new HashMap<>();
                    categoryToBooks = new HashMap<>();
                    ResultSet rs = booksQueryExec.execSelect();
                    while (rs.hasNext())
                    {
                        QuerySolution rb = rs.nextSolution();
                        String book = rb.get(JBSQueries.JBS_BOOK).toString();
                        String category = rb.get(JBSQueries.CATEGORY).toString();

                        // update book to categories
                        if (bookToCategories.containsKey(book))
                        {
                            bookToCategories.get(book).add(category);
                        }
                        else
                        {
                            ArrayList<String> categories = new ArrayList<>();
                            categories.add(category);
                            bookToCategories.put(book, categories);
                        }

                        // update category to books
                        if (categoryToBooks.containsKey(category))
                        {
                            categoryToBooks.get(category).add(book);
                        }
                        else
                        {
                            ArrayList<String> books = new ArrayList<>();
                            books.add(book);
                            categoryToBooks.put(category, books);
                        }
                    }
                    dataManager.cacheData("BOOK_TO_CATEGORIES", bookToCategories);
                    dataManager.cacheData("CATEGORIES_TO_BOOKS", categoryToBooks);
                }
            } finally
            {

            }
        } catch (Exception err)
        {
            isError = true;
            err.printStackTrace();
        }
        return new ParashotAndPrakimAndBooks(queryResultsParashot, queryResultsPrakim, bookToCategories, categoryToBooks);
    }

    @Override
    protected void onPostExecute(ParashotAndPrakimAndBooks parashotAndPrakimAndBooks) {
        super.onPostExecute(parashotAndPrakimAndBooks);
        if (isError)
        {
            mDialog.show();
            return;
        }
        Intent startMainActivityIntent = new Intent(mActivity, NavActivity.class);
        ArrayList<String> parashotArray = parashotAndPrakimAndBooks.getParashot();
        ArrayList<String> prakimArray = parashotAndPrakimAndBooks.getPrakim();
        HashMap<String, ArrayList<String>> bookToCategoriesMap = parashotAndPrakimAndBooks.getBookToCategories();
        HashMap<String, ArrayList<String>> categoriesToBooksMap = parashotAndPrakimAndBooks.getCategoryToBooks();
        startMainActivityIntent.putStringArrayListExtra(
                mActivity.getResources().getString(R.string.parashot_and_uri_extra), parashotArray);
        startMainActivityIntent.putStringArrayListExtra(
                mActivity.getResources().getString(R.string.prakim_and_uri_extra), prakimArray);
        startMainActivityIntent.putExtra("bookToCategories", bookToCategoriesMap);
        startMainActivityIntent.putExtra("categoryToBooks", categoriesToBooksMap);
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        mActivity.startActivity(startMainActivityIntent);
        mActivity.finish();
    }
}
