package com.shareexpenses.app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;

import java.util.ArrayList;

/**
 * Created by jess on 18/09/2014.
 */
public class AddCategoryFragment extends Fragment {

    private EditText name;
    private Account account;
    private ArrayList<Category> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;

        view=inflater.inflate(R.layout.add_category,container,false);

        setHasOptionsMenu(true);

        name =(EditText)view.findViewById(R.id.name_editText);

        if (null!=savedInstanceState)
        {
            account=(Account)savedInstanceState.getSerializable("account");
        }
        else
        {
            Bundle args;
            args=getArguments();
            account=(Account)args.getSerializable("account");
        }

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
                String sname = "";
                try {
                    sname = name.getText().toString();
                    CategoriesTabFragment categoriesTabFragment=(CategoriesTabFragment)getTargetFragment();
                    Category category = new Category(sname);
                    category.setAccount(account);
                    categoriesTabFragment.addCategory(category);
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

        /*MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
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
        hideInputForEditText(imm, name);
    }

    private void hideInputForEditText(InputMethodManager imm,EditText editText)
    {
        if (null!=editText)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(account != null) {
            outState.putSerializable("account", account);
        }
        if(categories != null) {
            outState.putSerializable("categories", categories);
        }
    }
}
