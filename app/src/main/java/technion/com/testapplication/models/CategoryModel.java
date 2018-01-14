package technion.com.testapplication.models;

/**
 * Created by tomerlevinson on 14/01/2018.
 */
public class CategoryModel {
    private String mCategoryName;
    private String mCategoryRefernceNum;

    public CategoryModel(String categoryName, String referenceNumber) {
        mCategoryName = categoryName;
        mCategoryRefernceNum = referenceNumber;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryRefernceNum() {
        return mCategoryRefernceNum;
    }
}
