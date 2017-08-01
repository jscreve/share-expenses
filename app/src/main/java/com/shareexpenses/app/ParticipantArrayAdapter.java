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
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jess on 08/09/2014.
 */
public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {

    private List<Participant> participants;
    private boolean isCheckBoxVisible;

    public ParticipantArrayAdapter(Context context, boolean isCheckBoxVisible) {
        super(context, 0);
        this.isCheckBoxVisible = isCheckBoxVisible;
    }

    public void setAll(List<Participant> participants) {
        if(participants != null) {
            setNotifyOnChange(false);
            clear();
            addAll(participants);
            setNotifyOnChange(true);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Participant> toArrayList()
    {
        ArrayList<Participant> result;

        result = new ArrayList<Participant>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    public Map<Participant, Boolean> getIsParticipantSelectedMap() {
        return isParticipantSelectedMap;
    }

    public void setIsParticipantSelectedMap(Map<Participant, Boolean> isParticipantSelectedMap) {
        this.isParticipantSelectedMap = isParticipantSelectedMap;
    }

    private Map<Participant, Boolean> isParticipantSelectedMap = new HashMap<Participant, Boolean>();

    @Override
    public void remove(Participant object) {
        super.remove(object);
        isParticipantSelectedMap.remove(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.participant_line, null);
        }

        final Participant participant = getItem(position);

        //manage checkbox
        CheckBox checkBox=(CheckBox)v.findViewById(R.id.checkbox);

        //update check box after deletion
        if(isParticipantSelectedMap.get(participant) != null) {
            if(isParticipantSelectedMap.get(participant) == false) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        } else {
            checkBox.setChecked(false);
        }

        if(!isCheckBoxVisible) {
            checkBox.setVisibility(View.INVISIBLE);
        } else {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCheck(((CheckBox) v).isChecked(), participant);
                }
            });
        }

        TextView value_textView=(TextView)v.findViewById(R.id.participant_textView);

        value_textView.setText(participant.getName() + " " + participant.getLastName());
        return v;
    }

    public void setCheck(boolean value, Participant participant) {
        if (value) {
            isParticipantSelectedMap.put(participant, true);
        } else {
            isParticipantSelectedMap.put(participant, false);
        }
    }
}
