package technion.com.testapplication;

import java.util.ArrayList;

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
    public static final String MAKOR = "makor";
    public static final String AUTHOR = "author";
    public static final String MAKOR_NAME = "label";
    public static final String SCORE = "score";
    public static final String MAKOR_TEXT = "text";
    public static final String NUM_OF_PSUKIM_AS_SUM = "sum";
    public static final String MAKOR_SOURCE_URI = "source";
    public static final String BOOK_SUBJECT = "book_subject";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_REFERENCE_NUM = "num";
    public static final String NUM_OF_PSUKIM = "numOfPsukim";


    public static final String GET_ALL_PSUKIM_FROM_PARASHA =
            "SELECT ?pasuk ?position ?pasuk_text sum(strlen(?text)) as ?total_perush_size WHERE {" +
                    "?pasuk a jbo:Pasuk; jbo:within jbr:section-tanach-parasha-15; jbo:position ?position; jbo:text ?pasuk_text." +
                    "?perush jbo:interprets ?pasuk; jbo:text ?text." +
                    "} ORDER BY ASC(xsd:integer(?position))" +
                    "";
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
    public static final String SAMPLE_DBPEDIA_QUERY =
            "PREFIX p: <http://dbpedia.org/property/>" +
                    "PREFIX dbpedia: <http://dbpedia.org/resource/>" +
                    "PREFIX category: <http://dbpedia.org/resource/Category:>" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                    "PREFIX geo: <http://www.georss.org/georss/>" +
                    "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
                    "SELECT ?label ?lat ?long ?comment " +
                    "WHERE {" +
                    " ?subject geo-pos:lat ?lat ." +
                    " ?subject geo-pos:long ?long ." +
                    " ?subject rdfs:label ?label ." +
                    " ?subject rdfs:comment ?comment ." +
                    " FILTER( (?lat > 48.23185544 && ?lat < 49.23185844 && ?long > 16.31660208 && ?long < 17.31660208) && (lang(?comment) = 'en') && (lang(?label) = 'en') )" +
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

    public static String getPsukimToHighlightFromMakor(String makorUri, ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n"
                + "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n"
                + "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n"
                + "SELECT ?makor ?pasuk ?span WHERE {\n"
                + "values ?makor { " + makorUri + " }\n"
                + "values ?pasuk { " + psukimList + " }\n"
                + "?mention a jbo:Mention.\n"
                + "?mention jbo:source ?makor; jbo:target ?pasuk; jbo:span ?span.\n"
                + "}\n";
    }

    public static String getCategoriesByPsukimWithReferenceNumber(ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n"
                + "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n"
                + "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n"
                + "SELECT ?category (COUNT(DISTINCT ?makor) AS ?num)  WHERE {\n"
                + "values ?pasuk { " + psukimList + " }\n"
                + "?makor jbo:mentions ?pasuk; jbo:book ?book.\n"
                + "?book dco:subject ?category.\n"
                + "} GROUP BY ?category order by DESC(?num)";
    }

    public static String getMekorotAuthors(ArrayList<String> mekorotUris) {
        ArrayList<String> prefixedMekorotUris = new ArrayList<>();
        for (int i = 0; i < mekorotUris.size(); i++) {
            String makorUri = mekorotUris.get(i);
            makorUri = makorUri.substring(makorUri.lastIndexOf("/") + 1);
            makorUri = "jbr:" + makorUri;
            prefixedMekorotUris.add(i, makorUri);
        }
        String mekorotList = "";
        for (String makor : prefixedMekorotUris) {
            mekorotList += makor + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "SELECT ?makor ?author WHERE {\n" +
                "values ?makor { " + mekorotList + " }\n" +
                "OPTIONAL {?makor jbo:book ?book. ?book jbo:author ?author.}\n" +
                "}\n";
    }

    public static String getCategoriesByPsukim(ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "            SELECT ?book_subject COUNT(DISTINCT(?source)) as ?count                     \n" +
                "            WHERE                                                                       \n" +
                "            {                                                                           \n" +
                "                {                                                                       \n" +
                "                    values ?pasuk { " + psukimList + " }                                \n" +
                "                    ?pasuk a jbo:Pasuk.                                                 \n" +
                "                    ?mentions rdf:type jbo:Mention.                                     \n" +
                "                    ?mentions jbo:target ?pasuk.                                        \n" +
                "                    ?mentions jbo:source ?source.                                          \n" +
                "                    ?source rdfs:label ?label.                                          \n" +
                "                    ?source jbo:text ?text.                                             \n" +
                "                    ?pasuk rdfs:label ?target_label.                                       \n" +
                "                    ?source jbo:book ?source_book.                                         \n" +
                "                    ?source_book dco:subject ?book_subject.                                \n" +
                "                }                                                                          \n" +
                "                UNION {                                                                 \n" +
                "                    values ?container { " + psukimList + " }                            \n" +
                "                    values ?types {jbo:Section jbo:ParashaTorah}                        \n" +
                "                    ?container a ?types.                                                \n" +
                "                    ?pasuk jbo:within ?container.                                       \n" +
                "                    ?pasuk a jbo:Pasuk.                                                 \n" +
                "                    ?mentions rdf:type jbo:Mention.                                     \n" +
                "                    ?mentions jbo:target ?pasuk.                                        \n" +
                "                    ?mentions jbo:source ?source.                                          \n" +
                "                    ?source rdfs:label ?label.                                          \n" +
                "                    ?source jbo:text ?text.                                             \n" +
                "                    ?pasuk rdfs:label ?target_label.                                       \n" +
                "                    ?source jbo:book ?source_book.                                         \n" +
                "                    ?source_book dco:subject ?book_subject.                                \n" +
                "                }                                                                       \n" +
                "                UNION {                                                                 \n" +
                "                    values ?books { " + psukimList + " }                                \n" +
                "                    values ?types {jbo:BookTorah}                                       \n" +
                "                    ?books a ?types.                                                    \n" +
                "                    ?pasuk jbo:book ?books.                                             \n" +
                "                    ?pasuk a jbo:Pasuk.                                                 \n" +
                "                    ?mentions rdf:type jbo:Mention.                                     \n" +
                "                    ?mentions jbo:target ?pasuk.                                        \n" +
                "                    ?mentions jbo:source ?source.                                          \n" +
                "                    ?source rdfs:label ?label.                                          \n" +
                "                    ?source jbo:text ?text.                                             \n" +
                "                    ?pasuk rdfs:label ?target_label.                                       \n" +
                "                    ?source jbo:book ?source_book.                                         \n" +
                "                    ?source_book dco:subject ?book_subject.                                \n" +
                "                }                                                                       \n" +
                "            }                                                                              \n" +
                "            group by ?book_subject order by ?count";
    }

    public static String getMekorotFiltered(ArrayList<String> subjects, ArrayList<String> psukim) {
        String psukimList = "";
        String subjectList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        for (String subject : subjects) {
            subjectList += subject + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                "      PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                "      PREFIX dco: <http://purl.org/dc/terms/>\n" +
                "      \n" +
                "      SELECT DISTINCT(?source) SAMPLE(?label) as ?label SAMPLE(?text) as ?text       SAMPLE(?sums) as ?sums SAMPLE(?spanss) as ?spanss SAMPLE (?labels) as ?labels      SAMPLE(?sum) as ?sum SAMPLE(?description) as ?description \tSAMPLE(?numOfPsukim) as ?numOfPsukim\n" +
                "   WHERE {\n" +
                "   \n" +
                "   \n" +
                "   SELECT ?source ?label ?text ?description\n" +
                "       (SUM(xsd:integer(?numOfMentions)) as ?sum)\n" +
                "       \n" +
                "       (group_concat(?numOfMentions;separator=\",\") as ?sums)\n" +
                "    (group_concat(?spans;separator=\",\") as ?spanss)\n" +
                "       (group_concat(?target_label ;separator=\",\") as ?labels)\n" +
                "    COUNT(?target_label) as ?numOfPsukim\n" +
                "    WHERE {\n" +
                "    \n" +
                "    \n" +
                " SELECT ?mentions ?source ?numOfMentions ?target_label ?label ?text ?description      (group_concat(DISTINCT(?span);separator=\",\") as ?spans)\n" +
                "     WHERE\n" +
                "     \n" +
                "     \n" +
                " {\n" +
                " \n" +
                " \n" +
                " \n" +
                " \n" +
                " \n" +
                " {\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "       values ?pasuk {" + psukimList + "}\n" +
                "       \n" +
                "       ?pasuk a jbo:Pasuk.\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "  ?mentions rdf:type jbo:Mention.\n" +
                "  \n" +
                "  ?mentions jbo:target ?pasuk.\n" +
                "  \n" +
                "  ?mentions jbo:source ?source.\n" +
                "  \n" +
                "  ?mentions jbo:numOfMentions ?numOfMentions.\n" +
                "       ?mentions jbo:span ?span.\n" +
                "       \n" +
                "   ?source rdfs:label ?label.\n" +
                "   \n" +
                "   ?source jbo:text ?text.\n" +
                "   \n" +
                "    ?source jbo:book ?source_book.values ?subjects {" + subjectList + " }.\n" +
                "    \n" +
                "   ?source_book dco:subject ?subjects.?source_book jbo:description ?description.\n" +
                "    ?pasuk rdfs:label ?target_label.\n" +
                "    \n" +
                " FILTER (regex(str(?text), \"\")  || regex(str(?label), \"\")).       }\n" +
                " \n" +
                " \n" +
                " UNION {\n" +
                "\n" +
                "\n" +
                " values ?container {" + psukimList + "}\n" +
                " \n" +
                " values ?types {jbo:Section jbo:ParashaTorah}\n" +
                "\n" +
                "\n" +
                "\n" +
                "?container a ?types.\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                " ?pasuk jbo:within ?container.\n" +
                " \n" +
                "  ?pasuk a jbo:Pasuk.\n" +
                "  \n" +
                "  \n" +
                "  \n" +
                "  \n" +
                "  ?mentions rdf:type jbo:Mention.\n" +
                "  \n" +
                "  ?mentions jbo:target ?pasuk.\n" +
                "  \n" +
                "  ?mentions jbo:source ?source.\n" +
                "  \n" +
                "  ?mentions jbo:numOfMentions ?numOfMentions.\n" +
                "       ?mentions jbo:span ?span.\n" +
                "       \n" +
                "   ?source rdfs:label ?label.\n" +
                "   \n" +
                "   ?source jbo:text ?text.\n" +
                "   \n" +
                "    ?source jbo:book ?source_book.values ?subjects { " + subjectList + " }.\n" +
                "    \n" +
                "   ?source_book dco:subject ?subjects.?source_book jbo:description ?description.\n" +
                "    ?pasuk rdfs:label ?target_label.\n" +
                "    \n" +
                "  FILTER (regex(str(?text), \"\")  || regex(str(?label), \"\")).      }\n" +
                "  \n" +
                "  \n" +
                " UNION {\n" +
                " \n" +
                " \n" +
                " values ?books {" + psukimList + "}\n" +
                " \n" +
                " values ?types { jbo:BookTorah }\n" +
                " \n" +
                " \n" +
                " \n" +
                " ?books a ?types.\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "  ?pasuk jbo:book ?books.\n" +
                "  \n" +
                "    ?pasuk a jbo:Pasuk.\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "  ?mentions rdf:type jbo:Mention.\n" +
                "  \n" +
                "  ?mentions jbo:target ?pasuk.\n" +
                "  \n" +
                "  ?mentions jbo:source ?source.\n" +
                "  \n" +
                "  ?mentions jbo:numOfMentions ?numOfMentions.\n" +
                "       ?mentions jbo:span ?span.\n" +
                "       \n" +
                "   ?source rdfs:label ?label.\n" +
                "   \n" +
                "   ?source jbo:text ?text.\n" +
                "   \n" +
                "    ?source jbo:book ?source_book.values ?subjects {" + subjectList + "}.\n" +
                "    \n" +
                "   ?source_book dco:subject ?subjects.?source_book jbo:description ?description.\n" +
                "    ?pasuk rdfs:label ?target_label.\n" +
                "    \n" +
                "  FILTER (regex(str(?text), \"\")  || regex(str(?label), \"\")).      }\n" +
                "  \n" +
                "  \n" +
                "  }\n" +
                "\n" +
                "\n" +
                " group by ?target_label ?text ?mentions ?description\n" +
                "     ?source ?numOfMentions ?label\n" +
                "     \n" +
                "  }\n" +
                "  \n" +
                "  \n" +
                " group by ?source ?label ?text ?description ?numOfMentions\n" +
                "       }\n" +
                "       \n" +
                "       \n" +
                " order by DESC(?sum) offset 0 limit 500";
    }

    public static String getMekorotWithAllData(ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "SELECT ?label ?text ?makor (COUNT(?pasuk) AS ?numOfPsukim) (SUM(xsd:integer(?numOfMentions)) as ?score) WHERE {\n" +
                "values ?pasuk {" + psukimList + "}\n" +
                "?mention a jbo:Mention.\n" +
                "?mention jbo:source ?makor; jbo:target ?pasuk; jbo:numOfMentions ?numOfMentions.\n" +
                "?makor rdfs:label ?label.\n" +
                "?makor jbo:text ?text.\n" +
                "} GROUP BY ?text ?label ?makor ORDER BY DESC(?score)\n";
    }

    public static String getMekorotWithNumOfPsukimRefernces(ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return "PREFIX jbr: <http://jbs.technion.ac.il/resource/>                           \n" +
                "            PREFIX jbo: <http://jbs.technion.ac.il/ontology/>                           \n" +
                "            PREFIX dco: <http://purl.org/dc/terms/>                                     \n" +
                "SELECT ?makor (COUNT(?pasuk) AS ?numOfPsukim) (SUM(xsd:integer(?numOfMentions)) as ?score) WHERE {\n" +
                "values ?pasuk {" + psukimList + "}\n" +
                "?mention a jbo:Mention.\n" +
                "?mention jbo:source ?makor; jbo:target ?pasuk; jbo:numOfMentions ?numOfMentions.\n" +
                "} GROUP BY ?makor ORDER BY DESC(?score)\n";
    }

    public static String getMekorot(ArrayList<String> psukim) {
        String psukimList = "";
        for (String pasuk : psukim) {
            psukimList += pasuk + " ";
        }
        return " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  \n" +
                " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n" +
                " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n" +
                " PREFIX foaf: <http://xmlns.com/foaf/0.1/>  \n" +
                " PREFIX dc: <http://purl.org/dc/elements/1.1/>  \n" +
                " PREFIX : <http://dbpedia.org/resource/>  \n" +
                " PREFIX dbpedia2: <http://dbpedia.org/property/>  \n" +
                " PREFIX dbpedia: <http://dbpedia.org/>  \n" +
                " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>  \n" +
                " PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                " PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                " PREFIX dco: <http://purl.org/dc/terms/>\n"
                + "SELECT ?source ?label ?text\n"
                + "?sums ?spanss ?labels\n"
                + "?sum ?description\n"
                + "WHERE {\n"
                + " SELECT ?source ?label ?text ?description (SUM(xsd:integer(?numOfMentions)) AS ?sum)\n"
                + " (group_concat(?numOfMentions;separator=\",\") AS ?sums)\n"
                + " (group_concat(?spans;separator=\",\") AS ?spanss)\n"
                + " (group_concat(?target_label ;separator=\",\") AS ?labels)\n"
                + " WHERE { \n"
                + "         SELECT ?mentions ?source ?numOfMentions ?target_label ?label ?text ?description\n"
                + "         (group_concat(distinct ?span;separator=\",\") AS ?spans)\n"
                + "         WHERE { \n"
                + "          {\n"
                + "                 values ?pasuk {" + psukimList + "}\n"
                + "                 ?pasuk a jbo:Pasuk.\n"
                + "                 ?mentions rdf:type jbo:Mention.\n"
                + "                 ?mentions jbo:target ?pasuk.\n"
                + "                 ?mentions jbo:source ?source.\n"
                + "                 ?mentions jbo:numOfMentions ?numOfMentions.\n"
                + "                 ?mentions jbo:span ?span.\n"
                + "                 ?source rdfs:label ?label.\n"
                + "                 ?source jbo:text ?text.\n"
                + "                 ?source jbo:book ?source_book.\n"
                + "                 ?source_book jbo:description ?description.\n"
                + "                 ?pasuk rdfs:label ?target_label }\n"
                + "          UNION {\n"
                + "                 values ?container {" + psukimList + "}\n"
                + "                 values ?types {jbo:Section jbo:ParashaTorah}\n"
                + "                 ?container a ?types.\n"
                + "                 ?pasuk jbo:within ?container.\n"
                + "                 ?pasuk a jbo:Pasuk.\n"
                + "                 ?mentions rdf:type jbo:Mention.\n"
                + "                 ?mentions jbo:target ?pasuk.\n"
                + "                 ?mentions jbo:source ?source.\n"
                + "                 ?mentions jbo:numOfMentions ?numOfMentions.\n"
                + "                 ?mentions jbo:span ?span.\n"
                + "                 ?source rdfs:label ?label.\n"
                + "                 ?source jbo:text ?text.\n"
                + "                 ?source jbo:book ?source_book.\n"
                + "                 ?source_book jbo:description ?description.\n"
                + "                 ?pasuk rdfs:label ?target_label\n"
                + "         }\n"
                + "         UNION {\n"
                + "             values ?books {" + psukimList + "}\n"
                + "             values ?types { jbo:BookTorah }\n"
                + "             ?books a ?types.\n"
                + "             ?pasuk jbo:book ?books.\n"
                + "             ?pasuk a jbo:Pasuk.\n"
                + "             ?mentions rdf:type jbo:Mention.\n"
                + "             ?mentions jbo:target ?pasuk.\n"
                + "             ?mentions jbo:source ?source.\n"
                + "             ?mentions jbo:numOfMentions ?numOfMentions.\n"
                + "             ?mentions jbo:span ?span.\n"
                + "             ?source rdfs:label ?label.\n"
                + "             ?source jbo:text ?text.\n"
                + "             ?source jbo:book ?source_book.\n"
                + "             ?source_book jbo:description ?description.\n"
                + "             ?pasuk rdfs:label ?target_label\n"
                + "         }\n"
                + "     }\n"
                + "     group by ?target_label ?text ?mentions ?description\n"
                + "     ?source ?numOfMentions ?label\n"
                + "}\n"
                + "group by ?source ?label ?text ?description\n"
                + "}\n"
                + " ORDER BY DESC(?sum) offset 0 limit 500";
    }

}
