package com.shareexpenses.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jess on 08/09/2014.
 */
public class DrawerArrayAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DrawerItem> mList;
    private CheckBox mLastCheckBox;

    // View Type for Separators
    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    // View type for account rows
    private static final int ITEM_VIEW_TYPE_ACCOUNT = 1;
    // View Type for Menu rows
    private static final int ITEM_VIEW_TYPE_MENU = 2;
    // Types of Views that need to be handled
    // -- Separators and Regular rows --
    private static final int ITEM_VIEW_TYPE_COUNT = 3;

    private SharedPreferences sharedPref;
    public static String LAST_SELECTED_ACCOUNT="last_selected_account";


    public DrawerArrayAdapter(Context context, ArrayList list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isSection = mList.get(position).isSeparator();
        boolean isAccount = mList.get(position).isAccount();

        if (isSection) {
            return ITEM_VIEW_TYPE_SEPARATOR;
        }
        else if(isAccount) {
            return ITEM_VIEW_TYPE_ACCOUNT;
        } else {
            return ITEM_VIEW_TYPE_MENU;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        final DrawerItem item = mList.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                // If its a section ?
                view = inflater.inflate(R.layout.drawer_list_section, null);
            }
            else if(itemViewType == ITEM_VIEW_TYPE_ACCOUNT) {
                // account ?
                view = inflater.inflate(R.layout.drawer_list_account, null);
            }
            else {
                // Regular row, menu item
                view = inflater.inflate(R.layout.drawer_list_item, null);
            }
        }
        else {
            view = convertView;
        }


        if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
            // If separator
            TextView separatorView = (TextView) view.findViewById(R.id.separator);
            separatorView.setText(item.getName());
        }
        else {
            if (itemViewType == ITEM_VIEW_TYPE_ACCOUNT) {
                // If account
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                final AccountDrawerItem accountDrawerItem=(AccountDrawerItem)item;
                //check init selection
                sharedPref = ((Activity)mContext).getPreferences(Context.MODE_PRIVATE);
                if(sharedPref.getLong(LAST_SELECTED_ACCOUNT, -1)==accountDrawerItem.getId()  && checkBox.isChecked() == false) {
                    checkBox.setChecked(true);
                    toggleCheckBox(checkBox, accountDrawerItem);
                }
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleCheckBox(checkBox, accountDrawerItem);
                    }
                });
                TextView accountView = (TextView) view.findViewById(R.id.name);
                accountView.setText(item.getName());
            } else {
                // If regular menu item
                TextView menuView = (TextView) view.findViewById(R.id.name);
                menuView.setText(item.getName());
            }
        }

        return view;
    }

    public void toggleCheckBox(CheckBox checkBox, AccountDrawerItem accountDrawerItem) {
        MainActivity mainActivity = (MainActivity) mContext;
        if(checkBox.isChecked()) {
            sharedPref = ((Activity)mContext).getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(LAST_SELECTED_ACCOUNT, accountDrawerItem.getId());
            editor.commit();
            if(mLastCheckBox != null && mLastCheckBox != checkBox) {
                mLastCheckBox.setChecked(false);
            }
            mLastCheckBox=checkBox;
            mainActivity.selectAccount(accountDrawerItem);
        } else {
            mLastCheckBox=null;
            mainActivity.selectAccount(null);
        }
    }
}