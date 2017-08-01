package com.shareexpenses.app;

import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jscreve on 26/09/2014.
 */
public class ResultOutput {

    public Participant participant;

    public List<Debt> debts = new ArrayList<Debt>();

    public String toString() {
        String output = "";
        for(Debt debt : debts) {
            output += debt.toString();
        }
        return output;
    }
}
