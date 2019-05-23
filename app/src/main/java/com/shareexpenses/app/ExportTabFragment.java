package com.shareexpenses.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import com.shareexpenses.app.model.Account;
import com.shareexpenses.app.model.Category;
import com.shareexpenses.app.model.Expense;
import com.shareexpenses.app.model.Participant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.shareexpenses.app.PermissionManager.*;

/**
 * Created by dlta on 14/03/2014.
 */
public class ExportTabFragment extends Fragment {

    Button exportButton;
    Account account;
    ArrayList<Expense> expenses;
    ArrayList<Participant> participantsForAccount;
    ArrayList<Category> categoriesForAccount;

    Button startButton;
    Button endButton;
    Button importSQLButton;
    Button exportSQLButton;
    Button exportSQLMailButton;
    private Date[] startDate = new Date[1];
    private Date[] endDate = new Date[1];
    private boolean permissionGranted = false;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        final View view=inflater.inflate(R.layout.export_tab_layout,container,false);
        startButton=(Button)view.findViewById(R.id.start_date);
        endButton=(Button)view.findViewById(R.id.end_date);
        importSQLButton=(Button)view.findViewById(R.id.import_sql);
        exportSQLButton=(Button)view.findViewById(R.id.export_sql);
        exportSQLMailButton=(Button)view.findViewById(R.id.export_sql_mail);
        exportButton=(Button)view.findViewById(R.id.export_file);

        setHasOptionsMenu(true);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDatePickerDialog(v, getActivity(), startDate, startButton);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDatePickerDialog(v, getActivity(), endDate, endButton);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
                String sDate = simpleDateFormat.format(new Date());
                String tempFileName = new StringBuilder().append("QIF_").append(sDate).append(".qif").toString();
                List<Expense> tempExpenses=expenses;
                //extract expenses
                if(startDate[0] != null && endDate[0] != null) {
                    tempExpenses=ComputeReport.extractExpenses(startDate[0], endDate[0], expenses, null, null);
                }
                File outputFile=QIFUtils.generateQIFFile(tempExpenses, categoriesForAccount, account.getAccountName(), tempFileName, getActivity());
                //need to fill up the email
                MailUtils.sendMail(getActivity(), "", "QIF file", "QIF file", outputFile);
            }
        });

        importSQLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File input = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.import_sql_file) + " " + input.getPath())
                        .setNeutralButton(getString(R.string.copy), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //copy SQL data
                                MainActivity mainActivity=(MainActivity)getActivity();
                                File path = mainActivity.getDatabasePath(Data.DB_NAME);
                                File input = null;
                                if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    permissionGranted = false;
                                    requestPermission(IMPORT_WRITE_EXTERNAL_STORAGE);
                                } else {
                                    permissionGranted = true;
                                }
                                if(permissionGranted) {
                                    input = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                                    //copy
                                    try {
                                        Util.copyFileUsingChannel(input, path);
                                    } catch (Exception ex) {
                                        Log.e("copy file", "sql file copy failed", ex);
                                    }
                                }

                                //reload everything
                                mainActivity.reloadDataAndOpenDrawer();
                                mainActivity.popFragment();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });

        exportSQLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.copy_sql_file) + " " + output.getPath())
                        .setNeutralButton(getString(R.string.copy), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //copy SQL data
                                MainActivity mainActivity=(MainActivity)getActivity();
                                File path = mainActivity.getDatabasePath(Data.DB_NAME);
                                File output = null;
                                if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    permissionGranted = false;
                                    requestPermission(EXPORT_WRITE_EXTERNAL_STORAGE);
                                } else {
                                    permissionGranted = true;
                                }
                                if(permissionGranted) {
                                    output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                                    //copy
                                    try {
                                        Util.copyFileUsingChannel(path, output);
                                    } catch (Exception ex) {
                                        Log.e("copy file", "sql file copy failed", ex);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });

        exportSQLMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.export_sql_file))
                        .setNeutralButton(getString(R.string.export), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //export SQL data
                                MainActivity mainActivity=(MainActivity)getActivity();
                                File path = mainActivity.getDatabasePath(Data.DB_NAME);
                                File output = null;
                                if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    permissionGranted = false;
                                    requestPermission(MAIL_WRITE_EXTERNAL_STORAGE);
                                } else {
                                    permissionGranted = true;
                                }
                                if(permissionGranted) {
                                    output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                                    //copy
                                    try {
                                        Util.copyFileUsingChannel(path, output);
                                        MailUtils.sendMail(getActivity(), "", "SQL file", "SQL file", output);
                                    } catch (Exception ex) {
                                        Log.e("copy file", "sql file copy failed", ex);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });

        if (null!=savedInstanceState)
        {
            expenses=(ArrayList<Expense>)savedInstanceState.getSerializable("expenses");
            account=(Account)savedInstanceState.getSerializable("account");
            participantsForAccount=(ArrayList<Participant>)savedInstanceState.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)savedInstanceState.getSerializable("categories");
        }
        else
        {
            Bundle args;
            args=getArguments();
            expenses=(ArrayList<Expense>)args.getSerializable("expenses");
            account=(Account)args.getSerializable("account");
            participantsForAccount=(ArrayList<Participant>)args.getSerializable("participants");
            categoriesForAccount=(ArrayList<Category>)args.getSerializable("categories");
        }

        if(account == null) {
            startButton.setEnabled(false);
            endButton.setEnabled(false);
            importSQLButton.setEnabled(true);
            exportSQLButton.setEnabled(false);
            exportSQLMailButton.setEnabled(false);
            exportButton.setEnabled(false);
        } else {
            startButton.setEnabled(true);
            endButton.setEnabled(true);
            importSQLButton.setEnabled(true);
            exportSQLButton.setEnabled(true);
            exportSQLMailButton.setEnabled(true);
            exportButton.setEnabled(true);
        }

        return view;
    }

    private void requestPermission(int code) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
        } else {
            // permission has not been granted yet. Request it directly.
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MAIL_WRITE_EXTERNAL_STORAGE:
                //premission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MainActivity mainActivity=(MainActivity)getActivity();
                    File path = mainActivity.getDatabasePath(Data.DB_NAME);
                    File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                    //copy
                    try {
                        Util.copyFileUsingChannel(path, output);
                        MailUtils.sendMail(getActivity(), "", "SQL file", "SQL file", output);
                    } catch (Exception ex) {
                        Log.e("copy file", "sql file copy failed", ex);
                    }
                }
                return;
            case EXPORT_WRITE_EXTERNAL_STORAGE:
                //premission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MainActivity mainActivity=(MainActivity)getActivity();
                    File path = mainActivity.getDatabasePath(Data.DB_NAME);
                    File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                    try {
                        Util.copyFileUsingChannel(path, output);
                    } catch (Exception ex) {
                        Log.e("copy file", "sql file copy failed", ex);
                    }
                }
                return;
            case IMPORT_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MainActivity mainActivity=(MainActivity)getActivity();
                    File path = mainActivity.getDatabasePath(Data.DB_NAME);
                    File input = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Data.DB_NAME);
                    //copy
                    try {
                        Util.copyFileUsingChannel(input, path);
                    } catch (Exception ex) {
                        Log.e("copy file", "sql file copy failed", ex);
                    }
                    mainActivity.reloadDataAndOpenDrawer();
                    mainActivity.popFragment();
                }
                return;
            // other 'case' lines to check for other
            // permissions this app might request
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
        if(categoriesForAccount != null) {
            outState.putSerializable("categories", categoriesForAccount);
        }
        if(participantsForAccount != null) {
            outState.putSerializable("participants", participantsForAccount);
        }
    }
}
