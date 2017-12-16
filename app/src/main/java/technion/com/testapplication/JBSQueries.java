package technion.com.testapplication;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class JBSQueries {
    public static final String GET_ALL_PSUKIM_FROM_PARASHA =
            "SELECT ?pasuk ?position ?pasuk_text sum(strlen(?text)) as ?total_perush_size WHERE {" +
                    "?pasuk a jbo:Pasuk; jbo:within jbr:section-tanach-parasha-15; jbo:position ?position; jbo:text ?pasuk_text." +
                    "?perush jbo:interprets ?pasuk; jbo:text ?text." +
                    "} ORDER BY ASC(xsd:integer(?position))" +
                    "";
}
