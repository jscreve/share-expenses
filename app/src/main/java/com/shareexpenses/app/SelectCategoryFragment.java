package com.shareexpenses.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import com.shareexpenses.app.model.Category;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jess on 18/09/2014.
 */
public class SelectCategoryFragment extends Fragment {

    private ArrayList<Category> categoriesForAccount;
    private ListView listView;
    private SelectCategoryArrayAdapter selectCategoryArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;

        view=inflater.inflate(R.layout.participants_list,container,false);

        listView = (ListView)view.findViewById(R.id.participantsListView);

        setHasOptionsMenu(true);

        ArrayList<Category> inputCategories=null;

        if (null!=savedInstanceState)
        {
            categoriesForAccount=(ArrayList<Category>)savedInstanceState.getSerializable("categoriesForAccount");
        }
        else
        {
            Bundle args;
            args=getArguments();
            categoriesForAccount=(ArrayList<Category>)args.getSerializable("categoriesForAccount");
            inputCategories=(ArrayList<Category>)args.getSerializable("selectedCategories");
        }

        selectCategoryArrayAdapter=new SelectCategoryArrayAdapter(getActivity());
        if(categoriesForAccount != null) {
            selectCategoryArrayAdapter.clear();
            selectCategoryArrayAdapter.addAll(categoriesForAccount);
        }

        listView.setAdapter(selectCategoryArrayAdapter);

        //initialize default values
        if(inputCategories == null) {
            inputCategories=new ArrayList<Category>();
        } else {
            for(Category category : inputCategories) {
                selectCategoryArrayAdapter.setCheck(true, category);
            }
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = selectCategoryArrayAdapter.getItem(position);
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.category_checkbox);
                checkBox.toggle();
                selectCategoryArrayAdapter.setCheck(checkBox.isChecked(), category);
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions_validate_cancel, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_accept);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //retrieve participants
                Map<Category, Boolean> isCategorySelected = selectCategoryArrayAdapter.getIsCategorySelectedMap();
                //update DB with participant selection
                UpdateCategoriesInterface updateCategoriesInterface = (UpdateCategoriesInterface)getTargetFragment();
                ArrayList<Category> categoriesForExpense = new ArrayList<Category>();
                for(Map.Entry<Category, Boolean> isCategorySelectedIterator : isCategorySelected.entrySet()) {
                    if(isCategorySelectedIterator.getValue().equals(true)) {
                        categoriesForExpense.add(isCategorySelectedIterator.getKey());
                    }
                }
                updateCategoriesInterface.setSelectedCategories(categoriesForExpense);

                //pop window
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                return false;
            }
        });

        MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                return false;
            }
        });
    }


        @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(categoriesForAccount != null) {
            outState.putSerializable("categoriesForAccount", categoriesForAccount);
        }
    }
}
