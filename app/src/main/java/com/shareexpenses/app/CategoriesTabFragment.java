package com.shareexpenses.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.CategoryForExpense;
import com.shareexpenses.app.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dlta on 14/03/2014.
 */
public class CategoriesTabFragment extends Fragment {

    ListView listView;
    ArrayList<Category> categories;
    Account account;
    ArrayList<Expense> expenses;
    private CategoriesArrayAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view;
		view=inflater.inflate(R.layout.categories_tab_layout,container,false);
        listView=(ListView)view.findViewById(R.id.categorylistView);

        setHasOptionsMenu(true);

        if (null!=savedInstanceState)
        {
            categories=(ArrayList<Category>)savedInstanceState.getSerializable("categories");
            account=(Account)savedInstanceState.getSerializable("account");
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
        }
        else
        {
            Bundle args;
            args=getArguments();
            categories=(ArrayList<Category>)args.getSerializable("categories");
            account=(Account)args.getSerializable("account");
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
        }

        adapter=new CategoriesArrayAdapter(getActivity());
        if(categories != null) {
            adapter.clear();
            adapter.addAll(categories);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = adapter.getItem(position);
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkbox);
                checkBox.toggle();
                adapter.setCheck(checkBox.isChecked(), category);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_new);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addCategoriesFragment();
                //reset selected categories
                adapter.setIsCategorySelectedMap(new HashMap<Category, Boolean>());
                return false;
            }
        });

        /*MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                mainActivity.openDrawer();
                return false;
            }
        });*/

        MenuItem menuItemRemove = menu.findItem(R.id.action_remove);
        menuItemRemove.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //if category is defined in the account, forbid the operation
                final List<Category> categories1 = getSelectedCategories();
                if(areCategoriesUsedInExpenses(categories1)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.category_is_used_in_account))
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.action_remove_sure))
                            .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (Category category : categories1) {
                                        removeCategory(category);
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create().show();
                }
                return false;
            }
        });

        MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemEdit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Category> categories = getSelectedCategories();
                if(categories.size() > 1 || categories.size() == 0) {
                    Util.displayWarningDialogBox(getActivity(), getString(R.string.select_one_category), getString(R.string.cancel));
                } else {
                    displayUpdateDialogBox(categories.get(0));
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean areCategoriesUsedInExpenses(List<Category> categories) {
        boolean used = false;
        for(Category category : categories) {
            if(isCategoryUsedInExpenses(category)) {
                used = true;
                break;
            }
        }
        return used;
    }

    private boolean isCategoryUsedInExpenses(Category category) {
        boolean used = false;
        if(expenses != null) {
            for (Expense expense : expenses) {
                List<CategoryForExpense> categoryForExpenseList = expense.getCategoriesForExpense();
                if(categoryForExpenseList != null) {
                    for(CategoryForExpense categoryForExpense : categoryForExpenseList) {
                        if(categoryForExpense.getCategory().equals(category)) {
                            used = true;
                            break;
                        }
                    }
                }
                if(used == true) {
                    break;
                }
            }
        }
        return used;
    }

    private void displayUpdateDialogBox (final Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setText(category.getName());
        builder.setView(input);
        builder.setMessage(getString(R.string.update_category_question))
                .setNeutralButton(getString(R.string.rename), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String value = input.getText().toString();
                        category.setName(value);
                        updateCategory(category);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create().show();
    }

    private List<Category> getSelectedCategories() {
        List<Category> expenses = new ArrayList<Category>();

        Map<Category, Boolean> selectedCategories = adapter.getIsCategorySelectedMap();
        for(Map.Entry<Category, Boolean> entry : selectedCategories.entrySet()) {
            if(entry.getValue().equals(true)) {
                Category category = entry.getKey();
                expenses.add(category);
            }
        }
        return expenses;
    }

    private void addCategoriesFragment() {
        AddCategoryFragment addCategoryFragment=new AddCategoryFragment();
        addCategoryFragment.setTargetFragment(this, 0);
        Bundle args=new Bundle();
        args.putSerializable("account", account);
        args.putSerializable("categories", categories);
        addCategoryFragment.setArguments(args);
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.pushFragment(addCategoryFragment);
    }

    public void addCategory(Category category) {
        Data data = MainApplication.getInstance().getData();
        data.saveCategory(category);
        adapter.add(category);
    }

    public void updateCategory(Category category) {
        Data data = MainApplication.getInstance().getData();
        data.updateCategory(category);

        int i = 0;
        boolean categoryFound = false;
        for(; i < categories.size(); i++) {
            if(categories.get(i).getId().equals(category.getId())) {
                categoryFound = true;
                break;
            }
        }
        if(categoryFound) {
            categories.remove(i);
            categories.add(i, category);
        }
        //reload categories for expense
        ((MainActivity)getActivity()).reloadData(account);
    }

    public void removeCategory(Category category) {
        Data data = MainApplication.getInstance().getData();
        data.removeCategory(category);
        adapter.remove(category);
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
        if(expenses != null) {
            outState.putSerializable("expenses", expenses);
        }
    }
}
