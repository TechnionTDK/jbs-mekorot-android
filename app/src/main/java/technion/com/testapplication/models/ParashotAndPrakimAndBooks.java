package technion.com.testapplication.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tomerlevinson on 18/12/2017.
 * Used to store prakim.
 */
public class ParashotAndPrakimAndBooks {
    private ArrayList<String> mParashot;
    private ArrayList<String> mPrakim;
    private HashMap<String, ArrayList<String>> mCategoryToBooks;

    public ParashotAndPrakimAndBooks(ArrayList<String> parashot, ArrayList<String> prakim,
                                     HashMap<String, ArrayList<String>> categoryToBooks) {
        mParashot = parashot;
        mPrakim = prakim;
        mCategoryToBooks = categoryToBooks;
    }

    public ArrayList<String> getParashot() {
        return mParashot;
    }

    public ArrayList<String> getPrakim() {
        return mPrakim;
    }

    public HashMap<String, ArrayList<String>> getCategoryToBooks() {
        return mCategoryToBooks;
    }
}
