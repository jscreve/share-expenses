package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jess on 08/09/2014.
 */
public class SelectCategoryArrayAdapter extends ArrayAdapter<Category> {

    private List<Category> categories;

    public Map<Category, Boolean> getIsCategorySelectedMap() {
        return isCategorySelectedMap;
    }

    public void setIsCategorySelectedMap(Map<Category, Boolean> isCategorySelectedMap) {
        this.isCategorySelectedMap = isCategorySelectedMap;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    private Map<Category, Boolean> isCategorySelectedMap = new HashMap<Category, Boolean>();

    public SelectCategoryArrayAdapter(Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.select_category_in_list, null);
        }

        final Category category = getItem(position);
        TextView category_name_textView=(TextView)v.findViewById(R.id.category_name_textView);
        CheckBox checkbox = (CheckBox)v.findViewById(R.id.category_checkbox);

        //if participant is selected, enable checkbox
        if(isCategorySelectedMap.get(category) != null && isCategorySelectedMap.get(category) == true && checkbox.isChecked() ==false) {
            checkbox.toggle();
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck (((CheckBox) v).isChecked(), category);
            }
        });

        category_name_textView.setText(category.getName());
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
