package com.shareexpenses.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jess on 08/09/2014.
 */
public class SelectParticipantArrayAdapter extends ArrayAdapter<Participant> {

    private List<Participant> participants;

    public Map<Participant, Boolean> getIsParticipantSelectedMap() {
        return isParticipantSelectedMap;
    }

    public void setIsParticipantSelectedMap(Map<Participant, Boolean> isParticipantSelectedMap) {
        this.isParticipantSelectedMap = isParticipantSelectedMap;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    private Map<Participant, Boolean> isParticipantSelectedMap = new HashMap<Participant, Boolean>();

    public SelectParticipantArrayAdapter(Context context) {
        super(context, 0);
    }

    public void setAll(List<Participant> participants) {
        setNotifyOnChange(false);
        clear();
        addAll(participants);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public ArrayList<Participant> toArrayList()
    {
        ArrayList<Participant> result;

        result = new ArrayList<Participant>();
        for (int i = 0; i < getCount(); i++)
            result.add(getItem(i));

        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.select_participant_in_list, null);
        }

        final Participant participant = getItem(position);


        TextView participant_name_textView=(TextView)v.findViewById(R.id.participant_name_textView);
        CheckBox checkbox = (CheckBox)v.findViewById(R.id.participant_checkbox);

        //if participant is selected, enable checkbox
        if(isParticipantSelectedMap.get(participant) != null && isParticipantSelectedMap.get(participant) == true && checkbox.isChecked() ==false) {
            checkbox.toggle();
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck (((CheckBox) v).isChecked(), participant);
            }
        });

        participant_name_textView.setText(participant.toString());
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
