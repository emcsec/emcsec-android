package com.aspanta.emcsec.ui.fragment.dialogFragmentEditContactAddressBook;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.aspanta.emcsec.tools.Config.REGEX_EMC_ADDRESS;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;

public class DialogFragmentEditContactAddressBookEmercoin extends DialogFragment {

    private Button mBtnSave, mBtnCancel;
    private EditText mEtLabel, mEtAddress;
    private Disposable addressDisposable;
    static AddressBookEmercoinFragment sAddressBookEmercoinFragment;
    static EmcAddressBook sAddressPojo;
    static int sPosition;

    public static DialogFragmentEditContactAddressBookEmercoin newInstance(
            AddressBookEmercoinFragment addressBookEmercoinFragment, EmcAddressBook addressPojo, int position) {

        DialogFragmentEditContactAddressBookEmercoin fragment = new DialogFragmentEditContactAddressBookEmercoin();
        sAddressBookEmercoinFragment = addressBookEmercoinFragment;
        sAddressPojo = addressPojo;
        sPosition = position;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_edit_contact_address_book_emercoin, container, false);
        init(view);

        mEtLabel.setText(sAddressPojo.getLabel());
        mEtAddress.setText(sAddressPojo.getAddress());

        setCancelable(false);
        validateFields();

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnSave.setOnClickListener(o -> {

            String address = mEtAddress.getText().toString();
            String label = mEtLabel.getText().toString();

            new AsyncTask<Void, Void, List<EmcAddressBook>>() {
                @Override
                protected List doInBackground(Void... params) {

                    List<EmcAddressBook> addressPojoList = App.getDbInstance().emcAddressBookDao().getAll();
                    addressPojoList.remove(sPosition);

                    sAddressPojo.setAddress(address);
                    sAddressPojo.setLabel(label);

                    addressPojoList.add(sPosition, sAddressPojo);
                    App.getDbInstance().emcAddressBookDao().deleteAll();
                    App.getDbInstance().emcAddressBookDao().insertListEmcAdresses(addressPojoList);

                    return App.getDbInstance().emcAddressBookDao().getAll();
                }

                @Override
                protected void onPostExecute(List<EmcAddressBook> addresses) {
                    sAddressBookEmercoinFragment.setAddressesList(addresses);
                }
            }.execute();
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
                    mBtnSave.setEnabled(isValid);
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
        mBtnSave = view.findViewById(R.id.btn_save_contact_address_book_emercoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_address_book_emercoin);
        mEtLabel = view.findViewById(R.id.et_edit_label_address_book_emercoin);
        mEtAddress = view.findViewById(R.id.et_edit_address_address_book_emercoin);
    }
}
