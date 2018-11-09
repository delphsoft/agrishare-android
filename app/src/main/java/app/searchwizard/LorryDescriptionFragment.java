package app.searchwizard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;

import static app.agrishare.Constants.TAB_LORRY_DESCRIPTION;
import static app.agrishare.Constants.TAB_QUANTITY;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class LorryDescriptionFragment extends BaseFragment {

    EditText load_description_edittext;
    Button submit_button;


    public LorryDescriptionFragment() {
        mtag = "name";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    LorryDescriptionFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_lorry_description_form, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        load_description_edittext = rootView.findViewById(R.id.load_description);
        load_description_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFields();

                    return true;
                }
                return false;
            }
        });


        submit_button = rootView.findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    checkFields();
                }

            }
        });

    }

    private void clearErrors(){
        load_description_edittext.setError(null);
    }

    public void checkFields() {
        closeKeypad();
        clearErrors();
        String description = load_description_edittext.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(description)) {
            load_description_edittext.setError(getString(R.string.error_field_required));
            focusView = load_description_edittext;
            cancel = true;
        }

        if (((SearchActivity) getActivity()).catergoryId == 1) {
            if (!description.isEmpty()) {

                Double field_size = Double.valueOf(description);
                if (field_size < 0.5) {
                    load_description_edittext.setError(getString(R.string.error_minimum_field_size_required_is_0_5ha));
                    focusView = load_description_edittext;
                    cancel = true;
                }
            }
        }

        if (cancel) {
            // There was an error; don't submit and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {

            MyApplication.searchQuery.AdditionalInformation = description;

            if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
                ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
            }

        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

        if (((SearchActivity) getActivity()).tabsStackList.contains(TAB_LORRY_DESCRIPTION))
            ((SearchActivity) getActivity()).tabsStackList.remove(TAB_LORRY_DESCRIPTION);
        ((SearchActivity) getActivity()).tabsStackList.add(TAB_LORRY_DESCRIPTION);
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

}