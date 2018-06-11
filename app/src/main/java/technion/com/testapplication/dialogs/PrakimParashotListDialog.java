package technion.com.testapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ViewPagerAdapter;
import technion.com.testapplication.fragments.PsukimTab;

/**
 * Created by tomerlevinson on 27/01/2018.
 * This is the dialog that is activated by the FAB in the main activity.
 */
public class PrakimParashotListDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText;
    private ArrayList<String> mAdapterArrayList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mParashot;
    private ArrayList<String> mPrakim;
    private Context mContext;
    private int mSpinnerCheck = 0;
    private ArrayList<Pair<String, String>> mParashotURILabelPairs;
    private ArrayList<Pair<String, String>> mPrakimURILabelPairs;
    private ViewPagerAdapter mViewPagerAdapter;
    private Activity mHostActivity;
    private static final int PSUKIM_TAB_INDEX = 0;

    public PrakimParashotListDialog(Context context, ArrayList<String> prakim, ArrayList<String> parashot,
                                    ArrayList<Pair<String, String>> parashotURILabelPairs,
                                    ArrayList<Pair<String, String>> prakimURILabelPairs,
                                    ViewPagerAdapter viewPagerAdapter,
                                    Activity activity) {
        super(context);

        /* Design the dialog in main.xml file */
        setContentView(R.layout.psukim_dialog);
        mHostActivity = activity;
        mParashot = parashot;
        mPrakim = prakim;
        mContext = context;
        mPrakimURILabelPairs = prakimURILabelPairs;
        mParashotURILabelPairs = parashotURILabelPairs;
        mViewPagerAdapter = viewPagerAdapter;
        filterText = this.findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        setSpinner();
        setAdapterForListView();
        setSubstrSearchForBtn();
    }

    private void setSubstrSearchForBtn() {
        final Dialog thisDialog = this;
        this.findViewById(R.id.btn_search_substr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pasukSubstr = ((TextView) thisDialog.findViewById(R.id.EditBox)).getText().toString();
                PsukimTab psukimTabFrag = (PsukimTab) mViewPagerAdapter.getItem(0);
                TabLayout tabs = mHostActivity.findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(PSUKIM_TAB_INDEX);
                if (tab != null)
                {
                    tab.select();
                }
                String spinnerText = ((Spinner) thisDialog.findViewById(R.id.spinner_nav)).getSelectedItem().toString();
                String headingText = spinnerText + ": " + pasukSubstr;
                psukimTabFrag.loadPuskimBySubstr(pasukSubstr, headingText);
                thisDialog.dismiss();
            }
        });
    }

    private void setAdapterForListView() {
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAdapterArrayList);
        list = this.findViewById(R.id.List);
        list.setAdapter(mAdapter);
        final Dialog thisDialog = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String spinnerText = ((Spinner) thisDialog.findViewById(R.id.spinner_nav)).getSelectedItem().toString();
                String perekOrParashaName = list.getItemAtPosition(position).toString();
                ArrayList<Pair<String, String>> perekOrParashaPairs;
                if (spinnerText.equals(mContext.getResources().getString(R.string.parasha)))
                {
                    perekOrParashaPairs = mParashotURILabelPairs;
                }
                else
                {
                    perekOrParashaPairs = mPrakimURILabelPairs;
                }
                PsukimTab psukimTabFrag = (PsukimTab) mViewPagerAdapter.getItem(0);
                TabLayout tabs = mHostActivity.findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(PSUKIM_TAB_INDEX);
                if (tab != null)
                {
                    tab.select();
                }
                String headingText = spinnerText + ": " + perekOrParashaName;
                psukimTabFrag.loadPuskim(perekOrParashaName, perekOrParashaPairs, headingText);
                thisDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    private void setSpinner() {
        Spinner spinner = this.findViewById(R.id.spinner_nav);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                                                                      R.layout.spinner_item_main,
                                                                      mContext.getResources().getStringArray(R.array.spinner_items));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        final Dialog thisDialog = this;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView txtView = (TextView) view;
                EditText editText = thisDialog.findViewById(
                        R.id.EditBox);
                ImageButton searchBtn = findViewById(R.id.btn_search_substr);
                if (txtView.getText().equals(
                        mContext.getResources().getString(R.string.perek)))
                {
                    editText.setHint(mContext.getResources().getString(R.string.enter_perek));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty())
                    {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mPrakim);
                    mAdapter.getFilter().filter("");
                    mAdapter.notifyDataSetChanged();
                    list.setAdapter(mAdapter);
                    searchBtn.setVisibility(View.INVISIBLE);
                }
                else if (txtView.getText().equals("פרשה"))
                {
                    editText.setHint(mContext.getResources().getString(R.string.enter_parasha));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty())
                    {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mParashot);
                    mAdapter.getFilter().filter("");
                    mAdapter.notifyDataSetChanged();
                    list.setAdapter(mAdapter);
                    searchBtn.setVisibility(View.INVISIBLE);
                }
                else if (txtView.getText().equals("טקסט חופשי"))
                {
                    editText.setHint("אנא הכנס טקסט לחיפוש");
                    list.setAdapter(null);
                    searchBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mAdapter.getFilter().filter(s);
        }
    };


    @Override
    public void onStop() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }
}
