package technion.com.testapplication.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.HashMap;

import technion.com.testapplication.JBSQueries;

public class FetchBooksAndCategories extends
        AsyncTask<String, Void, HashMap<String, ArrayList<String>>> {
    private Runnable mOnComplete;
    private Activity mCaller;

    public FetchBooksAndCategories(Activity caller, Runnable mOnComplete) {
        this.mOnComplete = mOnComplete;
        this.mCaller = caller;
    }

    @Override
    protected HashMap<String, ArrayList<String>> doInBackground(String... params) {
        HashMap<String, ArrayList<String>> results = new HashMap<>();
        try
        {
            QueryEngineHTTP query = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT, params[0]);
            ResultSet resultSet = query.execSelect();
            while (resultSet.hasNext())
            {
                QuerySolution rb = resultSet.nextSolution();
                String category = rb.get(JBSQueries.CATEGORY).toString();
                String book = rb.get(JBSQueries.JBS_BOOK).toString();
                if (results.containsKey(category))
                {
                    ArrayList<String> books = results.get(category);
                    books.add(book);
                    results.put(category, books);
                }
                else
                {
                    ArrayList<String> books = new ArrayList<>();
                    books.add(book);
                    results.put(category, books);
                }
            }
        } catch (Exception e)
        {
            results.clear();
        }

        return results;
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<String>> stringArrayListHashMap) {
        super.onPostExecute(stringArrayListHashMap);
        mOnComplete.run();
    }
}
