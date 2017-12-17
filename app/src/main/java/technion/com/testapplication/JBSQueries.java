package technion.com.testapplication;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class JBSQueries {

    public static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";
    public static final String JBS_ENDPOINT = "http://tdk3.csf.technion.ac.il:8890/sparql";
    public static final String JBS_LABEL = "label";
    public static final String JBS_URI = "uri";
    public static final String PASUK = "pasuk";
    public static final String PASUK_TEXT = "pasuk_text";

    public static final String GET_ALL_PSUKIM_FROM_PARASHA =
            "SELECT ?pasuk ?position ?pasuk_text sum(strlen(?text)) as ?total_perush_size WHERE {" +
                    "?pasuk a jbo:Pasuk; jbo:within jbr:section-tanach-parasha-15; jbo:position ?position; jbo:text ?pasuk_text." +
                    "?perush jbo:interprets ?pasuk; jbo:text ?text." +
                    "} ORDER BY ASC(xsd:integer(?position))" +
                    "";
    public static final String GET_ALL_PARASHOT = "PREFIX jbr: <http://jbs.technion.ac.il/resource/>" +
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
    public static final String SAMPLE_DBPEDIA_QUERY =
            "PREFIX p: <http://dbpedia.org/property/>"+
                    "PREFIX dbpedia: <http://dbpedia.org/resource/>"+
                    "PREFIX category: <http://dbpedia.org/resource/Category:>"+
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
                    "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
                    "PREFIX geo: <http://www.georss.org/georss/>"+
                    "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
                    "SELECT ?label ?lat ?long ?comment "+
                    "WHERE {"+
                    " ?subject geo-pos:lat ?lat ."+
                    " ?subject geo-pos:long ?long ."+
                    " ?subject rdfs:label ?label ."+
                    " ?subject rdfs:comment ?comment ."+
                    " FILTER( (?lat > 48.23185544 && ?lat < 49.23185844 && ?long > 16.31660208 && ?long < 17.31660208) && (lang(?comment) = 'en') && (lang(?label) = 'en') )"+
                    " .} ORDER BY ?lat ?long LIMIT 15 ";
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
