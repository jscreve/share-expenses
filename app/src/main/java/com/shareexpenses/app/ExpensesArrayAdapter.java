package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.shareexpenses.app.model.CategoryForExpense;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;
import com.shareexpenses.app.model.ParticipantForExpense;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jess on 08/09/2014.
 */
public class ExpensesArrayAdapter extends ArrayAdapter<Expense> {

    private List<Expense> expenses;

    public ExpensesArrayAdapter(Context context) {
        super(context, 0);
    }

    public Map<Expense, Boolean> getIsExpenseSelectedMap() {
        return isExpenseSelectedMap;
    }

    public void setIsExpenseSelectedMap(Map<Expense, Boolean> isExpenseSelectedMap) {
        this.isExpenseSelectedMap = isExpenseSelectedMap;
    }

    private Map<Expense, Boolean> isExpenseSelectedMap = new HashMap<Expense, Boolean>();


    public ArrayList<Expense> toArrayList()
    {
        ArrayList<Expense> result;

        result = new ArrayList<Expense>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    @Override
    public void remove(Expense object) {
        super.remove(object);
        isExpenseSelectedMap.remove(object);
    }

    public void setCheck(boolean value, Expense expense) {
        if (value) {
            isExpenseSelectedMap.put(expense, true);
        } else {
            isExpenseSelectedMap.put(expense, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.expense_line, null);
        }

        final Expense expense = getItem(position);

        //manage checkbox
        CheckBox checkBox=(CheckBox)v.findViewById(R.id.checkbox);

        //update check box after deletion
        if(isExpenseSelectedMap.get(expense) != null) {
            if(isExpenseSelectedMap.get(expense) == false) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean value = ((CheckBox) v).isChecked();
                setCheck(value, expense);
            }
        });

        //manage display
        TextView name_textView=(TextView)v.findViewById(R.id.name_textView);
        TextView value_textView=(TextView)v.findViewById(R.id.value_textView);
        TextView value_textView2=(TextView)v.findViewById(R.id.value_textView2);

        String date = "   ";
        DateFormat formatter1;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        date += formatter1.format(expense.getDate());

        name_textView.setText(expense.getName() + date);
        String expenseText = Double.toString(expense.getValue()) + "€ dépensés par : " + expense.getPayer().toString() + " pour : ";
        for(ParticipantForExpense participant : expense.getParticipantForExpenseList()) {
            expenseText += participant.getParticipant();
        }
        value_textView.setText(expenseText);

        String expenseText2 = "Catégories : ";
        for(CategoryForExpense category : expense.getCategoriesForExpense()) {
            expenseText2 += category.getCategory().getName();
            expenseText2 += " ";
        }
        value_textView2.setText(expenseText2);
        return v;
    }
}
