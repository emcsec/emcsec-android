package com.aspanta.emcsec.ui.fragment.dialogFragmentDeleteAddressBook;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.aspanta.emcsec.App;
import com.aspanta.emcsec.R;
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.ui.fragment.addressBookFragment.AddressBookEmercoinFragment;

import java.util.List;

public class DialogFragmentDeleteAddressBookEmercoin extends DialogFragment {

    private Button mBtnDel, mBtnCancel;
    static AddressBookEmercoinFragment mAddressBookEmercoinFragment;
    private static int sPosition;

    public static DialogFragmentDeleteAddressBookEmercoin newInstance
            (AddressBookEmercoinFragment addressBookEmercoinFragment, int position) {

        sPosition = position;
        DialogFragmentDeleteAddressBookEmercoin fragment = new DialogFragmentDeleteAddressBookEmercoin();
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
        View view = inflater.inflate(R.layout.dialog_fragment_delete_address_book_emercoin, container, false);
        init(view);

        setCancelable(false);

        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        mBtnDel.setOnClickListener(o -> {

            List<EmcAddressBook> addressPojoList = App.getDbInstance().emcAddressBookDao().getAll();
            addressPojoList.remove(sPosition);
            App.getDbInstance().emcAddressBookDao().deleteAll();
            App.getDbInstance().emcAddressBookDao().insertListEmcAdresses(addressPojoList);
            mAddressBookEmercoinFragment.setAddressesList(addressPojoList);

            dismiss();
        });

        mBtnCancel.setOnClickListener(o -> dismiss());
        return view;
    }

    private void init(View view) {
        mBtnDel = view.findViewById(R.id.btn_delete_address_book_emercoin);
        mBtnCancel = view.findViewById(R.id.btn_cancel_address_book_emercoin);
    }
}
