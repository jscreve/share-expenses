package com.shareexpenses.app.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by jess on 17/09/2014.
 */
public class Expense implements Serializable {

    private Long id;

    private double value;

    private String name;

    private List<CategoryForExpense> categoriesForExpense;

    private Date date;

    private Currency currency;

    private Account account;

    private Participant payer;

    private List<ParticipantForExpense> participantForExpenseList;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Expense(double value, String name, Participant payer, Date date, Currency currency) {
        this.value = value;
        this.name = name;
        this.payer=payer;
        this.date = date;
        this.currency = currency;
    }

    public Expense(double value, List<CategoryForExpense> categories, String name, Participant payer, Date date, Currency currency) {
        this.value = value;
        this.categoriesForExpense = categories;
        this.name = name;
        this.payer=payer;
        this.date = date;
        this.currency = currency;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryForExpense> getCategoriesForExpense() {
        return categoriesForExpense;
    }

    public void setCategoriesForExpense(List<CategoryForExpense> categories) {
        this.categoriesForExpense = categories;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<ParticipantForExpense> getParticipantForExpenseList() {
        return participantForExpenseList;
    }

    public void setParticipantForExpenseList(List<ParticipantForExpense> participantForExpenseList) {
        this.participantForExpenseList = participantForExpenseList;
    }

    public Participant getPayer() {
        return payer;
    }

    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense)) return false;

        Expense expense = (Expense) o;

        if (id != null ? !id.equals(expense.id) : expense.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
