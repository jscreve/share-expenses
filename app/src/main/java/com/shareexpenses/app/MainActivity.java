package com.shareexpenses.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Account> accounts;
    private Account selectedAccount = null;
    private Bundle args = new Bundle();
    // gestion du BackStack
    private boolean fragmentAnimated;
    private ArrayList<DrawerItem> drawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private String ACCOUNTS;
    private String ACTIONS;
    private String ADD_ACCOUNT;
    private String REMOVE_ACCOUNT;
    private String EXPENSES;
    private String FRIENDS;
    private String CATEGORIES;
    private String DEBTS;
    private String STATS;
    private String EXPORT;

    //fragments
    ExpensesTabFragment expensesTabFragment;
    ParticipantsTabFragment participantsTabFragment;
    CategoriesTabFragment categoriesTabFragment;
    ResultTabFragment resultTabFragment;
    StatsTabFragment statsTabFragment;
    ExportTabFragment exportTabFragment;

    DrawerArrayAdapter adapter;

    public boolean isFragmentAnimated() {
        return fragmentAnimated;
    }

    public void setFragmentAnimated(boolean fragmentAnimated) {
        this.fragmentAnimated = fragmentAnimated;
    }

    private void configureBackStack() {
        FragmentManager fm;
        fragmentAnimated = true;
        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setFragmentAnimated(true);
            }
        });
    }

    private ArrayList<Account> getAccounts() {
        Data data = MainApplication.getInstance().getData();
        accounts = data.getAccounts();
        return accounts;
    }

    public void addAccount(Account account) {
        Data data = MainApplication.getInstance().getData();
        data.saveAccount(account);
        getAccounts();
        buildMenu();
        //open drawer
        mDrawerLayout.openDrawer(mDrawerList);
    }

    public void removeAccount(Account account) {
        Data data = MainApplication.getInstance().getData();
        data.removeAccount(account);
        getAccounts();
        buildMenu();
        //open drawer
        mDrawerLayout.openDrawer(mDrawerList);
    }

    private void buildMenu() {
        drawerItems = new ArrayList<DrawerItem>();
        //add section
        drawerItems.add(new DrawerItem(ACCOUNTS, false, true));
        //add accounts
        List<AccountDrawerItem> accountsDrawerItems = new ArrayList<AccountDrawerItem>();
        if (accounts != null) {
            for (Account account : accounts) {
                AccountDrawerItem accountDrawerItem = new AccountDrawerItem(account.getAccountName(), account.getId(), true, false);
                accountsDrawerItems.add(accountDrawerItem);
                drawerItems.add(accountDrawerItem);
            }
        }
        //add section
        drawerItems.add(new DrawerItem(ACTIONS, false, true));
        //add menus items
        drawerItems.add(new DrawerItem(ADD_ACCOUNT, false, false));
        drawerItems.add(new DrawerItem(REMOVE_ACCOUNT, false, false));
        drawerItems.add(new DrawerItem(EXPENSES, false, false));
        drawerItems.add(new DrawerItem(FRIENDS, false, false));
        drawerItems.add(new DrawerItem(CATEGORIES, false, false));
        drawerItems.add(new DrawerItem(DEBTS, false, false));
        drawerItems.add(new DrawerItem(STATS, false, false));
        drawerItems.add(new DrawerItem(EXPORT, false, false));
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        adapter = new DrawerArrayAdapter(this, drawerItems);
        mDrawerList.setAdapter(adapter);
        //set first account checked
        //mDrawerList.setItemChecked(drawerItems.indexOf(accountsDrawerItems.get(0)), true);
        //mDrawerList.setItemChecked(0, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ACCOUNTS = getString(R.string.accounts);
        ACTIONS = getString(R.string.actions);
        ADD_ACCOUNT = getString(R.string.add_account);
        REMOVE_ACCOUNT = getString(R.string.remove_account);
        EXPENSES = getString(R.string.expenses);
        FRIENDS = getString(R.string.friends);
        CATEGORIES = getString(R.string.categories);
        DEBTS = getString(R.string.debts);
        STATS = getString(R.string.stats);
        EXPORT = getString(R.string.export);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTitle = "test";

        if (null != savedInstanceState) {
            accounts = (ArrayList<Account>) savedInstanceState.getSerializable("accounts");
        } else {
            accounts = getAccounts();
        }
        buildMenu();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.openDrawer(mDrawerList);

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        configureBackStack();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerList);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (accounts != null) {
            outState.putSerializable("accounts", accounts);
        }
        //TODO should be activated
        //super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(View view, int position) {
        //Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();

        // Highlight the selected item, update the title, and close the drawer
        //mDrawerList.setItemChecked(position, true);
        DrawerItem drawerItem = drawerItems.get(position);
        setTitle(drawerItem.getName());
        //push fragment
        //action
        if (!drawerItem.isAccount() && !drawerItem.isSeparator()) {
            if (drawerItem.getName().equals(ADD_ACCOUNT)) {
                AddAccountFragment addAccountFragment = new AddAccountFragment();
                addAccountFragment.setArguments(args);
                pushFragment(addAccountFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
            } else if (drawerItem.getName().equals(EXPORT)) {
                exportTabFragment = new ExportTabFragment();
                exportTabFragment.setArguments(args);
                pushFragment(exportTabFragment);
                mDrawerLayout.closeDrawer(mDrawerList);
            } else if (selectedAccount == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.please_select_an_account))
                        .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create().show();
            } else {
                if (drawerItem.getName().equals(REMOVE_ACCOUNT)) {
                    if (selectedAccount != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(getString(R.string.sure_to_delete_account))
                                .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        removeAccount(selectedAccount);
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.create().show();
                    }
                } else if (drawerItem.getName().equals(EXPENSES)) {
                    expensesTabFragment = new ExpensesTabFragment();
                    expensesTabFragment.setArguments(args);
                    pushFragment(expensesTabFragment);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else if (drawerItem.getName().equals(FRIENDS)) {
                    participantsTabFragment = new ParticipantsTabFragment();
                    participantsTabFragment.setArguments(args);
                    pushFragment(participantsTabFragment);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else if (drawerItem.getName().equals(CATEGORIES)) {
                    categoriesTabFragment = new CategoriesTabFragment();
                    categoriesTabFragment.setArguments(args);
                    pushFragment(categoriesTabFragment);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else if (drawerItem.getName().equals(DEBTS)) {
                    resultTabFragment = new ResultTabFragment();
                    resultTabFragment.setArguments(args);
                    pushFragment(resultTabFragment);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else if (drawerItem.getName().equals(STATS)) {
                    statsTabFragment = new StatsTabFragment();
                    statsTabFragment.setArguments(args);
                    pushFragment(statsTabFragment);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
        } else {
            if (drawerItem.isAccount) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();
                adapter.toggleCheckBox(checkBox, (AccountDrawerItem) drawerItem);
            }
        }
    }

    public void selectAccount(AccountDrawerItem accountDrawerItem) {

        if (accountDrawerItem == null) {
            selectedAccount = null;
        } else {
            //account selected
            selectedAccount = MainApplication.getInstance().getData().getAccount(accountDrawerItem.getId());
            args.putSerializable("account", selectedAccount);
            ArrayList<Expense> expenses = MainApplication.getInstance().getData().getExpenses(selectedAccount);
            ArrayList<Participant> participants = MainApplication.getInstance().getData().getParticipants(selectedAccount);
            ArrayList<Category> categories = MainApplication.getInstance().getData().getCategories(selectedAccount);
            args.putSerializable("expenses", expenses);
            args.putSerializable("participants", participants);
            args.putSerializable("categories", categories);
        }
    }

    public void reloadData(Account account) {
        ArrayList<Expense> expenses = MainApplication.getInstance().getData().getExpenses(selectedAccount);
        ArrayList<Participant> participants = MainApplication.getInstance().getData().getParticipants(selectedAccount);
        ArrayList<Category> categories = MainApplication.getInstance().getData().getCategories(selectedAccount);
        args.putSerializable("expenses", expenses);
        args.putSerializable("participants", participants);
        args.putSerializable("categories", categories);
    }

    public void reloadDataAndOpenDrawer() {
        accounts = getAccounts();
        selectedAccount = null;
        buildMenu();
        mDrawerLayout.openDrawer(mDrawerList);
    }

    public void reloadData() {
        accounts = getAccounts();
        selectedAccount = null;
        buildMenu();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(view, position);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void showFragment(Fragment newFragment) {
        FragmentManager fm;
        FragmentTransaction transaction;

        fm = getSupportFragmentManager();
        setFragmentAnimated(false);
        // Dépile tous les éléments dans la BackStack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.commit();
        invalidateOptionsMenu();
    }

    public void pushFragment(android.support.v4.app.Fragment newFragment) {
        FragmentManager fm;
        FragmentTransaction transaction;

        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.content_frame, newFragment);
        transaction.commit();
    }

    public void popFragment() {
        // Catch back action and pops from backstack
        // (if you called previously to addToBackStack() in your transaction)
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }
}
