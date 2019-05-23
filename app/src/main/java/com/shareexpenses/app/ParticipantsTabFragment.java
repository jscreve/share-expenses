package com.shareexpenses.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;
import com.shareexpenses.app.model.ParticipantForExpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dlta on 14/03/2014.
 */
public class ParticipantsTabFragment extends Fragment {

    ListView listView;
    ArrayList<Participant> participants;
    ArrayList<Expense> expenses;
    Account account;
    private ParticipantArrayAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view;
		view=inflater.inflate(R.layout.friends_tab_layout,container,false);
        listView=(ListView)view.findViewById(R.id.participantlistView);

        setHasOptionsMenu(true);

        if (null!=savedInstanceState)
        {
            participants=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            account=(Account)savedInstanceState.getSerializable("account");
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
        }
        else
        {
            Bundle args;
            args=getArguments();
            participants=(ArrayList<Participant>)args.getSerializable("participants");
            account=(Account)args.getSerializable("account");
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
        }

        adapter=new ParticipantArrayAdapter(getActivity(), true);
        if(participants != null) {
            adapter.clear();
            adapter.addAll(participants);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Participant participant = adapter.getItem(position);
                CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkbox);
                checkBox.toggle();
                adapter.setCheck(checkBox.isChecked(), participant);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions_no_edit, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_new);
        menuItemNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addParticipantsFragment();
                //reset selected participants
                adapter.setIsParticipantSelectedMap(new HashMap<Participant, Boolean>());
                return false;
            }
        });

        /*MenuItem menuItem_cancel = menu.findItem(R.id.action_cancel);
        menuItem_cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                mainActivity.openDrawer();
                return false;
            }
        });*/

        MenuItem menuItemRemove = menu.findItem(R.id.action_remove);
        menuItemRemove.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //if participant is defined in the account, forbid the operation
                final List<Participant> participants1 = getSelectedParticipants();
                if(areParticipantsUsedInExpenses(participants1)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.participant_is_used_in_account))
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.action_remove_sure))
                            .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (Participant participant : participants1) {
                                        removeParticipant(participant);
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create().show();
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private boolean areParticipantsUsedInExpenses(List<Participant> participants) {
        boolean used = false;
        for(Participant participant : participants) {
            if(isParticipantUsedInExpenses(participant)) {
                used = true;
                break;
            }
        }
        return used;
    }

    private boolean isParticipantUsedInExpenses(Participant participant) {
        boolean used = false;
        if(expenses != null) {
            for (Expense expense : expenses) {
                List<ParticipantForExpense> participantForExpenseList = expense.getParticipantForExpenseList();
                if(participantForExpenseList != null) {
                    for(ParticipantForExpense participantForExpense : participantForExpenseList) {
                        if(participantForExpense.getParticipant().getId()==participant.getId()) {
                            used = true;
                            break;
                        }
                    }
                }
                if(used == true) {
                    break;
                }
            }
        }
        return used;
    }


    private List<Participant> getSelectedParticipants() {
        List<Participant> participants1 = new ArrayList<Participant>();

        Map<Participant, Boolean> selectedParticipants = adapter.getIsParticipantSelectedMap();
        for(Map.Entry<Participant, Boolean> entry : selectedParticipants.entrySet()) {
            if(entry.getValue().equals(true)) {
                Participant participant = entry.getKey();
                participants1.add(participant);
            }
        }
        return participants1;
    }

    private void displayWarningDialogBox(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    private void addParticipantsFragment() {
        AddParticipantFragment addParticipantFragment=new AddParticipantFragment();
        addParticipantFragment.setTargetFragment(this, 0);
        Bundle args=new Bundle();
        args.putSerializable("account", account);
        args.putSerializable("participants", participants);
        addParticipantFragment.setArguments(args);
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.pushFragment(addParticipantFragment);
    }

    public void addParticipant(Participant participant) {
        Data data = MainApplication.getInstance().getData();
        data.saveParticipant(participant);
        adapter.add(participant);
    }

    public void removeParticipant(Participant participant) {
        Data data = MainApplication.getInstance().getData();
        data.removeParticipant(participant);
        adapter.remove(participant);
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(account != null) {
            outState.putSerializable("account", account);
        }
        if(participants != null) {
            outState.putSerializable("participants", participants);
        }
        if(expenses != null) {
            outState.putSerializable("expenses", expenses);
        }
        super.onSaveInstanceState(outState);
    }
}
