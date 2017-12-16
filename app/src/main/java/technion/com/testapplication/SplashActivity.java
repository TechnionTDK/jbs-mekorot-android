package technion.com.testapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final String technionQuery = "PREFIX jbr: <http://jbs.technion.ac.il/resource/>" +
                "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+
                "PREFIX dco: <http://purl.org/dc/terms/>"+
                "SELECT ?uri ?label ?position "+
                "WHERE {" +
                " ?uri a jbo:ParashaTorah ." +
                " ?uri rdfs:label ?label ."+
                " ?uri jbo:position ?position" +
                " .} ORDER BY ASC(xsd:integer(?position))";
        SparqlTask task = new SparqlTask(this);
        task.execute(technionQuery);
    }
}
