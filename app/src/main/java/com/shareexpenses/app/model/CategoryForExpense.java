package com.shareexpenses.app.model;

import java.io.Serializable;

/**
 * Created by jess on 17/09/2014.
 */
public class CategoryForExpense implements Serializable {

    private Category category;
    private Expense expense;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public CategoryForExpense(Category category, Expense expense) {
        this.category = category;
        this.expense=expense;
    }
}
