package technion.com.testapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ListViewItemCheckboxBaseAdapter;
import technion.com.testapplication.adapters.MekorotRecyclerViewAdapter;
import technion.com.testapplication.data_manage.DataManager;
import technion.com.testapplication.data_manage.MekorotForPsukim;
import technion.com.testapplication.models.CategoryModel;
import technion.com.testapplication.models.MakorModel;
import technion.com.testapplication.utils.ListViewItemDTO;

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

    // Return an initialize list of ListViewItemDTO.
    private List<ListViewItemDTO> getInitViewItemDtoList(CharSequence[] items, boolean[] checkedStatus) {
        List<ListViewItemDTO> ret = new ArrayList<>();
        for (int i = 0; i < items.length; i++)
        {
            String itemText = items[i].toString();
            ListViewItemDTO dto = new ListViewItemDTO();
            dto.setChecked(checkedStatus[i]);
            dto.setItemText(itemText);
            ret.add(dto);
        }
        return ret;
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
        final CharSequence[] items = prettifiedCategories.toArray(new CharSequence[prettifiedCategories.size()]);
        final boolean[] checkedItemsPositions = new boolean[prettifiedCategories.size()];
        for (Integer selectedItem : mDialogSelectedItems)
        {
            checkedItemsPositions[selectedItem] = true;
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        final ListView listViewWithCheckbox = dialogView.findViewById(R.id.filtering_listview);
        final List<ListViewItemDTO> initItemList = this.getInitViewItemDtoList(items, checkedItemsPositions);
        final ListViewItemCheckboxBaseAdapter listViewDataAdapter = new ListViewItemCheckboxBaseAdapter(getContext(), initItemList);
        listViewDataAdapter.notifyDataSetChanged();
        listViewWithCheckbox.setAdapter(listViewDataAdapter);
        // When list view item is clicked.
        listViewWithCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);
                ListViewItemDTO itemDto = (ListViewItemDTO) itemObject;
                CheckBox itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
                if (itemDto.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                }
                else
                {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }
                ArrayList<CategoryModel> mekorotCategories = new ArrayList<>(mMekorotCategories.values());
                Collections.sort(mekorotCategories, new Comparator<CategoryModel>() {
                    @Override
                    public int compare(CategoryModel cm1, CategoryModel cm2) {
                        return Integer.valueOf(cm2.getCategoryRefernceNum()) -
                                Integer.valueOf(cm1.getCategoryRefernceNum());
                    }
                });
                if (itemCheckbox.isChecked())
                {
                    // If the user checked the item, add it to the selected items
                    mDialogSelectedItems.add(itemIndex);
                    String prefixedSelection = mekorotCategories.get(itemIndex).getCategoryName();
                    mDialogSelectedItemsNames.add(prefixedSelection);
                }
                else if (mDialogSelectedItems.contains(itemIndex))
                {
                    // Else, if the item is already in the array, remove it
                    mDialogSelectedItems.remove(Integer.valueOf(itemIndex));
                    String prefixedSelection = mekorotCategories.get(itemIndex).getCategoryName();
                    mDialogSelectedItemsNames.remove(prefixedSelection);
                }
            }
        });

        final MekorotTab selfie = this;

        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();

        dialogView.findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogSelectedItemsNames.size() > 0)
                {
                    ArrayList<CategoryModel> selectedCategories = new ArrayList<>();
                    for (String selectedItemName : mDialogSelectedItemsNames)
                    {
                        selectedCategories.add(mMekorotCategories.get(selectedItemName));
                    }
                    mFilteredMekorotModels = new HashMap<>();
                    ArrayList<String> relevantBooks = new ArrayList<>();
                    ArrayList<MakorModel> mekorot = new ArrayList<>(mMekorotModels.values());
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
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_clear_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfie.clearFilterDialogSelections();
                int size = initItemList.size();
                for (int i = 0; i < size; i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);
                    dto.setChecked(false);
                }
                listViewDataAdapter.notifyDataSetChanged();
            }
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
