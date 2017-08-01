package com.shareexpenses.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by dlta on 14/03/2014.
 */
public class StatsTabFragment extends Fragment implements UpdateParticipantsInterface, UpdateCategoriesInterface {

    Button startButton;
    Button endButton;
    Button displayGraphButton;
    Button displayGraphButtonPerMonth;
    Account account;
    private DebtArrayAdapter adapter;
    private ArrayList<Expense> expenses;
    private Date[] startDate = new Date[1];
    private Date[] endDate = new Date[1];
    private TextView displayedParticipants;
    private TextView displayedCategories;
    private ArrayList<Participant> participantsForAccount;
    private ArrayList<Participant> selectedParticipants;
    private ArrayList<Category> selectedCategories;
    private ArrayList<Category> categoriesForAccount;
    private DecimalFormat oneDecimalFormat = new DecimalFormat("#.0");
    private StatsTabFragment statsTabFragment = this;

    public void setSelectedParticipants(ArrayList<Participant> selectedParticipants) {
        this.selectedParticipants = selectedParticipants;
    }

    public void setSelectedCategories(ArrayList<Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.stats_tab_layout,container,false);
        startButton=(Button)view.findViewById(R.id.start_date);
        endButton=(Button)view.findViewById(R.id.end_date);
        Button select_participants_button=(Button)view.findViewById(R.id.select_participants);
        Button select_categories_button=(Button)view.findViewById(R.id.select_categories);

        displayedParticipants=(TextView)view.findViewById(R.id.displayed_participants);
        displayedCategories=(TextView)view.findViewById(R.id.displayed_categories);

        displayGraphButton = (Button)view.findViewById(R.id.display_graph);
        displayGraphButtonPerMonth = (Button)view.findViewById(R.id.display_graph_per_month);

        setHasOptionsMenu(true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDatePickerDialog(v, getActivity(), startDate, startButton);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDatePickerDialog(v, getActivity(), endDate, endButton);
            }
        });

        if(startDate[0] != null) {
            //update date
            startButton.setText(Util.formatter1.format(startDate[0]));
        }

        if(endDate[0] != null) {
            //update date
            endButton.setText(Util.formatter1.format(endDate[0]));
        }

        select_participants_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardForCurrentSelection();
                MainActivity mainActivity = (MainActivity) getActivity();
                SelectParticipantFragment selectParticipantFragment = new SelectParticipantFragment();
                selectParticipantFragment.setTargetFragment(statsTabFragment, 0);
                Bundle args = new Bundle();
                args.putSerializable("participantsForAccount", participantsForAccount);
                args.putSerializable("selectedParticipants", selectedParticipants);
                selectParticipantFragment.setArguments(args);
                mainActivity.pushFragment(selectParticipantFragment);
            }
        });

        select_categories_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardForCurrentSelection();
                MainActivity mainActivity = (MainActivity) getActivity();
                SelectCategoryFragment selectCategoryFragment = new SelectCategoryFragment();
                selectCategoryFragment.setTargetFragment(statsTabFragment, 0);
                Bundle args = new Bundle();
                args.putSerializable("categoriesForAccount", categoriesForAccount);
                args.putSerializable("selectedCategories", selectedCategories);
                selectCategoryFragment.setArguments(args);
                mainActivity.pushFragment(selectCategoryFragment);
            }
        });

        //update displayed participants
        String participants = "";
        if(selectedParticipants != null) {
            for (Participant participant : selectedParticipants) {
                participants += participant.getLastName() + " " + participant.getName() + ',';
            }
            if (participants.length() > 0) {
                participants = participants.substring(0, participants.length() - 1);
            }
            displayedParticipants.setText(participants);
        }


        //update displayed categories
        String categories = "";
        if(selectedCategories != null) {
            for (Category category : selectedCategories) {
                categories += category.getName() + ',';
            }
            if (categories.length() > 0) {
                categories = categories.substring(0, categories.length() - 1);
            }
            displayedCategories.setText(categories);
        }


        displayGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardForCurrentSelection();
                MainActivity mainActivity = (MainActivity) getActivity();
                DisplayStatsTabFragment displayStatsTabFragment = new DisplayStatsTabFragment();
                displayStatsTabFragment.setTargetFragment(statsTabFragment, 0);
                Bundle args = new Bundle();
                args.putSerializable("expenses", expenses);
                args.putSerializable("account", account);
                args.putSerializable("categoriesToDisplay", selectedCategories);
                args.putSerializable("payersToDisplay", selectedParticipants);
                args.putSerializable("startDate", startDate[0]);
                args.putSerializable("endDate", endDate[0]);
                args.putSerializable("graphStyle", DisplayStatsTabFragment.PER_CATEGORY);
                displayStatsTabFragment.setArguments(args);
                mainActivity.pushFragment(displayStatsTabFragment);
            }
        });

        displayGraphButtonPerMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardForCurrentSelection();
                MainActivity mainActivity = (MainActivity) getActivity();
                DisplayStatsTabFragment displayStatsTabFragment = new DisplayStatsTabFragment();
                displayStatsTabFragment.setTargetFragment(statsTabFragment, 0);
                Bundle args = new Bundle();
                args.putSerializable("expenses", expenses);
                args.putSerializable("account", account);
                args.putSerializable("categoriesToDisplay", selectedCategories);
                args.putSerializable("payersToDisplay", selectedParticipants);
                args.putSerializable("startDate", startDate[0]);
                args.putSerializable("endDate", endDate[0]);
                args.putSerializable("graphStyle", DisplayStatsTabFragment.PER_MONTH);
                displayStatsTabFragment.setArguments(args);
                mainActivity.pushFragment(displayStatsTabFragment);
            }
        });

        if (null!=savedInstanceState)
        {
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
            account=(Account)savedInstanceState.getSerializable("account");
            participantsForAccount=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)savedInstanceState.getSerializable("categories");
        }
        else
        {
            Bundle args;
            args=getArguments();
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
            account=(Account)args.getSerializable("account");
            participantsForAccount=(ArrayList<Participant>)args.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)args.getSerializable("categories");
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

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
    }

    private void hideKeyboardForCurrentSelection(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void main(String[] args) {
        DateFormat formatter1;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try {
            String dateTemp = "2014-8-2";
            date = formatter1.parse(dateTemp);
        } catch (ParseException e) {

        }
        System.out.println(date);
    }
}
