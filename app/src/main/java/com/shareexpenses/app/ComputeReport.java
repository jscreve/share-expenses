package com.shareexpenses.app;

import com.shareexpenses.app.model.*;

import java.util.*;

/**
 * Created by jscreve on 23/09/2014.
 */
public class ComputeReport {

    /**
     * Extract expenses assuming there are ordered by date
     *
     * @param from
     * @param to
     * @param expenses
     * @param payers
     * @param categories
     * @return
     */
    public static List<Expense> extractExpenses(Date from, Date to, List<Expense> expenses, List<Participant> payers, List<Category> categories) {
        List<Expense> outputExpenses = new ArrayList<Expense>();
        Set<Participant> payersSet = null;
        if(payers != null) {
            payersSet = new HashSet<Participant>(payers);
        }
        Set<Category> categoriesSet = null;
        if(categories != null) {
            categoriesSet = new HashSet<Category>(categories);
        }
        for (Expense expense : expenses) {
            if (    expense.getDate().after(to) ||
                    expense.getDate().before(from) ||
                    (categories != null && !categories.isEmpty() && !isCategoryInExpense(categoriesSet, expense)) ||
                    (payers != null && !payers.isEmpty() && !isPayerInExpense(payersSet, expense))) {
                continue;
            } else {
                outputExpenses.add(expense);
            }
        }
        return outputExpenses;
    }

    private static boolean isCategoryInExpense(Set<Category> categories, Expense expense) {
        boolean isInExpense = false;
        for(CategoryForExpense categoryForExpense : expense.getCategoriesForExpense()) {
            if(categories.contains(categoryForExpense.getCategory())) {
                isInExpense = true;
                break;
            }
        }
        return isInExpense;
    }

    private static boolean isPayerInExpense(Set<Participant> payers, Expense expense) {
        boolean isInExpense = false;
        if(payers.contains(expense.getPayer())) {
            isInExpense = true;
        }
        return isInExpense;
    }

    public static class ExpensesDetails {
        public Map<String, Double> expensesByCategory = new HashMap<String, Double>();
        public Double totalExpenses=0.0d;
    }

    public static class ExpensesDetailsPerMonth {
        public Map<Date, Double> expensesByCategory = new HashMap<Date, Double>();
        public Double totalExpenses=0.0d;
    }

    public static ExpensesDetails computeExpensePerCategory(Date from, Date to, List<Expense> expenses, List<Participant> payersToFilter, List<Category> categoriesToFilter) {
        //filter out expenses
        List<Expense> filteredExpenses = extractExpenses(from, to, expenses, payersToFilter, categoriesToFilter);

        ExpensesDetails expensesDetails = new ExpensesDetails();

        //compute total expenses and expenses per category
        for(Expense expense : filteredExpenses) {
            expensesDetails.totalExpenses += expense.getValue();
            List<CategoryForExpense> categories = expense.getCategoriesForExpense();
            if(categories.size() > 0) {
                Double expensePerCategory = expense.getValue() / (double) categories.size();
                for (CategoryForExpense categoryForExpense : categories) {
                    Category category1 = categoryForExpense.getCategory();
                    Double tempExpensePerCategory;
                    if ((tempExpensePerCategory = expensesDetails.expensesByCategory.get(category1.getName())) == null) {
                        tempExpensePerCategory = 0.0d;
                    }
                    tempExpensePerCategory += expensePerCategory;
                    expensesDetails.expensesByCategory.put(category1.getName(), tempExpensePerCategory);
                }
            } else {
                Double tempExpensePerCategory;
                if ((tempExpensePerCategory = expensesDetails.expensesByCategory.get(Category.EMPTY_CATEGORY)) == null) {
                    tempExpensePerCategory = 0.0d;
                }
                tempExpensePerCategory += expense.getValue();
                expensesDetails.expensesByCategory.put(Category.EMPTY_CATEGORY, tempExpensePerCategory);
            }
        }
        return expensesDetails;
    }

    public static ExpensesDetailsPerMonth computeExpensePerMonth(Date from, Date to, List<Expense> expenses, List<Participant> payersToFilter, List<Category> categoriesToFilter) {
        //filter out expenses
        List<Expense> filteredExpenses = extractExpenses(from, to, expenses, payersToFilter, categoriesToFilter);

        ExpensesDetailsPerMonth expensesDetailsPerMonth = new ExpensesDetailsPerMonth();

        //compute total expenses and expenses per month
        for(Expense expense : filteredExpenses) {
            expensesDetailsPerMonth.totalExpenses += expense.getValue();
            Date month = getExpenseMonth(expense);
            Double tempExpensePerCategory;
            if ((tempExpensePerCategory = expensesDetailsPerMonth.expensesByCategory.get(month)) == null) {
                tempExpensePerCategory = 0.0d;
            }
            tempExpensePerCategory += expense.getValue();
            expensesDetailsPerMonth.expensesByCategory.put(month, tempExpensePerCategory);
        }
        return expensesDetailsPerMonth;
    }

    private static Date getExpenseMonth(Expense expense) {
        return Util.getDateFromMonth(Util.getMonth(expense.getDate()));
    }
}
