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
    public static String getAllPsukimFromParashaQuery(String parashaUri) {
        return " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                " PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                " PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                " PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                " PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                " SELECT distinct ?pasuk ?position ?pasuk_text WHERE {\n" +
                " ?pasuk a jbo:Pasuk; jbo:within jbr:" + parashaUri + "; jbo:position ?position; jbo:text ?pasuk_text.\n" +
                " ?perush jbo:interprets ?pasuk; jbo:text ?text.\n" +
                "} ORDER BY ASC(xsd:integer(?position))";
    }
}
