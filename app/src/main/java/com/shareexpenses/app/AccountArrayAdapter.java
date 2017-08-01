package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shareexpenses.app.model.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jess on 08/09/2014.
 */
public class AccountArrayAdapter extends ArrayAdapter<Account> {

    private List<Account> accounts;

    public AccountArrayAdapter(Context context) {
        super(context, 0);
    }

    public void setAll(List<Account> accounts) {
        setNotifyOnChange(false);
        clear();
        addAll(accounts);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public ArrayList<Account> toArrayList()
    {
        ArrayList<Account> result;

        result = new ArrayList<Account>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.account_line, null);
        }

        final Account account = getItem(position);
        TextView name_textView=(TextView)v.findViewById(R.id.name_textView);

        name_textView.setText(account.getAccountName());
        return v;
    }
}
