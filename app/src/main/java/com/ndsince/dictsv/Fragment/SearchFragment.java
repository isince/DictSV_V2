package com.ndsince.dictsv.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ndsince.dictsv.DAO.CategoryDAO;
import com.ndsince.dictsv.DAO.DBHelper;
import com.ndsince.dictsv.DAO.WordDAO;
import com.ndsince.dictsv.LogCheck;
import com.ndsince.dictsv.MainActivity;
import com.ndsince.dictsv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.ndsince.dictsv.Fragment.SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private final String TAG = "SearchFragment";

    Button btnChoiceCate;

    private WordDAO wordDAO;
    private CategoryDAO categoryDAO;

    private View rootView;

    ArrayList<String> listStringAllCategoryName;
    ArrayList<String> listStringSelectCate;
    List<String> listStringCategoryName;
    boolean[] booleanSelectCate, booleanCheckItem;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        wordDAO = new WordDAO(getActivity());
        categoryDAO = new CategoryDAO(getActivity());

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //initWidget and data
        initWidget();
        setOnClickListenerButton();

        return rootView;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * initWidget
     */
    private void initWidget() {
        btnChoiceCate = (Button) rootView.findViewById(R.id.btnChoiceCate);
    }

    /**
     * setDialogSelectCategory
     */
    private void setDialogSelectCategory() {
        listStringAllCategoryName = categoryDAO.getAllStringListCategory();
        listStringCategoryName = new ArrayList<>();


        //set Check box list
        listStringCategoryName.add("All category");
        for (String categoryName : listStringAllCategoryName)
            listStringCategoryName.add(categoryName);
        CharSequence[] charSequences = listStringCategoryName.toArray(
                new CharSequence[listStringCategoryName.size()]);

        //set boolean array
        if (booleanSelectCate == null) { // first run
            int i = 0;
            booleanSelectCate = new boolean[listStringCategoryName.size()];
            booleanCheckItem = new boolean[listStringCategoryName.size()];

            for (boolean b : booleanSelectCate) {
                booleanSelectCate[i] = true;
                i++;
            }
            booleanCheckItem = booleanSelectCate.clone();
        } else {
            booleanCheckItem = booleanSelectCate.clone();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Category");

        dialog.setMultiChoiceItems(charSequences, booleanCheckItem, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (which == 0) {//check all cate
                        int i = 0;
                        for (boolean b : booleanSelectCate) {
                            booleanCheckItem[i] = true;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, true);
                            i++;
                        }
                    } else {
                        if (getBooleanCountTrue(booleanCheckItem) ==
                                listStringCategoryName.size() - 1) {
                            booleanCheckItem[0] = true;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, true);
                        }
                        booleanCheckItem[which] = true;
                    }

                } else {
                    if (which == 0) {
                        int i = 0;
                        for (boolean b : booleanSelectCate) {
                            booleanCheckItem[i] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                            i++;
                        }
                    } else {
                        if (booleanCheckItem[0]) {
                            booleanCheckItem[0] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                        }
                        booleanCheckItem[which] = false;
                    }

                }
            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set list select category
                listStringSelectCate = new ArrayList<>();
                LogCheck.e(TAG, "Onclick", String.valueOf(getBooleanCountTrue(booleanCheckItem)));
                int i = 0;
                for (boolean b : booleanCheckItem) {
                    if(b) listStringSelectCate.add(listStringCategoryName.get(i));
                    i++;
                }

                getWordList();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * setOnClickListenerButton
     */
    private void setOnClickListenerButton() {

        //Button
        btnChoiceCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialogSelectCategory();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    /**
     * getWordList
     */
    private void getWordList() {
        //TODO convert list to database

        if (listStringSelectCate.contains(listStringCategoryName.get(0))) {
            //search all category
        } else {

            //whrerArg : Cate_id = '0'
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < listStringSelectCate.size(); i++) {

                buffer.append(DBHelper.COLUMN_WORD_CATEGORY_ID);
                buffer.append(" = '");
                buffer.append(categoryDAO.getCategoryObjByName(
                        listStringSelectCate.get(i)).getmId());
                buffer.append("'");

                if (i < listStringSelectCate.size() - 1) buffer.append(" OR ");
            }

            LogCheck.d(TAG, "getWordList", buffer.toString());

            // search by category id

            LogCheck.d(TAG, "TestFragment", String.valueOf(((MainActivity)getActivity()).getwordlist()));

        }
    }

    /**
     * getBooleanCountTrue
     * @param booleanArray
     * @return
     */
    public int getBooleanCountTrue(boolean[] booleanArray) {
        int chkTrue = 0;
        for (boolean b : booleanArray) {
            if (b) chkTrue++;
        }
        return chkTrue;
    }
}
