# Jewish Bookshelf Android - jbs-mekorot-android
An Android Application providing accessibility to the texts of the jewish book shelf with various options that functions as a search engine for texts (mekorot) within the Jewish Bookshelf. 

The user while interacting with the app (clicking on the floating action button) enters a name of a perek/parasha and receives the set of psukim of that makor.

At this moment, the psukim tab fills up with the psukim of the selected Makor, and the user has an option to mark a set of psukim of his choice. 

Once marked, when moving to the mekorot tab, he's provided with a set of mekorot that mention the marked psukim from the set. The mekorot shown in the mekorot tab are ordered by their relevance (number of psukim that they mention).

The user can filter the shown mekorot and in case he liked them, click the like button and add them to the favorites tab. 

Moreover, the user can view each makor and see the highlighted psukim that are referenced in that makor and browse through them, using the forward and backward buttons supplied in the shown makor text.

The project uses SPARQL queries to gather the data and show it in a convenient way.

### Table of Contents
**[Technologies used in this Project](#technologies-used-in-this-project)**<br>
**[Repository structure](#repository-structure)**<br>
**[General application flow](#general-application-flow)**<br>
**[Further UI tweaking](#further-ui-tweaking)**<br>
**[Creating new Queries](#creating-new-queries)**<br>
**[Executing queries](#executing-queries)**<br>

## Technologies used in this Project
- [Java](https://docs.oracle.com/javase/specs/)
- [SPARQL](https://www.w3.org/TR/rdf-sparql-query/)
- [Androjena](https://code.google.com/archive/p/androjena/)

## Repository structure
### libs
- **androjena_0.5** - Androjena is an Android port of Hewlett-Packard's Jena Semantic Framework
- **arqoid_0.5** - ARQoid is an Android port of Hewlett-Packard's ARQ SPARQL Query Engine
- **arqoid_0.5_sources** 
- **icu4j-3.4.5** - ICU4J is a set of Java libraries that provides more comprehensive support for Unicode
- **iri-0.8** - Support for Internationalised Resource Identifiers in Jena
- **lucenoid_3.0.2** - Lucenoid is a high-performance, full-featured text search engine library
- **slf4j-android-1.6.1-RC1** - The Simple Logging Facade for Java

### activities
- **SplashActivity** - Splash screen while queries are being loaded.
- **MainActivity** - Activity that holds all of the tabs. (Favorites, Mekorot, Psukim)
- **MakorDetailView** - Activity that shows the contents of a specific Makor.
- **MakorFavoriteView** - Activity that shows the contents of a favorite Makor in which you can share your Makor in either a text or link format.
- **SettingActivity** - Settings activity for choosing font families and sizes.

### adapters
- **FavoritesRecyclerViewAdapter** - Adapter for the favorites tab.
- **MekorotRecyclerViewAdapter** - Adapter for the mekorot tab.
- **PsukimRecyclerViewAdapter** - Adapter for the psukim tab.
- **ViewPagerAdapter** - Simple implementation of a view pager adapter.

### async
- **FetchHighlightsForMakorTask** - Fetch psukim to highlight indices in a makor (based on psukim list and a makor).
- **FetchMekorotByScoreTask** - Fetch Mekorot by score given a set of psukim.
- **FetchParashotAndPrakimTask** - Fetch all Parashot and Prakim.
- **FetchPsukimTask** - Fetch psukim from a given perek or parasha.

### fragments
- **FavoritesTab** - Implementation of the favorites tab.
- **MekorotTab** - Implementation of the mekorot tab.
- **PsukimTab** - Implementation of the psukim tab.

### models
- **CategoryModel** - Stores makor category information. (name, number of references)
- **MakorModel** - Stores makor information (name, author, text, number of psukim mentions, uri)
- **ParashotAndPrakim** - Stores Parashot and Prakim.
- **PasukModel** - Stores pasuk information (text, label, uri, whether the pasuk is selected)

### dialogs
- **PsukimListDialog** - Dialog that is activated by the FAB in the main activity.

### utils
- **FontUtils** - Used for font functionalities (setting size, setting text font)
- **PreferencesUtils** - Used in order to store information in the shared preferences.
- **WholeWordIndexFinder** - Used in order to match keyword in search strings.
- **IndexWrapper** - Used to wrap indices in the WholeWorldIndexFinder.

### other classes
- **JBSQueries** - Contains all the queries used in the project.

### assets
- **fonts** - Contains all the fonts used in the project.

## General application flow
- When the splash screen is loading, we're executing the _FetchParashotAndPrakimTask_ in order to get the prakim and parashot.
- Entering the _MainActivity_ we're seeing a FAB. Clicking on it lets us choose a perek or a parasha. Once chosen the _FetchPsukimTask_ is called in order to get the relevant psukim.
- Once we've chosen a set of psukim we click on the Mekorot tab and the _FetchMekorotByScoreTask_ is called in order to get the relevant mekorot and their filtering options.
- Finally, clicking on a specific Makor we enter the _MakorDetailView_ activity and call the _FetchHighlightsForMakorTask_ in order to get the psukim highlights for the specific makor (based on our psukim selection from phase 2 in the flow)

## Further UI tweaking
- App colors and styling can be changed easily from _values/styles.xml_ and _values/colors.xml_.
- App texts and strings can also be easily modified _from values/strings.xml_.
- App dimensions on specific UI elements can be tweaked form _values/dimens.xml_.

## Creating new Queries
In order to create and execute new Queries, you will first have to add your query to JBSQueries.java and afterwards have a look at any of the async classes aforementioned. (Thorough explanation below of the doInBackground method - mind the comments)


	ArrayList<String> queryResults = new ArrayList<>();
    try {
    	// In this case params[0] will be the query you want to execute
    	// Taken from the JBSQueries class.
        QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                params[0]);
        try {
        	// Executing the select
            ResultSet rs = queryEngineHTTP.execSelect();
            // Going over the result set.
            while (rs.hasNext()) {
                QuerySolution rb = rs.nextSolution();
                // According to the query you've executed you will receive a query solution
                // From this query solution you can extract the desired keywords
                String span = rb.get("YOUR_KEYWORD_HERE").toString();
                // Saving the desired output into an an list.
                queryResults.add(span);
            }
        } finally {
        	// Close the queryEngineHTTP stream.
            queryEngineHTTP.close();
        }


    } catch (Exception err) {
        err.printStackTrace();
    }


## Executing queries
Any queries written in _JBSQueries.java_ can be easily executed in the following link:

[http://tdk-p6.cs.technion.ac.il:8081/search.html](http://tdk-p6.cs.technion.ac.il:8081/search.html)

All you'll have to do is define the following:

- **eLinda endpoint as** [http://tdk-p6.cs.technion.ac.il:8081/](http://tdk-p6.cs.technion.ac.il:8081/)
- **JBS graph name as** [http://jbs.technion.ac.il](http://jbs.technion.ac.il)
- **Prefixes as**

		PREFIX jbr: <http://jbs.technion.ac.il/resource/>
		PREFIX jbo: <http://jbs.technion.ac.il/ontology/>
		PREFIX dco: <http://purl.org/dc/terms/>

- **And finally paste your query into the edit box**.

#### Notes 
- some queries are parametized, therefore you will need to replace the variables with actual strings.
- Other queries you can play around with can be found here: [Google Doc](https://docs.google.com/document/d/1MhTRhy99P_DytAVrMufJRUMmfHu16vKDs2y2jmT3pXo/edit)