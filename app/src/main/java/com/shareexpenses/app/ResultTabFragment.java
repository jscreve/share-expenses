package com.shareexpenses.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;

/**
 * Created by dlta on 14/03/2014.
 */
public class ResultTabFragment extends Fragment {

    ListView listView;
    Account account;
    private DebtArrayAdapter adapter;
    private ArrayList<Expense> expenses;
    private ArrayList<Participant> participants;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view;
        view=inflater.inflate(R.layout.result_tab_layout,container,false);
        listView=(ListView)view.findViewById(R.id.result_listView);

        setHasOptionsMenu(true);

        if (null!=savedInstanceState)
        {
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
            participants=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            account=(Account)savedInstanceState.getSerializable("account");
        }
        else
        {
            Bundle args;
            args=getArguments();
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
            participants=(ArrayList<Participant>)args.getSerializable("participants");
            account=(Account)args.getSerializable("account");
        }

        adapter=new DebtArrayAdapter(getActivity());
        if(expenses != null) {
            ResultOutput resultOutput = new ComputeResult().computeDebts(expenses, participants);
            adapter.clear();
            adapter.addAll(resultOutput.debts);
        }

        listView.setAdapter(adapter);
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
        super.onSaveInstanceState(outState);
    }
}
