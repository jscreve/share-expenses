package com.shareexpenses.app;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.shareexpenses.app.model.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jess on 18/09/2014.
 */
public class AddExpenseFragment extends Fragment implements UpdateCategoriesInterface, UpdateParticipantsInterface {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private EditText nameValue;
    private EditText inputValue;
    private TextView displayedParticipants;
    private TextView displayedCategories;
    private Spinner spinner;
    private static TextView displayedDate;
    private Button displayedDateButton;
    private Account account;
    private Expense expenseToUpdate;
    private ArrayList<Expense> expenses;
    private Participant payer;
    private ArrayList<Participant> participantsForAccount;
    private ArrayList<Participant> selectedParticipants;
    private ArrayList<Category> selectedCategories;
    private ArrayList<Category> categoriesForAccount;
    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 1;

    public EditText getNameValue() {
        return nameValue;
    }

    public void setNameValue(EditText nameValue) {
        this.nameValue = nameValue;
    }

    public ArrayList<Participant> getSelectedParticipants() {
        return selectedParticipants;
    }

    public void setSelectedParticipants(ArrayList<Participant> selectedParticipants) {
        this.selectedParticipants = selectedParticipants;
    }

    public ArrayList<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(ArrayList<Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    private AddExpenseFragment addExpenseFragment = this;


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            //dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            displayedDate.setText(dateFormat.format(c.getTime()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;

        view=inflater.inflate(R.layout.add_expense,container,false);

        setHasOptionsMenu(true);

        displayedDateButton = (Button)view.findViewById(R.id.update_date_button);
        displayedDate = (TextView)view.findViewById(R.id.date_field);

        nameValue = (EditText)view.findViewById(R.id.name_editText);
        inputValue =(EditText)view.findViewById(R.id.value_editText);

        Button select_participants_button=(Button)view.findViewById(R.id.select_participants);
        Button select_categories_button=(Button)view.findViewById(R.id.select_categories);

        displayedParticipants=(TextView)view.findViewById(R.id.displayed_participants);
        displayedCategories=(TextView)view.findViewById(R.id.displayed_categories);

        if (null!=savedInstanceState)
        {
            account=(Account)savedInstanceState.getSerializable("account");
            participantsForAccount=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)savedInstanceState.getSerializable("categories");
        }
        else
        {
            Bundle args;
            args=getArguments();
            account=(Account)args.getSerializable("account");
            expenseToUpdate=(Expense)args.getSerializable("expense");
            participantsForAccount=(ArrayList<Participant>)args.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)args.getSerializable("categories");
        }

        if(expenseToUpdate != null) {
            nameValue.setText(expenseToUpdate.getName());
            inputValue.setText(Double.toString(expenseToUpdate.getValue()));
            //payer = expenseToUpdate.getPayer();

            //init only if not modified already
            if(selectedParticipants==null) {
                selectedParticipants = new ArrayList<Participant>();
                for (ParticipantForExpense tempParticipantForExpense : expenseToUpdate.getParticipantForExpenseList()) {
                    selectedParticipants.add(tempParticipantForExpense.getParticipant());
                }
            }
            if(selectedCategories==null) {
                selectedCategories = new ArrayList<Category>();
                for (CategoryForExpense tempCategoryForExpense : expenseToUpdate.getCategoriesForExpense()) {
                    selectedCategories.add(tempCategoryForExpense.getCategory());
                }
            }
            displayedDate.setText(dateFormat.format(expenseToUpdate.getDate()));
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(getTodayDate());
            displayedDate.setText(dateFormat.format(c.getTime()));
        }

        spinner = (Spinner)view.findViewById(R.id.paid_by_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ParticipantArrayAdapter participantArrayAdapter = new ParticipantArrayAdapter(getActivity(), false);
        //populate list
        participantArrayAdapter.setAll(participantsForAccount);
        // Specify the layout to use when the list of choices appears
        participantArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(participantArrayAdapter);

        //set initial value
        if(expenseToUpdate != null) {
            spinner.setSelection(getIndex(spinner, expenseToUpdate.getPayer()));
        }

        displayedDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboardForCurrentSelection();
                payer = (Participant) spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideKeyboardForCurrentSelection();
            }
        });

        select_participants_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardForCurrentSelection();
                MainActivity mainActivity = (MainActivity) getActivity();
                SelectParticipantFragment selectParticipantFragment = new SelectParticipantFragment();
                selectParticipantFragment.setTargetFragment(addExpenseFragment, 0);
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
                selectCategoryFragment.setTargetFragment(addExpenseFragment, 0);
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
        return view;
    }


    //private method to get Spinner index
    private int getIndex(Spinner spinner, Participant myPayer)
    {
        int index = 0;

        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).equals(myPayer)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions_validate_cancel, menu);

        MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               hideKeyboardForCurrentSelection();
               MainActivity mainActivity = (MainActivity) getActivity();
               mainActivity.popFragment();
               return false;
           }
       });

        MenuItem menuItem_accept = menu.findItem(R.id.action_accept);
        menuItem_accept.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_accept.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (payer == null) {
                    //manage error, display popup
                } else {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    Double value = 0.0d;
                    Date date = getTodayDate();
                    try {
                        ExpensesTabFragment expensesTabFragment = (ExpensesTabFragment) getTargetFragment();
                        Expense expense = null;
                        value = Double.parseDouble(inputValue.getText().toString());
                        try {
                            date = dateFormat.parse(displayedDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (expenseToUpdate == null) {
                            expense = new Expense(value, nameValue.getText().toString(), payer, date, new Currency("EUR"));
                        } else {
                            expense = expenseToUpdate;
                            expense.setName(nameValue.getText().toString());
                            expense.setValue(value);
                            expense.setPayer(payer);
                            expense.setDate(date);
                        }
                        expense.setAccount(account);
                        ArrayList<ParticipantForExpense> tempParticipantsForExpense = new ArrayList<ParticipantForExpense>();
                        //if no participant defined, we set all participants for this account
                        if (selectedParticipants == null || selectedParticipants.size() == 0) {
                            selectedParticipants = participantsForAccount;
                        }
                        for (Participant participant : selectedParticipants) {
                            tempParticipantsForExpense.add(new ParticipantForExpense(participant, expense, 0.0d));
                        }
                        expense.setParticipantForExpenseList(tempParticipantsForExpense);

                        //manage categories
                        ArrayList<CategoryForExpense> tempCategoriesForExpense = new ArrayList<CategoryForExpense>();
                        if (selectedCategories == null || selectedCategories.size() == 0) {
                            selectedCategories = new ArrayList<Category>();
                        }
                        for (Category category : selectedCategories) {
                            tempCategoriesForExpense.add(new CategoryForExpense(category, expense));
                        }
                        expense.setCategoriesForExpense(tempCategoriesForExpense);

                        if (expenseToUpdate != null) {
                            expensesTabFragment.updateExpense(expense);
                        } else {
                            expensesTabFragment.addExpense(expense);
                        }
                        mainActivity.popFragment();
                        //temporary
                        hideKeyboard();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
                return false;
            }
        });
    }


    private Date getTodayDate() {
        //set date as today
        DateFormat formatter1;
        formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date date = new Date();
        try {
            date = formatter1.parse(Integer.toString(year) + "-" + Integer.toString(month + 1) + '-' + Integer.toString(day));
        } catch (Exception e) {

        }
        return date;
    }

    // Masque le clavier
    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        hideInputForEditText(imm, inputValue);
    }

    private void hideInputForEditText(InputMethodManager imm,EditText editText)
    {
        if (null!=editText)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void hideKeyboardForCurrentSelection(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(account != null) {
            outState.putSerializable("account", account);
        }
        if(participantsForAccount != null) {
            outState.putSerializable("participants", participantsForAccount);
        }
    }
}
