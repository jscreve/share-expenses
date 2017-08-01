package com.shareexpenses.app;

import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;
import com.shareexpenses.app.model.ParticipantForExpense;

import java.util.*;

/**
 * Created by jscreve on 23/09/2014.
 */
public class ComputeResult {

    //store participants in array, create indexes
    private Map<Participant, Integer> participantsIndexes = new HashMap<Participant, Integer>();
    private Map<Integer, Participant> participantsReverseIndexes = new HashMap<Integer, Participant>();
    private Map<Integer, Double> debts = new HashMap<Integer, Double>();

    //balance per particpant
    private Double[] balance;

    private List<Participant> participants;

    //4 steps
    //1 : compute allocated expenses per participan
    //2 : real expenses per participant
    //3 : compute postive ou negative sold per participant
    //4 : allocate sold repartition between participant
    public ResultOutput computeDebts(List<Expense> expenses, List<Participant> participants) {
        this.participants = participants;

        //for later computation
        storeParticipants(participants);

        //create temp data
        balance = new Double[participants.size()];
        for(int i = 0; i < participants.size(); i++) {
            balance[i] = 0.0d;
        }

        //compute expense allocation
        allocateExpenses(expenses);

        return computeDebts();
    }

    private void allocateExpenses(List<Expense> expenses) {
        for(Expense expense : expenses) {
            Double expenseValue = expense.getValue();
            Double expenseValuePerPayee = expenseValue / expense.getParticipantForExpenseList().size();
            Participant payer = expense.getPayer();
            Integer payerIndex = participantsIndexes.get(payer);
            for(ParticipantForExpense payee : expense.getParticipantForExpenseList()) {
                Participant payeeParticipant = payee.getParticipant();
                Integer payeeIndex = participantsIndexes.get(payeeParticipant);
                balance[payeeIndex] -= expenseValuePerPayee;
            }
            balance[payerIndex] += expenseValue;
        }
    }

    private class ParticipantIndexBalance {
        Integer participantIndex;
        Double balance;

        public ParticipantIndexBalance(Integer participantIndex, Double balance) {
            this.participantIndex = participantIndex;
            this.balance = balance;
        }
    }

    private ResultOutput computeDebts() {
        ResultOutput ro = new ResultOutput();

        //put everything in an array of participant debts
        ParticipantIndexBalance[] debts = new ParticipantIndexBalance[participants.size()];
        for(int i = 0; i < balance.length; i++) {
            debts[i] = new ParticipantIndexBalance(i, balance[i]);
        }

        //sort debts
        Arrays.sort(debts, new Comparator<ParticipantIndexBalance>() {
            public int compare(ParticipantIndexBalance o1, ParticipantIndexBalance o2) {
                return o1.balance.compareTo(o2.balance);
            }
        });

        //go through debts and generate result
        for(int i = 0; i < participants.size(); i++) {
            Double balanceFrom = debts[i].balance;
            if(balanceFrom < 0) {
                //dispatch the debt to others
                for (int j = participants.size() - 1; j >= 0; j--) {
                    if((balanceFrom = debts[i].balance) < 0) {
                        Double balanceTo = debts[j].balance;
                        if (balanceTo > 0) {
                            //generate a payment
                            Double payment = Math.min(balanceTo, -balanceFrom);
                            //System.out.println("Payement from : " + debts[i].participantIndex + " debt to : " + debts[j].participantIndex + " value : " + payment);
                            ro.debts.add(new Debt(participantsReverseIndexes.get(debts[i].participantIndex), participantsReverseIndexes.get(debts[j].participantIndex), payment));
                            debts[i].balance += payment;
                            debts[j].balance -= payment;
                        }
                    }
                }
            }
        }
        return ro;
    }

    private void storeParticipants(List<Participant> participants) {
        int i = 0;
        for(Participant participant : participants) {
            participantsIndexes.put(participant, i);
            participantsReverseIndexes.put(i, participant);
            i++;
        }
    }

    /*public static void main(String[] args) {

        List<Expense> expenses = new ArrayList<Expense> ();
        List<Participant> participants = new ArrayList<Participant>();
        Participant participant1 = new Participant("toto", "1");
        Participant participant2 = new Participant("toto2", "2");
        Participant participant3 = new Participant("toto3", "3");
        participants.add(participant1);
        participants.add(participant2);
        participants.add(participant3);

        //1
        Expense expense1 = new Expense(24.2d, "exp1", participant1, null, null);
        List<ParticipantForExpense> participantForExpenses = new ArrayList<ParticipantForExpense>();
        participantForExpenses.add(new ParticipantForExpense(participant2, expense1, 100.0d));
        participantForExpenses.add(new ParticipantForExpense(participant3, expense1, 100.0d));
        expense1.setParticipantForExpenseList(participantForExpenses);

        //2
        Expense expense2 = new Expense(26.2d, "exp2", participant2, null, null);
        List<ParticipantForExpense> participantForExpenses2 = new ArrayList<ParticipantForExpense>();
        participantForExpenses2.add(new ParticipantForExpense(participant1, expense2, 100.0d));
        participantForExpenses2.add(new ParticipantForExpense(participant3, expense2, 100.0d));
        expense2.setParticipantForExpenseList(participantForExpenses2);

        //3
        Expense expense3 = new Expense(30.2d, "exp3", participant3, null, null);
        List<ParticipantForExpense> participantForExpenses3 = new ArrayList<ParticipantForExpense>();
        participantForExpenses3.add(new ParticipantForExpense(participant1, expense3, 100.0d));
        participantForExpenses3.add(new ParticipantForExpense(participant2, expense3, 100.0d));
        expense3.setParticipantForExpenseList(participantForExpenses3);

        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        ResultOutput output = new ComputeResult().computeDebts(expenses, participants);

        System.out.println(output);

        double[][] debts = new double[4][4];
        debts[0][0] = 0;
        debts[0][1] = 1;
        debts[0][2] = 2;
        debts[0][3] = 3;

        debts[1][0] = 0;
        debts[1][1] = 0;
        debts[1][2] = -2;
        debts[1][3] = 3;

        debts[1][0] = 0;
        debts[1][1] = 0;
        debts[1][2] = -2;
        debts[1][3] = 3;

        debts[2][0] = 0;
        debts[2][1] = 0;
        debts[2][2] = 0;
        debts[2][3] = 4;

        debts[3][0] = 0;
        debts[3][1] = 0;
        debts[3][2] = 0;
        debts[3][3] = 0;

        new ComputeResult().reduceDebts(debts);
    }*/
}
