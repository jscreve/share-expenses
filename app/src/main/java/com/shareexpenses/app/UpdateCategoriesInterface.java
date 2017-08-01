package com.shareexpenses.app;

import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Participant;

import java.util.ArrayList;

/**
 * Created by jscreve on 24/08/15.
 */
public interface UpdateCategoriesInterface {
    public void setSelectedCategories(ArrayList<Category> selectedCategories);
}
