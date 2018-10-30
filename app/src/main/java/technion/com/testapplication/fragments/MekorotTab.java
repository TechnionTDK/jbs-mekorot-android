package technion.com.testapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.MekorotRecyclerViewAdapter;
import technion.com.testapplication.data_manage.DataManager;
import technion.com.testapplication.data_manage.MekorotForPsukim;
import technion.com.testapplication.models.CategoryModel;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class MekorotTab extends Fragment {
    private ArrayList<String> mPrefixedPsukimUris;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Map<String, CategoryModel> mMekorotCategories;
    public static ArrayList<MakorModel> MekorotModels = new ArrayList<>();
    private static final int CATEGORY_STRING_LENGTH = 9;
    private static ArrayList<Integer> mDialogSelectedItems = new ArrayList<>();
    private static ArrayList<String> mDialogSelectedItemsNames = new ArrayList<>();
    MekorotChangesListener mCallback;
    private static final String CATEGORY_NUM_OPEN_BRACE = " (";
    private static final String CATEGORY_NUM_CLOSE_BRACE = ")";
    private HashMap<String, MakorModel> mMekorotModels;
    private HashMap<String, MakorModel> mFilteredMekorotModels;
    private HashMap<String, ArrayList<String>> mCategoryToBooks;

    public MekorotTab() {
    }

    public void setRecyclerViewAdapter(HashMap<String, MakorModel> mekorot) {
        if (getView() != null)
        {
            MekorotModels.clear();
            for (Object o : mekorot.entrySet())
            {
                Map.Entry pair = (Map.Entry) o;
                MakorModel makorModel = (MakorModel) pair.getValue();
                MekorotModels.add(makorModel);
            }
            Collections.sort(MekorotModels, new MakorComparator());
            Collections.reverse(MekorotModels);
            mRecyclerView = getView().findViewById(R.id.recycler_view);
            mAdapter = new MekorotRecyclerViewAdapter(mPrefixedPsukimUris, MekorotModels,
                                                      getContext(), mCallback);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            setFilterDialog();
            mCallback.setMekorotTabResultsNum(MekorotModels.size());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mCallback = (MekorotChangesListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                                                 + " must implement MekorotChangesListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Clear filtering dialogs - called from main activity upon new query submitted.
     */
    public void clearFilterDialogSelections() {
        mDialogSelectedItems.clear();
        mDialogSelectedItemsNames.clear();
    }

    public void clear() {
        if (mRecyclerView != null)
        {
            mRecyclerView.removeAllViewsInLayout();
            mRecyclerView.setAdapter(null);
        }

    }

    /**
     * Sets the filter dialog in the mekorot tab.
     */
    private void setFilterDialog() {
        ArrayList<String> prettifiedCategories = new ArrayList<>();
        final ArrayList<CategoryModel> mekorotCategories = new ArrayList<>(mMekorotCategories.values());
        Collections.sort(mekorotCategories, new Comparator<CategoryModel>() {
            @Override
            public int compare(CategoryModel cm1, CategoryModel cm2) {
                return Integer.valueOf(cm2.getCategoryRefernceNum()) -
                        Integer.valueOf(cm1.getCategoryRefernceNum());
            }
        });
        for (CategoryModel category : mekorotCategories)
        {
            String prettifiedCategory = category.getCategoryName().substring(
                    CATEGORY_STRING_LENGTH).replace("_", " ")
                    + CATEGORY_NUM_OPEN_BRACE
                    + category.getCategoryRefernceNum()
                    + CATEGORY_NUM_CLOSE_BRACE;
            prettifiedCategories.add(prettifiedCategory);
        }
        prettifiedCategories.add("אפס בחירה");
        final CharSequence[] items = prettifiedCategories.toArray(
                new CharSequence[prettifiedCategories.size()]);
        final boolean[] checkedItemsPositions = new boolean[prettifiedCategories.size()];
        for (Integer selectedItem : mDialogSelectedItems)
        {
            checkedItemsPositions[selectedItem] = true;
        }
        final MekorotTab selfie = this;
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.choose_category_to_filter))
                .setMultiChoiceItems(items, checkedItemsPositions,
                                     new DialogInterface.OnMultiChoiceClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int indexSelected,
                                                             boolean isChecked) {
                                             if (indexSelected == checkedItemsPositions.length - 1)
                                             {
                                                 for (int i = 0; i < checkedItemsPositions.length; i++)
                                                 {
                                                     checkedItemsPositions[i] = false;
                                                     ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                                                 }
                                                 mDialogSelectedItems.clear();
                                                 mDialogSelectedItemsNames.clear();
                                             }

                                             ArrayList<CategoryModel> mekorotCategories = new ArrayList<>(mMekorotCategories.values());
                                             Collections.sort(mekorotCategories, new Comparator<CategoryModel>() {
                                                 @Override
                                                 public int compare(CategoryModel cm1, CategoryModel cm2) {
                                                     return Integer.valueOf(cm2.getCategoryRefernceNum()) -
                                                             Integer.valueOf(cm1.getCategoryRefernceNum());
                                                 }
                                             });
                                             if (isChecked && indexSelected != checkedItemsPositions.length - 1)
                                             {
                                                 // If the user checked the item, add it to the selected items
                                                 mDialogSelectedItems.add(indexSelected);
                                                 String prefixedSelection = mekorotCategories.get(
                                                         indexSelected).getCategoryName();
                                                 mDialogSelectedItemsNames.add(prefixedSelection);
                                             }
                                             else if (mDialogSelectedItems.contains(indexSelected))
                                             {
                                                 // Else, if the item is already in the array, remove it
                                                 mDialogSelectedItems.remove(Integer.valueOf(indexSelected));
                                                 String prefixedSelection = mekorotCategories.get(
                                                         indexSelected).getCategoryName();
                                                 mDialogSelectedItemsNames.remove(prefixedSelection);
                                             }
                                         }
                                     })
                .setPositiveButton(getResources().getString(R.string.choose_button),
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int id) {
                                           if (mDialogSelectedItemsNames.size() > 0)
                                           {
                                               ArrayList<CategoryModel> selectedCategories = new ArrayList<>();
                                               for (String selectedItemName : mDialogSelectedItemsNames)
                                               {
                                                   selectedCategories.add(mMekorotCategories.get(selectedItemName));
                                               }
                                               mFilteredMekorotModels = new HashMap<>();
                                               ArrayList<String> relevantBooks = new ArrayList<>();
                                               ArrayList<MakorModel> mekorot = new ArrayList<>();
                                               for (MakorModel makor : mMekorotModels.values())
                                               {
                                                   mekorot.add(makor);
                                               }
                                               for (CategoryModel cm : selectedCategories)
                                               {
                                                   ArrayList<String> books = mCategoryToBooks.get(cm.getCategoryUri());
                                                   relevantBooks.addAll(books);
                                               }
                                               for (MakorModel makor : mekorot)
                                               {
                                                   if (relevantBooks.contains(makor.getMakorBook()))
                                                   {
                                                       mFilteredMekorotModels.put(makor.getMakorUri(), makor);
                                                   }
                                               }
                                               selfie.setRecyclerViewAdapter(mFilteredMekorotModels);
                                           }
                                           else
                                           {
                                               selfie.setRecyclerViewAdapter(mMekorotModels);
                                           }
                                       }
                                   }).setNegativeButton(getResources().getString(R.string.cancel_button),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.dismiss();
                                                            }
                                                        }).create();
        mCallback.setFilterIcon(dialog);
    }

    /**
     * Main activity uses this to communicate with the fragment
     * upon a message from the favorites, that it has changed.
     */
    public void notifyFromFavorites() {
        if (mAdapter != null)
        {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Run the mekorot and categories query.
     *
     * @param psukimUris
     */
    public void runMekorotAndCategoriesQueries(ArrayList<String> psukimUris) {
        mPrefixedPsukimUris = new ArrayList<>();
        for (int i = 0; i < psukimUris.size(); i++)
        {
            String pasukUri = psukimUris.get(i);
            pasukUri = pasukUri.substring(pasukUri.lastIndexOf("/") + 1);
            pasukUri = getResources().getString(R.string.jbr_prefix) + pasukUri;
            mPrefixedPsukimUris.add(i, pasukUri);
        }
        String mekorotQuery = JBSQueries.getMekorotWithAllData(mPrefixedPsukimUris);
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
    }

    public void setCategoryToBooks(HashMap<String, ArrayList<String>> categoryToBooks) {
        mCategoryToBooks = categoryToBooks;
    }

    /**
     * Used in order to communicate with the main activity in the following scenarios:
     * 1) Filter icon should be enabled.
     * 2) Set tab number of results.
     * 3) Update number of favorites.
     */
    public interface MekorotChangesListener {
        void setFilterIcon(Dialog dialog);

        void setMekorotTabResultsNum(int numOfResults);

        void updateFavoritesNum(int numOfFavorites);
    }

    public class MakorComparator implements Comparator<MakorModel> {
        @Override
        public int compare(MakorModel makorModel, MakorModel makorModel1) {
            return Integer.parseInt(makorModel.getNumOfPsukimMentions()) - Integer.parseInt(
                    makorModel1.getNumOfPsukimMentions());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mekorot_tab, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
        {
            mAdapter.notifyDataSetChanged();
        }
    }
}
