package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shareexpenses.app.model.Expense;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jess on 08/09/2014.
 */
public class DebtArrayAdapter extends ArrayAdapter<Debt> {

    private List<Debt> debts;

    public DebtArrayAdapter(Context context) {
        super(context, 0);
    }

    public void setAll(List<Expense> expenses) {
        setNotifyOnChange(false);
        clear();
        addAll(debts);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public ArrayList<Debt> toArrayList()
    {
        ArrayList<Debt> result;

        result = new ArrayList<Debt>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.result_line, null);
        }

        final Debt debt = getItem(position);
        TextView name_textView=(TextView)v.findViewById(R.id.result_textView);

        name_textView.setText(debt.toString());
        return v;
    }
}
