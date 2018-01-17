package com.aspanta.emcsec.ui.fragment.dialogFragmentAddAddressBook;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookEmercoinFragment;

import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.REGEX_EMC_ADDRESS;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class DialogFragmentAddAddressBookEmercoin extends DialogFragment {

    private Button mBtnAdd, mBtnCancel;
    private EditText mEtLabel, mEtAddress;
    private Disposable addressDisposable;
    static AddressBookEmercoinFragment mAddressBookEmercoinFragment;

    public static DialogFragmentAddAddressBookEmercoin newInstance(AddressBookEmercoinFragment addressBookEmercoinFragment) {
        DialogFragmentAddAddressBookEmercoin fragment = new DialogFragmentAddAddressBookEmercoin();
        mAddressBookEmercoinFragment = addressBookEmercoinFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_add_address_book_emercoin, container, false);
        init(view);

        setCancelable(false);
        validateFields();

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnAdd.setOnClickListener(o -> {

            App.getDbInstance().emcAddressBookDao().insertAll(new EmcAddressBook(mEtLabel.getText().toString(), mEtAddress.getText().toString()));
            mAddressBookEmercoinFragment.setAddressesList(App.getDbInstance().emcAddressBookDao().getAll());
            dismiss();
        });
        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void validateFields() {

        addressDisposable = textChanges(mEtAddress)
                .map(inputText -> addressValidation(inputText.toString()))
                .subscribe(isValid -> {
                    if (isValid) {
                        setDefaultUnderline(mEtAddress);
                    } else {
                        if (mEtAddress.getText().toString().isEmpty()) {
                            setDefaultUnderline(mEtAddress);
                        } else {
                            setErrorUnderline(mEtAddress);
                        }
                    }
                    mBtnAdd.setEnabled(isValid);
                });
    }

    private boolean addressValidation(String address) {
        return address.matches(REGEX_EMC_ADDRESS);
    }

    private void setErrorUnderline(EditText editText) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.red));
            editText.setBackgroundTintList(colorStateList);
            editText.getBackground().mutate().setColorFilter
                    (ContextCompat.getColor(getContext(), R.color.red), PorterDuff.Mode.SRC_ATOP);
        } else {
            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.red));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        }
    }

    private void setDefaultUnderline(EditText editText) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent));
            editText.setBackgroundTintList(colorStateList);
            editText.getBackground().mutate().setColorFilter
                    (ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        } else {
            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (addressDisposable != null) {
            addressDisposable.dispose();
        }
    }

    private void init(View view) {
        mBtnAdd = view.findViewById(R.id.btn_add_new_address_book_emercoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_new_address_book_emercoin);
        mEtLabel = view.findViewById(R.id.et_label_add_address_book_emercoin);
        mEtAddress = view.findViewById(R.id.et_address_add_address_book_emercoin);
    }
}
