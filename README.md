# "Sulamot" app for the Jewish Bookshelf
An Android application providing accessibility to the texts of the jewish book shelf with various options that function as a search engine for texts (mekorot) within the Jewish Bookshelf.

We're accessing the jewish book shelf via a linked data set built on top on RDF using SPARQL.
The jewish book shelf linked data sets consists of the following:
- **Defining a JBS ontology** (classes and properties).
- **Representing the structure of various Jewish texts in RDF format, based on the defined ontology.**
- **Conducting text analysis tasks, and representing the results in RDF.**

## Basic app flow

- Getting the text of Parasha/Perek from the Tanach.
- Selecting psukim (verses), or selecting all the psukim.
- Viewing mekorot connecting to a set of psukim selected.
- Liking a makor puts in in the favorites section (tab).
- Clicking on a makor in the mekorot tab takes you to the makor view.
- Accessing the settings screen is enabled via the main screen (And enables you to change font family and size).
- Viewing Mekorot highlights according to the set of psukim chosen is possible via the makor view.

## pptx Presentation
**[Presentation](/jbs-mekorot-android.pptx)**<br>

## pptx Presentation - project continuation
**[Presentation](/continuation_presentation.pptx)**<br>

## Table of Contents
**[Installation Instructions](#installation-instructions)**<br>
**[Technologies used in this Project](#technologies-used-in-this-project)**<br>
**[Project structure](#project-structure)**<br>
**[General application flow](#general-application-flow)**<br>
**[Further UI tweaking](#further-ui-tweaking)**<br>
**[Creating new Queries](#creating-new-queries)**<br>
**[Executing queries](#executing-queries)**<br>

## Installation instructions
- cd YOUR_DESIRED_DIRECTORY
- git clone https://github.com/TechnionTDK/jbs-mekorot-android.git
- Open Android Studio.
- Load project from YOUR_DESIRED_DIRECTORY
- Click on run.

## Technologies used in this Project
The project uses SPARQL queries to gather the data and show it in a convenient way.
- [Java](https://docs.oracle.com/javase/specs/)
- [SPARQL](https://www.w3.org/TR/rdf-sparql-query/)
- [Androjena](https://github.com/lencinhaus/androjena)

Then open the project in Android Studio. We recommend using Android Studio version 3.

## Project structure
### libs
- **androjena_0.5** - Androjena is an Android port of Hewlett-Packard's Jena Semantic Framework
- **arqoid_0.5** - ARQoid is an Android port of Hewlett-Packard's ARQ SPARQL Query Engine
- **arqoid_0.5_sources** 
- **icu4j-3.4.5** - ICU4J is a set of Java libraries that provides more comprehensive support for Unicode
- **iri-0.8** - Support for Internationalised Resource Identifiers in Jena
- **lucenoid_3.0.2** - Lucenoid is a high-performance, full-featured text search engine library
- **slf4j-android-1.6.1-RC1** - The Simple Logging Facade for Java
- **volley** - Enables post/get requests to a remote server

**Note:** all these jars are not exported via the build.gradle file, but were manually added to the project.
Also note that they all required for the operation of the Androjena library. These jars were built from sources as explained [here](https://github.com/lencinhaus/androjena).

### activities
- **SplashActivity** - Splash screen while queries that extract parasha/perek labels are being loaded, and the application cache is being validated.
- **NavActivity** - Activity that holds all of the tabs (Mekorot, Psukim) and the Navigation Drawer.
- **ResultsActivity** - Activity that shows the contents of a specific Makor.
- **FavoritesActivity** - Activity that shows the contents of a favorite Makor in which you can share your Makor in either a text or link format.
- **SettingActivity** - Settings activity for choosing font families and sizes.

### adapters
- **FavoritesRecyclerViewAdapter** - Adapter for the favorites tab.
- **MekorotRecyclerViewAdapter** - Adapter for the mekorot tab.
- **PsukimRecyclerViewAdapter** - Adapter for the psukim tab.
- **ViewPagerAdapter** - Simple implementation of a view pager adapter.
- **ResultsCollectionPageAdapter** - Adapter for the results pager which allows swiping between results.

### async
- **FetchHighlightsForMakorTask** - Fetch psukim to highlight indices in a makor (based on psukim list and a makor).
- **FetchMekorotByScoreTask** - Fetch Mekorot by score given a set of psukim.
- **FetchParashotAndPrakimTask** - Fetch all Parashot and Prakim.
- **FetchPsukimTask** - Fetch psukim from a given perek or parasha.
- **FetchPsukimBySubstrTask** - Fetch all the psukim for a given sub string.
- **PostRequester** - Allows asynchronous POST requests functionality.

### fragments
- **MekorotTab** - Implementation of the mekorot tab.
- **PsukimTab** - Implementation of the psukim tab.

### models
- **CategoryModel** - Stores makor category information. (name, number of references)
- **MakorModel** - Stores makor information (name, author, text, number of psukim mentions, uri)
- **ParashotAndPrakim** - Stores Parashot and Prakim.
- **PasukModel** - Stores pasuk information (text, label, uri, whether the pasuk is selected)
- **ResultModel** - Stores the information for a result (text, title, uri)
- **ErrorModel** - Stores information of an error report (makor uri, issue_text, free_text, type, range)

### dialogs
- **PrakimParashotListDialog** - Dialog that is activated by the FAB in the main activity.

### utils
- **FontUtils** - Used for font functionalities (setting size, setting text font)
- **PreferencesUtils** - Used in order to store information in the shared preferences.
- **WholeWordIndexFinder** - Used in order to match keyword in search strings (used for makor highlighting).
- **IndexWrapper** - Used to wrap indices in the WholeWorldIndexFinder (used for makor highlighting).
- **AutoResizeTextView** - Used to display a title element with dynamic font size (for results with long titles).

### data_manage
- **Cacheable** - An abstract class to define an interface for a cacheable object.
- **DataManager** - A class that abstracts the source of the data (cache, remote) and manages caching functionality.
- **InternalStorage** - A class that exposes an interface to local device IO functions.
- **MekorotForPsukim** - Implements Cacheable, wraps the async fetch functionality for the DataManager and stores the data.
- **PsukimForParasha** - Same as MekorotForPsukim but for different data.

### other classes
- **JBSQueries** - Contains all SPARQL queries used in the project.

### assets
- **fonts** - Contains all the fonts used in the project.
// explain how to add new fonts, provide link to http://freefonts.co.il/

## General application flow
- When the splash screen is loading, cached data validation is performed. Any cached data that wasn't used for more than 2 weeks is deleted. Also, We're executing the _FetchParashotAndPrakimTask_ in order to get the prakim and parashot.
- Entering the _NavActivity_ we're seeing a FAB. Clicking on it lets us choose a perek or a parasha. Once chosen the _FetchPsukimTask_ is called or the data is read from the cache, in order to get the relevant psukim.
- Once we've chosen a set of psukim we click on the Mekorot tab and the _FetchMekorotByScoreTask_ is called or the data is read from the cache, in order to get the relevant mekorot and their filtering options.
- Finally, clicking on a specific Makor we enter the _MakorDetailView_ activity and call the _FetchHighlightsForMakorTask_ in order to get the psukim highlights for the specific makor (based on our psukim selection from phase 2 in the flow).
- Clicking on a highlighted text, or manually selecting text allows the user to report an error, in case he found one.

## Further UI tweaking
- App colors and styling can be changed easily from _values/styles.xml_ and _values/colors.xml_.
- App texts and strings can also be easily modified _from values/strings.xml_.
- App dimensions on specific UI elements can be tweaked form _values/dimens.xml_.

## How to add and execute new SPARQL queries
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

# Cacheable Data
- The caching feature allows storing SPARQL responses locally on the device, providing a smoother user experience by skipping the long wait for queries that can take up to 20 seconds.
- Cacheable data lifetime is 2 weeks from the last time that the data was requested.
- The following models are included in the caching mechanism:
-- MekorotForPsukim: The Mekorot results for a set of Psukim
-- PsukimForParashaPerek: The relevant Psukim to the chosen Perek/Parasha
-- CategoryToBooks: The mapping between one Category to a set of Books

## How to add a new cacheable data object
The heaviest remote data requests are already being saved into the cache to allow better user experience, although the _DataManager_ architecture enables the developer to add any other data to the cache for future use.
To add a cacheable data object:
```
public class MekorotForPsukim extends Cacheable {
    // Data specific members
    private ArrayList<String> mPsukimList;
    private ArrayList<Object> mMekorotModelCategoryArray;
    private String mMekorotQuery;
    private boolean mShouldFilter;
    // End of data specific members
    
    private Fragment mOwnerFrag;

    public MekorotForPsukim(ArrayList<String> mPsukimList, Fragment mOwnerFrag, boolean shouldFilter, String mekorotQuery) {
        this.mPsukimList = mPsukimList;
        this.mOwnerFrag = mOwnerFrag;
        mShouldFilter = shouldFilter;
        mMekorotQuery = mekorotQuery;
    }

    @Override
    public String getKey() {
    	// Return a unique key per different data
	// The following examples creates a unique hash string per query
        return "MEKOROT_FOR_PSUKIM_" + mMekorotQuery.toString().hashCode() * (mShouldFilter ? -1 : 1);
    }

    @Override
    public ArrayList<Object> getData() {
        return mMekorotModelCategoryArray;
    }

    @Override
    public void setData(Object data) {
        mMekorotModelCategoryArray = (ArrayList<Object>) data;
    }

    @Override
    public void FetchDataAsync(Runnable onComplete) {
        String categoriesQuery = JBSQueries.getCategoriesByPsukimWithReferenceNumber(
                mPsukimList);
        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(mOwnerFrag, mShouldFilter, this, onComplete);
        fetchMekorotByScoreTask.execute(mMekorotQuery, categoriesQuery);
    }
}
```

An instance such as the above can now be managed by the DataManager, saved in cache and read from it in case it is found in the cache. A callback function allows running code when the DataManager completes retrieving the data.

```
	final MekorotTab selfie = this;
        final MekorotForPsukim mekorotForPsukim = new MekorotForPsukim(mPrefixedPsukimUris, this, false, mekorotQuery);
        Runnable onComplete = new Runnable() {
            @Override
            public void run() {
                mMekorotCategories = new TreeMap<>();
                mMekorotModels = (HashMap<String, MakorModel>) mekorotForPsukim.getData().get(0);
                ArrayList<CategoryModel> categoryModels = (ArrayList<CategoryModel>) mekorotForPsukim.getData().get(1);
                for (CategoryModel cm : categoryModels)
                {
                    mMekorotCategories.put(cm.getCategoryName(), cm);
                }
                selfie.setRecyclerViewAdapter(mMekorotModels);
            }
        };
        DataManager dataManager = new DataManager(this.getContext());
        dataManager.getData(mekorotForPsukim, onComplete);
```

#### Notes 
- some queries are parametized, therefore you will need to replace the variables with actual strings.
- Other queries you can play around with can be found here: [Google Doc](https://docs.google.com/document/d/1MhTRhy99P_DytAVrMufJRUMmfHu16vKDs2y2jmT3pXo/edit)

# Remote Linux Server
As a part of the error reporting feature added to the project, a remote linux server is initialized and installed with local MySQL to store the error reports which arrive from the application users.
The following section will describe the following:
- Initializing the linux server
- Installing and configuring the necessary packages on the server
- Installing local MySQL DB
- Installing and running Apache2
- Installing and configuring PHP
- Creating web interface for the local MySQL using phpMyAdmin tool

## Initialize the linux server
- Updating and upgrading all the existing packages:
```
$ sudo apt-get update
$ sudo apt-get upgrade
```

#### Install MySQL:
```
$ sudo apt-get mysql-server
```

#### Installing and configuring Apache2
During installation of apache2, depending on the machine that it is being installed on - several issues can appear. The following guide addresses such issues and explains what needs to be done in order to complete the installation.
**guide for apache2** https://www.digitalocean.com/community/tutorials/how-to-install-the-apache-web-server-on-ubuntu-16-04
``` 
$ sudo apt-get install apache2
$ sudo ufw app list
$ sudo ufw allow 'Apache Full'
```
#### Install php7.0
```
$ sudo apt-get install php7.0 libapache2-mod-php7.0 
$ sudo a2enmod php7.0
$ sudo service apache2 restart
```

#### Install phpMyAdmin
During installation of phpMyAdmin, depending on the machine that it is being installed on - several issues can appear. The following guide addresses such issues and explains what needs to be done in order to complete the installation.
**installation guide** https://www.digitalocean.com/community/tutorials/how-to-install-and-secure-phpmyadmin-on-ubuntu-16-04
```
$ sudo apt-get install phpmyadmin php-mbstring php-gettext
```
- During the installation you will be required to define a username and password.
- When asked, select "Configure with Apache2"
- When asked, choose to set up database with dbconfig-common

#### Configure the SQL DB:
- run the mysql, create the database and the table, create user and grant permissions:
```
$ mysql -u root -p
[root password]
$ create database mekorot;
$ grant select,insert,update,delete,create,drop on mekorot.* to 'phpmyadmin'@localhost
```

- In order to create the 'mekorot' table, please use the phpMyAdmin tool as explained below.
- It is recommended to create a dedicated user for the mekorot database and not to use the root user for remote access to the DB due to security reasons.
- Further instructions will refer to the SQL DB user as [SQL_USER]

- Server should now be up and running, and the SQL is accessible through the phpMyAdmin.

#### Creating a PHP file that allows connection to the SQL DB (dbconnection.php)
- The following php file should be placed at the server directory /var/www/db/
This file creates the object "$dbconnection" which allows accessing the local database from other php files, and the access to it is only enabled locally.
- (Please notice that parameters with [] are macros that should be replaced with the corresponding info)
```
	<?php
	header('Content-Type: text/html; charset=utf-8');

	$db_username="[SQL_USER]";
	$db_password="[PASSWORD]";

	try {
	    $dbconnection = new PDO('mysql:host=localhost;port=3306;dbname=mekorot', $db_username, $db_password, array(PDO::ATTR_PERSISTENT => true
	    ));
	} catch(PDOException $e) {

	}
	$dbconnection->query("SET NAMES 'utf8'");

	?>
```

#### Creating the php report error interface (db_functions.php)
This file should be placed at /var/www/db/
```
	<?php
	header('Content-Type: text/html; charset=utf-8');

	require_once "dbconnection.php";

	function reportError($makorUri, $makorRange, $issueText, $freeText, $reportType)
	{
	    global $dbconnection;

	    $dbconnection->beginTransaction();

	    $stmt = $dbconnection->prepare('INSERT INTO error_reports 
					  (makor_uri,makor_range,issue_text,free_text,report_type,date)
					  VALUES (:makor_uri, :makor_range, :issue_text, :free_text, :report_type, NOW())');
	    $result = $stmt->execute(array(':makor_uri' => $makorUri, ':makor_range' => $makorRange, ':issue_text' => $issueText,
		':free_text' => $freeText, ':report_type' => $reportType));

	    if ($result != true)
	    {
		$dbconnection->rollBack();
		return false;
	    }
	    $dbconnection->commit();

	    return true;
	}
```

#### Creating the php interface for the android application (error_report.php)
- This file should be placed at /var/www/html/
- This file is the endpoint for the android application.
```
	<?php
	require_once "../db/db_functions.php";

	$result = false;
	$message = "";

	if (!isset($_POST['makor_uri']) || !isset($_POST['report_type']))
	{
	    $message = 'error_report.php: lacks necessary POST parameters.';
	    goto result;
	}

	$makorUri = $_POST['makor_uri'];
	$makorRange = $_POST['makor_range'];
	$issueText = $_POST['issue_text'];
	$freeText = $_POST['free_text'];
	$reportType = $_POST['report_type'];

	$result = reportError($makorUri, $makorRange, $issueText, $freeText, $reportType);

	result:
	echo json_encode(array('result' => $result, 'error' => !$result, 'message' => $message,
	    'params' => array($makorUri, $makorRange, $issueText, $reportType)));
	exit;
```

# PostRequester Class
- This class simplifies the POST request API, using _Volley_ library for html requests.
 
## API

**public PostRequester(Context context)**
Constructor

**public void SendRequest(final String URL, final Map<String, String> Params, final Runnable OnResponse, final Runnable OnError)**
- Dispatches the asynchrouos POST request to the given URL, adding the supplied Parameters (Params).
- Upon request reponse and after parsing the response ends successfully, the OnResponse callback is invoked.
- Upon any error in the request or during it's parse, the OnError callback is invoked.
- Usage example: the following method reports an error to the remote server
```
private void reportError(ErrorModel error) {
        final ResultsActivity selfie = this;
        Map<String, String> params = new HashMap<>();
        params.put("makor_uri", error.makorUri);
        params.put("makor_range", error.makorRange);
        params.put("issue_text", error.issueText);
        params.put("free_text", error.freeText);
        params.put("report_type", error.reportType.toString());

        Runnable onResponse = new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(selfie, "דיווח נשלח בהצלחה", Toast.LENGTH_SHORT).show();
            }
        };

        Runnable onError = new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "דיווח שגיאה נכשל", Toast.LENGTH_SHORT).show();
            }
        };

        PostRequester postRequester = new PostRequester(this);
        postRequester.SendRequest("http://haggaidev.com/mekorot/html/error_report.php", params, onResponse, onError);
    }
```

- The POST request response is stored inside the PostRequest class member: 
**private org.json.JSONObject JsonObject;**

# Major changes

## Results filtering by categories
- Filtering the results is a common action for the user. In the previous implementation, for each filtering selection a new query would be dispatched, taking notable time to return.
- The current implementation uses the relational structure of Mekorot and Categories to infer the relevant set of Mekorot per selected filtering category.
- Explanation of the filtering flow:
1. User filters results, he selects one or more categories
2. Creating a set of Books, by using the mapping _Category -> Set of Books_ for each category
3. Create the set of filetered Mekorot, by using the mapping _Makor -> Book_. each makor with a book that appears in the books set from step 2 is included in the filetered Mekorot set.

## Crowd sourcing
- Error reporting feature, runs in the _Results_ screen, allowes users to report errors about the result.
- Reporting is enabled through the menu, or through highlighted text or manual user text selection.
- The application instances a data model with the error details and reports it to the remote server.
- The error is sent to the remote server using the _PostRequester_ class.
- The error reporting code runs through the _ResultsActivity_, through the menu flow, through highlighted text click callback, and through a custom menu that appears when user manually selects text from the result text.
- Highlighted text callback is instanced inside the method _highlightPsukim_.
- The custom menu that appears during user manual text selection is defined by using _setCustomSelectionActionModeCallback_ which is a class method of TextView. **An important note about custom menu - the only device that doesn't support the custom menu is MIUI4 (Xiaomi MI 4)**
- Managing the reported errors is enabled through the phpMyAdmin interface.
