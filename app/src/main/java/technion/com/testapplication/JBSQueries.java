package technion.com.testapplication;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class JBSQueries {

    public static final String JBS_ENDPOINT = "http://tdk3.csf.technion.ac.il:8890/sparql";
    public static final String JBS_LABEL = "label";
    public static final String JBS_BOOK = "book";
    public static final String JBS_URI = "uri";
    public static final String PASUK = "pasuk";
    public static final String PASUK_TEXT = "pasuk_text";
    public static final String PASUK_LABEL = "label";
    public static final String MAKOR = "makor";
    public static final String AUTHOR = "author";
    public static final String MAKOR_NAME = "label";
    public static final String SCORE = "score";
    public static final String MAKOR_TEXT = "text";
    public static final String MAKOR_SOURCE_URI = "source";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_REFERENCE_NUM = "num";
    public static final String READ_URL = "http://tdk-p6.cs.technion.ac.il:3000/read?uri=";

    public static final String GET_ALL_PARASHOT = "PREFIX jbr: <http://jbs.technion.ac.il/resource/>" +
            "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
            "PREFIX dco: <http://purl.org/dc/terms/>" +
            "SELECT ?uri ?label ?position " +
            "WHERE {" +
            " ?uri a jbo:ParashaTorah ." +
            " ?uri rdfs:label ?label ." +
            " ?uri jbo:position ?position" +
            " .} ORDER BY ASC(xsd:integer(?position))";

    public static final String GET_ALL_PRAKIM = "PREFIX jbr: <http://jbs.technion.ac.il/resource/> \n" +
            "PREFIX jbo: <http://jbs.technion.ac.il/ontology/> \n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX dco: <http://purl.org/dc/terms/>\n" +
            "SELECT ?uri ?label ?position WHERE {\n" +
            "?uri a jbo:Section. \n" +
            "?uri rdfs:label ?label. \n" +
            "?uri jbo:position ?position.\n" +
            "?uri jbo:book ?book.\n" +
            "?book a jbo:BookTanach.\n" +
            "} ORDER BY ASC(xsd:integer(?position))";

    public static final String GET_BOOKS_AND_CATEGORIES = "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
            "PREFIX dc: <http://purl.org/dc/terms/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "SELECT ?book ?label ?category {\n" +
            "?book a jbo:Book; rdfs:label ?label; dc:subject ?category.}";

    public static String getAllPsukimFromParashaQuery(String parashaUri) {
        return " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                " PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                " PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                " PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                " PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                " PREFIX dco: <http://purl.org/dc/terms/> \n" +
                " SELECT ?pasuk ?label ?pasuk_text ?position (COUNT(?makor) as ?numOfMekorot) \n" +
                " WHERE {?pasuk a jbo:Pasuk. \n" +
                " ?pasuk jbo:within jbr:" + parashaUri + ".\n" +
                " ?pasuk rdfs:label ?label. \n" +
                " ?pasuk jbo:text ?pasuk_text. \n" +
                " ?pasuk jbo:position ?position. \n" +
                " ?mention a jbo:Mention.\n" +
                " ?mention jbo:source ?makor; jbo:target ?pasuk. \n" +
                " } ORDER BY ASC(xsd:integer(?position))";
    }

    public static String getAllPsukimBySubstrQuery(String pasukSubstr) {
        return " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                " PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                " PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                " PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                " PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                " PREFIX dco: <http://purl.org/dc/terms/> \n" +
                " SELECT ?pasuk ?label ?pasuk_text ?position (COUNT(?makor) as ?numOfMekorot) \n" +
                " WHERE {?pasuk a jbo:Pasuk. \n" +
                " ?pasuk rdfs:label ?label. \n" +
                " ?pasuk jbo:text ?pasuk_text. \n" +
                " ?pasuk jbo:position ?position. \n" +
                " ?mention a jbo:Mention.\n" +
                " ?mention jbo:source ?makor; jbo:target ?pasuk. \n" +
                " filter (regex(str(?pasuk_text), \"" + pasukSubstr + "\" )). \n" +
                " } ORDER BY ASC(xsd:integer(?position))";
    }

    public static String getPsukimToHighlightFromMakor(String makorUri, ArrayList<String> psukim) {
        StringBuilder psukimList = new StringBuilder("");
        for (String pasuk : psukim)
        {
            psukimList.append(pasuk);
            psukimList.append(" ");
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n"
                + "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n"
                + "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n"
                + "SELECT ?makor ?pasuk ?pasuk_text ?label ?span WHERE {\n"
                + "values ?makor { " + makorUri + " }\n"
                + "values ?pasuk { " + psukimList + " }\n"
                + "?pasuk jbo:text ?pasuk_text. \n"
                + "?pasuk rdfs:label ?label. \n"
                + "?mention a jbo:Mention.\n"
                + "?mention jbo:source ?makor; jbo:target ?pasuk; jbo:span ?span.\n"
                + "}\n";
    }

    public static String getCategoriesByPsukimWithReferenceNumber(ArrayList<String> psukim) {
        StringBuilder psukimList = new StringBuilder("");
        for (String pasuk : psukim)
        {
            psukimList.append(pasuk);
            psukimList.append(" ");
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n"
                + "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n"
                + "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n"
                + "            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>             \n"
                + "SELECT ?category (COUNT(DISTINCT ?makor) AS ?num) WHERE {\n"
                + "values ?pasuk { " + psukimList + " }\n"
                + "?makor jbo:mentions ?pasuk; jbo:book ?book.\n"
                + "?book dco:subject ?category.\n"
                + "?category rdfs:label ?label."
                + "} GROUP BY ?category ?label order by DESC(?num)";
    }

    public static String getMekorotAuthors(ArrayList<String> mekorotUris) {
        ArrayList<String> prefixedMekorotUris = new ArrayList<>();
        for (int i = 0; i < mekorotUris.size(); i++)
        {
            String makorUri = mekorotUris.get(i);
            makorUri = makorUri.substring(makorUri.lastIndexOf("/") + 1);
            makorUri = "jbr:" + makorUri;
            prefixedMekorotUris.add(i, makorUri);
        }
        StringBuilder mekorotList = new StringBuilder("");
        for (String makor : prefixedMekorotUris)
        {
            mekorotList.append(makor);
            mekorotList.append(" ");
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "SELECT ?makor ?author WHERE {\n" +
                "values ?makor { " + mekorotList + " }\n" +
                "OPTIONAL {?makor jbo:book ?book. ?book jbo:author ?author.}\n" +
                "}\n";
    }

    public static String getMekorotWithAllData(ArrayList<String> psukim) {
        StringBuilder psukimList = new StringBuilder("");
        for (String pasuk : psukim)
        {
            psukimList.append(pasuk);
            psukimList.append(" ");
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "SELECT SAMPLE(?label) as ?label SAMPLE(?text) as ?text ?makor SAMPLE(?book) as ?book (COUNT(?pasuk) AS ?numOfPsukim) (SUM(xsd:integer(?numOfMentions)) as ?score) WHERE {\n" +
                "values ?pasuk {" + psukimList + "}\n" +
                "?mention a jbo:Mention.\n" +
                "?mention jbo:source ?makor; jbo:target ?pasuk; jbo:numOfMentions ?numOfMentions.\n" +
                "?makor rdfs:label ?label.\n" +
                "?makor jbo:text ?text.\n" +
                "?makor jbo:book ?book.\n" +
                "} GROUP BY ?text ?label ?makor ?book ORDER BY DESC(?score)\n";
    }

}
