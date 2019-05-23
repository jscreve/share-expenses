package com.shareexpenses.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by dlta on 14/03/2014.
 */
public class DisplayStatsTabFragment extends Fragment {

    Account account;
    private ArrayList<Expense> expenses;
    private ArrayList<Category> categoriesToDisplay = null;
    private ArrayList<Participant> payersToDisplay = null;
    private Date startDate = null;
    private Date endDate = null;
    private DecimalFormat oneDecimalFormat = new DecimalFormat("#.0");
    private Integer graphStyle;
    public static int PER_CATEGORY = 0;
    public static int PER_MONTH = 1;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.display_stats_tab_layout,container,false);

        if (null!=savedInstanceState)
        {
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
            account=(Account)savedInstanceState.getSerializable("account");
            categoriesToDisplay = (ArrayList<Category>)savedInstanceState.getSerializable("categoriesToDisplay");
            payersToDisplay = (ArrayList<Participant>)savedInstanceState.get("payersToDisplay");
            startDate = (Date)savedInstanceState.getSerializable("startDate");
            endDate = (Date)savedInstanceState.getSerializable("endDate");
            graphStyle = (Integer)savedInstanceState.getSerializable("graphStyle");
        }
        else
        {
            Bundle args;
            args=getArguments();
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
            account=(Account)args.getSerializable("account");
            if(args.getSerializable("categoriesToDisplay") != null) {
                categoriesToDisplay = (ArrayList<Category>) args.getSerializable("categoriesToDisplay");
            }
            if(args.getSerializable("payersToDisplay") != null) {
                payersToDisplay = (ArrayList<Participant>) args.getSerializable("payersToDisplay");
            }
            startDate = (Date)args.getSerializable("startDate");
            endDate = (Date)args.getSerializable("endDate");
            graphStyle = (Integer)args.getSerializable("graphStyle");
        }

        // retrieve data
        if(startDate != null && endDate != null) {
            ComputeReport.ExpensesDetails expensesDetails = null;
            if(graphStyle == PER_CATEGORY) {
                displayCategoryGraph(view);
            } else {
                displayPerMonthGraph(view);
            }
        }
        return view;
    }

    private void displayCategoryGraph(final View view) {
        ComputeReport.ExpensesDetails expensesDetails = ComputeReport.computeExpensePerCategory(startDate, endDate, expenses, payersToDisplay, categoriesToDisplay);
        int nbElements = expensesDetails.expensesByCategory.entrySet().size();
        //Graph lib won't display if only one element
        if(nbElements > 1) {
            String[] titles = new String[nbElements];
            DataPoint[] dataValues = new DataPoint[nbElements];
            int i = 0;
            TreeMap<String, Double> orderedEntries = new TreeMap<String, Double>(expensesDetails.expensesByCategory);
            for (TreeMap.Entry<String, Double> entry : orderedEntries.entrySet()) {
                dataValues[i] = new DataPoint(i, entry.getValue());
                titles[i] = entry.getKey();
                i++;
            }
            BarGraphSeries<DataPoint> series = new BarGraphSeries(dataValues);
            GraphView graphView = new GraphView(getActivity());
            graphView.setTitle("Stats, total : " + oneDecimalFormat.format(expensesDetails.totalExpenses));

            graphView.addSeries(series); // data
            graphView.getViewport().setScalable(false);
            graphView.getViewport().setScrollable(false);

            //style
            graphView.getGridLabelRenderer().setTextSize(20.0f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(75);
            graphView.getGridLabelRenderer().setNumVerticalLabels(10);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(6);
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(titles);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearLayout1);
            layout.removeAllViewsInLayout();
            layout.addView(graphView);
        }
    }

    private void displayPerMonthGraph(final View view) {
        ComputeReport.ExpensesDetailsPerMonth expensesDetails = ComputeReport.computeExpensePerMonth(startDate, endDate, expenses, payersToDisplay, categoriesToDisplay);
        int nbElements = expensesDetails.expensesByCategory.entrySet().size();
        //Graph lib won't display if only one element
        if(nbElements > 1) {
            String[] titles = new String[nbElements];
            DataPoint[] dataValues = new DataPoint[nbElements];
            int i = 0;
            TreeMap<Date, Double> orderedEntries = new TreeMap<Date, Double>(expensesDetails.expensesByCategory);
            for (TreeMap.Entry<Date, Double> entry : orderedEntries.entrySet()) {
                dataValues[i] = new DataPoint(i, entry.getValue());
                titles[i] = Util.getMonth(entry.getKey());
                //dataValues[i] = new DataPoint(entry.getKey(), entry.getValue());
                i++;
            }
            BarGraphSeries<DataPoint> series = new BarGraphSeries(dataValues);
            GraphView graphView = new GraphView(getActivity());
            graphView.setTitle("Stats, total : " + oneDecimalFormat.format(expensesDetails.totalExpenses));

            graphView.addSeries(series); // data

            //dynamic viewport is quite ugly
            /*graphView.getViewport().setScalable(true);
            graphView.getViewport().setScrollable(true);

            //style
            graphView.getGridLabelRenderer().setTextSize(20.0f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(75);
            graphView.getGridLabelRenderer().setNumVerticalLabels(10);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), Util.monthFormatter1));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(dataValues.length);
            graphView.getViewport().setMinX(dataValues[0].getX());
            graphView.getViewport().setMaxX(dataValues[dataValues.length - 1].getX());
            graphView.getViewport().setXAxisBoundsManual(true);*/

            graphView.getViewport().setScalable(false);
            graphView.getViewport().setScrollable(false);

            //style
            graphView.getGridLabelRenderer().setTextSize(20.0f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(75);
            graphView.getGridLabelRenderer().setNumVerticalLabels(10);
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(titles);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(dataValues.length);

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearLayout1);
            layout.removeAllViewsInLayout();
            layout.addView(graphView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(account != null) {
            outState.putSerializable("account", account);
        }
        if(expenses != null) {
            outState.putSerializable("expenses", expenses);
        }
        if(categoriesToDisplay != null) {
            outState.putSerializable("categoriesToDisplay", categoriesToDisplay);
        }
        if(payersToDisplay != null) {
            outState.putSerializable("participantsToDisplay", payersToDisplay);
        }
        if(startDate != null) {
            outState.putSerializable("startDate", startDate);
        }
        if(endDate != null) {
            outState.putSerializable("endDate", endDate);
        }
        if(graphStyle != null) {
            outState.putSerializable("graphStyle", graphStyle);
        }
        super.onSaveInstanceState(outState);
    }
}
