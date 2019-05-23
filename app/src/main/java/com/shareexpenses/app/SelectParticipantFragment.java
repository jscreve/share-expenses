package com.shareexpenses.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jess on 18/09/2014.
 */
public class SelectParticipantFragment extends Fragment {

    private ArrayList<Participant> participantsForAccount;
    private Expense expense;
    private ListView listView;
    private Button okButton;
    private SelectParticipantArrayAdapter selectParticipantArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;

        view=inflater.inflate(R.layout.participants_list,container,false);

        listView = (ListView)view.findViewById(R.id.participantsListView);

        setHasOptionsMenu(true);

        ArrayList<Participant> inputParticipants=null;

        if (null!=savedInstanceState)
        {
            participantsForAccount=(ArrayList<Participant>)savedInstanceState.getSerializable("participantsForAccount");
        }
        else
        {
            Bundle args;
            args=getArguments();
            participantsForAccount=(ArrayList<Participant>)args.getSerializable("participantsForAccount");
            inputParticipants=(ArrayList<Participant>)args.getSerializable("selectedParticipants");
        }

        selectParticipantArrayAdapter=new SelectParticipantArrayAdapter(getActivity());
        if(participantsForAccount != null) {
            selectParticipantArrayAdapter.clear();
            selectParticipantArrayAdapter.addAll(participantsForAccount);
        }

        listView.setAdapter(selectParticipantArrayAdapter);

        //initialize default values
        if(inputParticipants == null) {
            inputParticipants=new ArrayList<Participant>();
        } else {
            for(Participant participant : inputParticipants) {
                selectParticipantArrayAdapter.setCheck(true, participant);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Participant participant = selectParticipantArrayAdapter.getItem(position);
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.participant_checkbox);
                checkBox.toggle();
                selectParticipantArrayAdapter.setCheck(checkBox.isChecked(), participant);
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions_validate_cancel, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_accept);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //retrieve participants
                Map<Participant, Boolean> isParticipantSelected = selectParticipantArrayAdapter.getIsParticipantSelectedMap();
                //update DB with participant selection
                UpdateParticipantsInterface updateParticipantsInterface = (UpdateParticipantsInterface)getTargetFragment();
                ArrayList<Participant> participantsForExpense = new ArrayList<Participant>();
                for(Map.Entry<Participant, Boolean> isParticipantSelectedIterator : isParticipantSelected.entrySet()) {
                    if(isParticipantSelectedIterator.getValue().equals(true)) {
                        participantsForExpense.add(isParticipantSelectedIterator.getKey());
                    }
                }
                updateParticipantsInterface.setSelectedParticipants(participantsForExpense);

                //pop window
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                return false;
            }
        });

        MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(participantsForAccount != null) {
            outState.putSerializable("participantsForAccount", participantsForAccount);
        }
        super.onSaveInstanceState(outState);
    }
}
