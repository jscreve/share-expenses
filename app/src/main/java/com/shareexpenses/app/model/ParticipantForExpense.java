package com.shareexpenses.app.model;

import java.io.Serializable;

/**
 * Created by jess on 22/09/2014.
 */
public class ParticipantForExpense implements Serializable {

    private Participant participant;

    private Expense expense;

    private double participation;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipantForExpense(Participant participant, Expense expense, double participation) {
        this.participant = participant;
        this.expense = expense;
        this.participation = participation;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public double getParticipation() {
        return participation;
    }

    public void setParticipation(double participation) {
        this.participation = participation;
    }
}
