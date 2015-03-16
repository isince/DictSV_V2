package com.ndsince.dictsv.Fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ndsince.dictsv.DAO.Category;
import com.ndsince.dictsv.DAO.CategoryDAO;
import com.ndsince.dictsv.DAO.Word;
import com.ndsince.dictsv.DAO.WordDAO;
import com.ndsince.dictsv.LogCheck;
import com.ndsince.dictsv.R;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.ndsince.dictsv.Fragment.EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    //TODO check input Language edtText correct
    //TODO Dialog การตัดคำภาษาไทย

    private final String TAG = "EditFragment";

    private EditText edtVocabWord, edtVocabTermino, edtVocabTrans, edtCateName;
    private Spinner spnTopCate, spnMidCate, spnBottomCate;
    private Button btnVocabCreate, btnVocabClear, btnCatCreate, btnCatClear, btnCatDelete;
    private CheckBox checkBoxCate;
    private View rootView;

    ArrayAdapter adapterSpinnerCategory;

    private CategoryDAO categoryDAO;
    private WordDAO wordDAO;
    private Word word;
    private Category category;

    ArrayList<String> listStringAllCategoryName;
    boolean chkSpinnerEmpty = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditFragment.
     */
    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        return fragment;
    }   // EditFragment newInstance()

    public EditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wordDAO = new WordDAO(getActivity());
        categoryDAO = new CategoryDAO(getActivity());

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        //initWidget and data
        initWidget();
        if (savedInstanceState == null ||
                !savedInstanceState.containsKey("listStringAllCategoryName")) {

            LogCheck.d(TAG, "setSpinnerCate[saveInstanceState]", "Don't have InstanceState");
            setSpinnerCate(true);

        } else {

            LogCheck.d(TAG, "setSpinnerCate[saveInstanceState]", "Have InstanceState");
            listStringAllCategoryName = savedInstanceState.
                    getStringArrayList("listStringAllCategoryName");
            setSpinnerCate(false);
        }

        //set Event
        setOnClickListenerButton();
        setTextChangedListenerEditText();

        return rootView;
    }

//--------------------------------------------------------------------------------------------------

    /**
     *
     */
    public void addWord() {
        boolean chkRepetitionWord;
        word = new Word();
        word = getAlleditTextToWordObj();

        chkRepetitionWord = wordDAO.chkRepetitionWord(word.getmWord(),
                categoryDAO.getCategoryObjByName(
                        spnTopCate.getSelectedItem().toString().trim()).getmId());

        if (chkRepetitionWord) {
            LogCheck.e(TAG, "addWord(chkRepetitionWord)", "พบคำซ้ำ");
            Toast.makeText(getActivity(), "พบคำซ้ำในหมวดนี้", Toast.LENGTH_SHORT).show();
        } else {
            LogCheck.d(TAG, "addWord(chkRepetitionWord)", "ไม่พบคำซ้ำ");
            wordDAO.addWord(word);
            clearAllEditText();
        }
    }

    /**
     *
     */
    public void addCategory() {
        if (checkBoxCate.isChecked()) { // New Category

            if (categoryDAO.chkRepetitionCategory(
                    edtCateName.getText().toString().trim())) {
                LogCheck.e(TAG, "addCategory(chkRepetitionCategory)", "พบหมวดซ้ำ");
                Toast.makeText(getActivity(), "พบหมวดซ้ำ", Toast.LENGTH_SHORT).show();
            } else {
                LogCheck.d(TAG, "addCategory(chkRepetitionCategory)", "ไม่พบหมวดซ้ำ");
                //Set capitalize
                categoryDAO.addCategory(
                        new Category(capitalize(edtCateName.getText().toString().trim())));

                setSpinnerCate(true);
                checkTextChangeEvent(1);
                edtCateName.setText("");
            }
        } else {    // update Category

            if (chkSpinnerEmpty) {
                setConfirmDialogNewCategory();
            } else {
                if (categoryDAO.chkRepetitionCategory(
                        edtCateName.getText().toString().trim())) {
                    LogCheck.e(TAG, "addCategory(chkRepetitionCategory)", "พบฃื่อหมวดที่คุณตั้งซ้ำกับของเดิมที่มีอยู่แล้ว");
                    Toast.makeText(getActivity(), "พบฃื่อหมวดที่คุณตั้งซ้ำกับของเดิมที่มีอยู่แล้ว", Toast.LENGTH_SHORT).show();
                } else {
                    categoryDAO.updateCategory(
                            new Category(categoryDAO.getCategoryObjByName(
                                    spnMidCate.getSelectedItem().toString().trim()).getmId(),
                                    capitalize(edtCateName.getText().toString().trim())));

                    setSpinnerCate(true);
                    edtCateName.setText("");
                }
            }
        }
    }

    /**
     * delCategory
     */
    public void delCategory() {
        /*
        * สามเงื่อนไข
        * - ไม่มีหมวดคำศัพท์เลย
        * - ลบหมวดมีคำศัพท์
        * - ลบหมวดที่ไม่มีคำศัพท์
        * */

        if (chkSpinnerEmpty) {

            LogCheck.d(TAG, "delCategory(chkSpinnerEmpty)", "ไม่มีหมวด!");
            Toast.makeText(getActivity(), "ไม่พบหมวดคำศัพท์!",Toast.LENGTH_SHORT).show();
        } else if (wordDAO.chkWordByCategoryID(categoryDAO.getCategoryObjByName(
                spnBottomCate.getSelectedItem().toString().trim()).getmId())) {

            LogCheck.d(TAG, "delCategory(chkWordByCategoryID)", "มีคำศัพท์");
            setConfirmDialogDeleteCategory(false);
        } else {

            LogCheck.d(TAG, "delCategory(chkWordByCategoryID)", "ไม่มีคำศัพท์");
            setConfirmDialogDeleteCategory(true);
        }
    }

//--------------------------------------------------------------------------------------------------

    /**
     * Start initial new widget
     */
    private void initWidget() {

        //initWidgetVocab
        edtVocabWord = (EditText) rootView.findViewById(R.id.edtVocabWord);
        edtVocabTermino = (EditText) rootView.findViewById(R.id.edtVocabTermino);
        edtVocabTrans = (EditText) rootView.findViewById(R.id.edtVocabTrans);

        spnTopCate = (Spinner) rootView.findViewById(R.id.spnVocabFirst);

        btnVocabCreate = (Button) rootView.findViewById(R.id.btnWordCreate);
        btnVocabClear = (Button) rootView.findViewById(R.id.btnWordClear);

        //initWidgetNewCate
        checkBoxCate = (CheckBox) rootView.findViewById(R.id.checkboxNewCate);

        spnMidCate = (Spinner) rootView.findViewById(R.id.spnCateSecond);

        edtCateName = (EditText) rootView.findViewById(R.id.edtCateName);

        btnCatCreate = (Button) rootView.findViewById(R.id.btnCateCreate);
        btnCatClear = (Button) rootView.findViewById(R.id.btnCateClear);

        //initWidgetDelCate
        spnBottomCate = (Spinner) rootView.findViewById(R.id.spnCateThird);

        btnCatDelete = (Button) rootView.findViewById(R.id.btnCateDel);
    }   //initWidget

    /**
     * Set setOnClickListener event on Button
     */
    private void setOnClickListenerButton() {

        //Create or Save button
        btnVocabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord();
            }
        });
        checkBoxCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set spinner false if checkBox has checked
                if (checkBoxCate.isChecked()) {   // new category
                    spnMidCate.setEnabled(false);
                    edtCateName.setHint("New Category");
                    btnCatCreate.setText("Create");
                } else {
                    spnMidCate.setEnabled(true);    // update category
                    edtCateName.setHint("Category");
                    btnCatCreate.setText("Save");
                }
            }
        });
        btnCatCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        btnCatDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delCategory();
            }
        });

        //Clear button
        btnCatClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllEditText();
            }
        });
        btnVocabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllEditText();
            }
        });

    }   // setOnClickListenerButton

    /**
     * Set addTextChangedListener event on editText
     */
    private void setTextChangedListenerEditText() {
        edtVocabWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTextChangeEvent(1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtVocabTermino.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTextChangeEvent(1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtVocabTrans.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTextChangeEvent(1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtCateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkTextChangeEvent(2);
            }
        });

        //------------------------------------------------------------------------------------------
        edtVocabTrans.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    spnTopCate.requestFocus();
                    spnTopCate.performClick();
                }
                return true;
            }
        });

    }   // setTextChangedListenerEditText

    /**
     * Load spinner category with run new thread
     *
     * @param chkRenewCate if set true for load new data from Category table
     */
    private void setSpinnerCate(final boolean chkRenewCate) {
        final Handler handler = new Handler();

        //background or custom thread - run long task
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Quarry Data From Category Table

                if (chkRenewCate) {
                    //TODO Create getAllStringListCategory at MainActivity
                    listStringAllCategoryName = categoryDAO.getAllStringListCategory();
                    chkSpinnerEmpty = listStringAllCategoryName.size() == 0;
                }

                adapterSpinnerCategory = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, listStringAllCategoryName);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Update all user interface

                        spnTopCate.setAdapter(adapterSpinnerCategory);
                        spnMidCate.setAdapter(adapterSpinnerCategory);
                        spnBottomCate.setAdapter(adapterSpinnerCategory);
                    }
                });
            }
        }).start();
    }   // setSpinnerCate

    /**
     * setConfirmDialogDeleteCategory Delete category
     *
     * @param chkWordsEmpty false : Have words in that category id
     */
    private void setConfirmDialogDeleteCategory(final boolean chkWordsEmpty) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation;

        TextView textDialogHaveWord = (TextView) dialog.findViewById(R.id.textDialogMsg1);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        if (chkWordsEmpty) textDialogHaveWord.setVisibility(View.INVISIBLE);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkWordsEmpty) {
                    categoryDAO.deleteCategory(categoryDAO.getCategoryObjByName(
                            spnBottomCate.getSelectedItem().toString().trim()), true);
                } else {
                    categoryDAO.deleteCategory(categoryDAO.getCategoryObjByName(
                            spnBottomCate.getSelectedItem().toString().trim()), false);
                }

                setSpinnerCate(true);
                dialog.dismiss();
                checkTextChangeEvent(1);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * setConfirmDialogNewCategory
     */
    private void setConfirmDialogNewCategory() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation;

        TextView textDialogHaveWord = (TextView) dialog.findViewById(R.id.textDialogMsg1);
        TextView textDialogMsg2 = (TextView) dialog.findViewById(R.id.textDialogMsg2);
        TextView textDialogMsg3 = (TextView) dialog.findViewById(R.id.textDialogMsg3);

        textDialogHaveWord.setVisibility(View.INVISIBLE);
        textDialogMsg3.setVisibility(View.INVISIBLE);

        textDialogMsg2.setText("คุณต้องการจะสร้างหมวดคำศัพท์หมวดใหม่ใช่หรือไม่?");

        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnConfirm.setText("Yes");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxCate.setChecked(true);
                addCategory();
                checkBoxCate.setChecked(false);

                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

//--------------------------------------------------------------------------------------------------

    /**
     * Check Input form editText to set visible button
     */
    private void checkTextChangeEvent(int iEventChange) {
        switch (iEventChange) {
            //Watcher Word editText
            case 1:
                if (!chkSpinnerEmpty && !isEmpty(edtVocabWord) && (!isEmpty(edtVocabTermino)
                        || !isEmpty(edtVocabTrans))) {
                    btnVocabCreate.setEnabled(true);
                } else {
                    btnVocabCreate.setEnabled(false);
                }
                break;
            //Watcher Category editText (add or Edit)
            case 2:
                if ((!isEmpty(edtCateName))) {
                    btnCatCreate.setEnabled(true);
                } else {
                    btnCatCreate.setEnabled(false);
                }
                break;
        }
    }   // checkTextChangeEvent

    /**
     * getTextToWord
     *
     * @return Word object
     */
    private Word getAlleditTextToWordObj() {
        word = new Word();

        word.setmWord(edtVocabWord.getText().toString().trim());
        word.setmTermino(edtVocabTermino.getText().toString().trim());
        word.setmTrans(edtVocabTrans.getText().toString().trim());

        //get id category database form spinner item
        word.setmCategory(categoryDAO.getCategoryObjByName(
                spnTopCate.getSelectedItem().toString().trim()));

        return word;
    }   // getTextToWord

//---------------------------------------------------------------------------------------------------
// /String

    /**
     * Check editText is empty
     *
     * @param editText
     * @return boolean
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }   // isEmpty

    /**
     * Set clear all editText
     */
    private void clearAllEditText() {
        //edt vocab form
        edtVocabWord.setText("");
        edtVocabTermino.setText("");
        edtVocabTrans.setText("");

        //edt vocab form
        edtCateName.setText("");
    }   // clearAllEditText

    /**
     * set first letter upper case
     *
     * @param str
     * @return
     */
    private String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

//-------------------------------------------------------------------------------------------------

    /**
     * set variable save InstanceState when activate fragment -
     * extends FragmentStatePagerAdapter only
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(!chkSpinnerEmpty)
            outState.putStringArrayList("listStringAllCategoryName", listStringAllCategoryName);

        super.onSaveInstanceState(outState);
    }   //onSaveInstanceState
}
