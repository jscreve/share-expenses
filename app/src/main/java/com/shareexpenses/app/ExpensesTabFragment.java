package com.shareexpenses.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dlta on 14/03/2014.
 */
public class ExpensesTabFragment extends Fragment {

	ListView listView;
    ArrayList<Expense> expenses;
    ArrayList<Participant> participants;
    ArrayList<Category> categories;
    Account account;
    private ExpensesArrayAdapter adapter;


	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view;

		view=inflater.inflate(R.layout.expenses_tab_layout,container,false);
        listView=(ListView)view.findViewById(R.id.listView);

        setHasOptionsMenu(true);

        if (null!=savedInstanceState)
        {
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
            account=(Account)savedInstanceState.getSerializable("account");
            participants=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            categories=(ArrayList<Category>)savedInstanceState.getSerializable("categories");
        }
        else
        {
            Bundle args;
            args=getArguments();
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
            account=(Account)args.getSerializable("account");
            participants=(ArrayList<Participant>)args.getSerializable("participants");
            categories=(ArrayList<Category>)args.getSerializable("categories");
        }

        if(adapter == null) {
            adapter = new ExpensesArrayAdapter(getActivity());
        }
        if(expenses != null) {
            adapter.clear();
            adapter.addAll(expenses);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expense expense = adapter.getItem(position);
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkbox);
                checkBox.toggle();
                adapter.setCheck(checkBox.isChecked(), expense);
            }
        });

		return view;
	}

    private boolean checkFriendsDefined() {
        boolean participantsDefined = false;
        if(participants != null && participants.size() > 0) {
            participantsDefined = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.please_define_participants_first))
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create().show();
        }
        return participantsDefined;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_new);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(checkFriendsDefined()) {
                    //check friends are defined
                    addExpenseFragment(null);
                    //reset selected expenses
                    adapter.setIsExpenseSelectedMap(new HashMap<Expense, Boolean>());
                }
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
                List<Expense> expenses = getSelectedExpenses();
                for(Expense expense : expenses) {
                    removeExpense(expense);
                }
                //uncheck boxes
              //  for(int i = 0; i < adapter.getCount(); i++) {
              //      listView.setItemChecked(i, false);
              //  }
                return false;
            }
        });

        MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemEdit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Expense> expenses = getSelectedExpenses();
                if(expenses.size() > 1 || expenses.size() == 0) {
                    Util.displayWarningDialogBox(getActivity(), getString(R.string.select_one_expense), getString(R.string.cancel));
                } else {
                    addExpenseFragment(expenses.get(0));
                    //reset selected expenses
                    adapter.setIsExpenseSelectedMap(new HashMap<Expense, Boolean>());
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private List<Expense> getSelectedExpenses() {
        List<Expense> expenses = new ArrayList<Expense>();

        Map<Expense, Boolean> selectedExpenses = adapter.getIsExpenseSelectedMap();
        for(Map.Entry<Expense, Boolean> entry : selectedExpenses.entrySet()) {
            if(entry.getValue().equals(true)) {
                Expense expense = entry.getKey();
                expenses.add(expense);
            }
        }
        return expenses;
    }

    private void addExpenseFragment(Expense expense) {
        AddExpenseFragment addExpenseFragment=new AddExpenseFragment();
        addExpenseFragment.setTargetFragment(this, 0);
        Bundle args=new Bundle();
        args.putSerializable("expense", expense);
        args.putSerializable("account", account);
        args.putSerializable("expenses", expenses);
        args.putSerializable("participants", participants);
        args.putSerializable("categories", categories);
        addExpenseFragment.setArguments(args);
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.pushFragment(addExpenseFragment);
    }

    public void addExpense(Expense expense) {
        Data data = MainApplication.getInstance().getData();
        data.saveExpense(expense);
        //expenses will be reloaded as the add is done in another screen
        //expenses.add(expense);
        //adapter.add(expense);
    }

    public void updateExpense(Expense expense) {
        Data data = MainApplication.getInstance().getData();
        data.updateExpense(expense);
        //expenses will be reloaded as the add is done in another screen
        /*int i = 0;
        boolean expenseFound = false;
        for(; i < expenses.size(); i++) {
            if(expenses.get(i).getId().equals(expense.getId())) {
                expenseFound = true;
                break;
            }
        }
        if(expenseFound) {
            expenses.remove(i);
            expenses.add(i, expense);
        }*/
    }

    public void removeExpense(Expense expense) {
        Data data = MainApplication.getInstance().getData();
        data.removeExpense(expense);
        adapter.remove(expense);

        int i = 0;
        boolean expenseFound = false;
        for(; i < expenses.size(); i++) {
            if(expenses.get(i).getId().equals(expense.getId())) {
                expenseFound = true;
                break;
            }
        }
        if(expenseFound) {
            expenses.remove(i);
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(account != null) {
            outState.putSerializable("account", account);
        }
        if(expenses != null) {
            outState.putSerializable("expenses", expenses);
        }
        if(participants != null) {
            outState.putSerializable("participants", participants);
        }
        if(categories != null) {
            outState.putSerializable("categories", categories);
        }
    }

}
