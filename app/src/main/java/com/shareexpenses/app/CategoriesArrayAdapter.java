package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jess on 08/09/2014.
 */
public class CategoriesArrayAdapter extends ArrayAdapter<Category> {

    private List<Category> categories;

    public CategoriesArrayAdapter(Context context) {
        super(context, 0);
    }

    public void setAll(List<Category> categories) {
        setNotifyOnChange(false);
        clear();
        addAll(categories);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public ArrayList<Category> toArrayList()
    {
        ArrayList<Category> result;

        result = new ArrayList<Category>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    @Override
    public void remove(Category object) {
        super.remove(object);
        isCategorySelectedMap.remove(object);
    }

    public Map<Category, Boolean> getIsCategorySelectedMap() {
        return isCategorySelectedMap;
    }

    public void setIsCategorySelectedMap(Map<Category, Boolean> isCategorySelectedMap) {
        this.isCategorySelectedMap = isCategorySelectedMap;
    }

    private Map<Category, Boolean> isCategorySelectedMap = new HashMap<Category, Boolean>();


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.category_line, null);
        }

        final Category category = getItem(position);
        TextView value_textView=(TextView)v.findViewById(R.id.category_textView);

        //manage checkbox
        CheckBox checkBox=(CheckBox)v.findViewById(R.id.checkbox);

        //update check box after deletion
        if(isCategorySelectedMap.get(category) != null) {
            if(isCategorySelectedMap.get(category) == false) {
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
                setCheck(((CheckBox) v).isChecked(), category);
            }
        });

        value_textView.setText(category.getName());
        return v;
    }

    public void setCheck(boolean value, Category category) {
        if (value) {
            isCategorySelectedMap.put(category, true);
        } else {
            isCategorySelectedMap.put(category, false);
        }
    }
}
