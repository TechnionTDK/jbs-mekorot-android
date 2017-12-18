package technion.com.testapplication.models;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 18/12/2017.
 */
public class ParashotAndPrakim {
    private ArrayList<String> mParashot;
    private ArrayList<String> mPrakim;

    public ParashotAndPrakim(ArrayList<String> parashot, ArrayList<String> prakim) {
        mParashot = parashot;
        mPrakim = prakim;
    }

    public ArrayList<String> getParashot() {
        return mParashot;
    }

    public ArrayList<String> getPrakim() {
        return mPrakim;
    }
}
