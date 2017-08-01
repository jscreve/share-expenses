package com.shareexpenses.app;

import com.shareexpenses.app.model.Participant;

import java.util.Locale;

/**
 * Created by jscreve on 26/09/2014.
 */
public class Debt {
    public Debt(Participant fromParticipant, Participant toParticipant, Double debt) {
        this.fromParticipant = fromParticipant;
        this.toParticipant = toParticipant;
        this.debt = debt;
    }
    public Participant fromParticipant;
    public Participant toParticipant;
    public Double debt;

    public String toString() {
        String output = "";
        if(debt > 0.0d) {
            String formattedDebt = String.format("%.2f", debt);
            output = fromParticipant + " " + MainApplication.getInstance().getString(R.string.must_pay) + " : " + formattedDebt + java.util.Currency.getInstance(Locale.getDefault()).getSymbol() + " " + MainApplication.getInstance().getString(R.string.to) + " " + toParticipant + "\n";
        }
        return output;
    }
}
