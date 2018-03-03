package technion.com.testapplication;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.adapters.ViewPagerAdapter;
import technion.com.testapplication.fragments.PsukimTab;

/**
 * Created by tomerlevinson on 27/01/2018.
 * This is the dialog that is activated by the FAB in the main activity.
 */
public class PsukimListDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText = null;
    private ArrayList<String> mAdapterArrayList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> mParashot;
    ArrayList<String> mPrakim;
    private Context mContext;
    private int mSpinnerCheck = 0;
    private ArrayList<Pair<String, String>> mParashotURILabelPairs;
    private ArrayList<Pair<String, String>> mPrakimURILabelPairs;
    private ViewPagerAdapter mViewPagerAdapter;
    private Activity mHostActivity;

    public PsukimListDialog(Context context, ArrayList<String> prakim, ArrayList<String> parashot,
                            ArrayList<Pair<String, String>> parashotURILabelPairs,
                            ArrayList<Pair<String, String>> prakimURILabelPairs,
                            ViewPagerAdapter viewPagerAdapter,
                            Activity activity) {
        super(context);

        /** Design the dialog in main.xml file */
        setContentView(R.layout.psukim_dialog);
        mHostActivity = activity;
        mParashot = parashot;
        mPrakim = prakim;
        mContext = context;
        mPrakimURILabelPairs = prakimURILabelPairs;
        mParashotURILabelPairs = parashotURILabelPairs;
        mViewPagerAdapter = viewPagerAdapter;
        filterText = (EditText) this.findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        setSpinner();
        setAdapterForListView();
    }

    private void setAdapterForListView() {
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1,
                mAdapterArrayList);
        list = (ListView) this.findViewById(R.id.List);
        list.setAdapter(mAdapter);
        final Dialog thisDialog = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String spinnerText = ((Spinner) thisDialog.findViewById(
                        R.id.spinner_nav)).getSelectedItem().toString();
                String perekOrParashaName = list.getItemAtPosition(position).toString();
                String perekOrParashaUri = "";
                ArrayList<Pair<String, String>> perekOrParashaPairs;
                if (spinnerText.equals(mContext.getResources().getString(R.string.parasha))) {
                    perekOrParashaPairs = mParashotURILabelPairs;
                } else {
                    perekOrParashaPairs = mPrakimURILabelPairs;
                }
                for (Pair<String, String> uriLabel : perekOrParashaPairs) {
                    if (uriLabel.first.equals(perekOrParashaName)) {
                        perekOrParashaUri = uriLabel.second;
                    }
                }
                PsukimTab psukimTabFrag = (PsukimTab) mViewPagerAdapter.getItem(
                        0);
                perekOrParashaUri = perekOrParashaUri.substring(
                        perekOrParashaUri.lastIndexOf("/") + 1);
                TabLayout tabs = (TabLayout) mHostActivity.findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(0);
                if (tab != null) {
                    tab.select();
                }
                psukimTabFrag.loadPuskim(perekOrParashaUri, perekOrParashaName);
                thisDialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
    }

    private void setSpinner() {
        Spinner spinner = (Spinner) this.findViewById(R.id.spinner_nav);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext,
                R.layout.spinner_item_main,
                mContext.getResources().getStringArray(R.array.spinner_items));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        final Dialog thisDialog = this;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText editText = (EditText) thisDialog.findViewById(
                        R.id.EditBox);
                if (((TextView) view).getText().equals(
                        mContext.getResources().getString(R.string.perek))) {
                    editText.setHint(mContext.getResources().getString(R.string.enter_perek));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty()) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mPrakim);
                    mAdapter.getFilter().filter("");
                    mAdapter.notifyDataSetChanged();
                } else {
                    editText.setHint(mContext.getResources().getString(R.string.enter_parasha));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty()) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mParashot);
                    mAdapter.getFilter().filter("");
                    mAdapter.notifyDataSetChanged();
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
