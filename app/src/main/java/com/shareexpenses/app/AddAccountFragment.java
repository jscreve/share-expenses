package com.shareexpenses.app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.shareexpenses.app.model.Account;

import java.util.List;

/**
 * Created by jess on 18/09/2014.
 */
public class AddAccountFragment extends Fragment {

    private EditText inputValue;

    private List<Account> accounts;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view;
        view=inflater.inflate(R.layout.add_account,container,false);
        Button add_button=(Button)view.findViewById(R.id.add_account_button);
        inputValue =(EditText)view.findViewById(R.id.value_editText);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions_validate, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_accept);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity mainActivity=(MainActivity)getActivity();
                String name;
                try {
                    name = inputValue.getText().toString();
                    //AccountListFragment accountListFragment = (AccountListFragment)getTargetFragment();
                    mainActivity.addAccount(new Account(name));
                    //accountListFragment.
                    mainActivity.popFragment();
                    //temporary
                    hideKeyboard();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                }
                return false;
            }
        });

       /* MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                hideKeyboard();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                mainActivity.openDrawer();
                return false;
            }
        });*/
    }


    // Masque le clavier
    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        hideInputForEditText(imm, inputValue);
    }

    private void hideInputForEditText(InputMethodManager imm,EditText editText)
    {
        if (null!=editText)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
