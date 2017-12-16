package technion.com.testapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class FetchPsukimTask extends AsyncTask<String, Void, ArrayList<PasukModel>> {
    private static final String TECHNION_ENDPOINT = "http://tdk3.csf.technion.ac.il:8890/sparql";
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    public static final String MAGIC_SEPERATOR = "$$$";

    public FetchPsukimTask(Activity activity) {
        mActivity = activity;
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage("אנא המתנ/י");
        this.mProgressDialog.show();
    }

    @Override
    protected ArrayList<PasukModel> doInBackground(String... params) {
        ArrayList<PasukModel> queryResults = new ArrayList<>();
        try {
            com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(params[0]);
            com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(TECHNION_ENDPOINT, query);

            try {
                ResultSet rs = qexec.execSelect();

                while(rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution() ;
                    PasukModel pasukModel = new PasukModel(rb.get("pasuk_text").toString());
                    pasukModel.setUri(rb.get("pasuk").toString());
                    queryResults.add(pasukModel);
                }
            }
            finally {
                qexec.close();
            }


        }catch( Exception err){
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
            ((PsukimActivity)mActivity).setRecyclerViewAdapter(pasukModelList);
        }
    }
}
