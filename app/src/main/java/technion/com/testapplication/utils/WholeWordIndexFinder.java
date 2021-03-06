package technion.com.testapplication.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tomerlevinson on 18/02/2018.
 * Used in order to match keyword in search strings.
 */
public class WholeWordIndexFinder {

    private String searchString;

    public WholeWordIndexFinder(String searchString) {
        this.searchString = searchString;
    }

    public List<IndexWrapper> findIndexesForKeyword(String keyword) {
        String regex = "\b*" + keyword + "\b*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchString);

        List<IndexWrapper> wrappers = new ArrayList<IndexWrapper>();

        while (matcher.find() == true) {
            int end = matcher.end();
            int start = matcher.start();
            IndexWrapper wrapper = new IndexWrapper(start, end);
            wrappers.add(wrapper);
        }
        return wrappers;
    }


}