package com.shareexpenses.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jess on 17/09/2014.
 */
public class CurrencyDAO implements Serializable {

    private String accountName;

    private List<Participant> participantList;

    private List<Expense> expenses;

    public CurrencyDAO(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
