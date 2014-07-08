package com.android.callassistant.customer;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.callassistant.R;
import com.android.callassistant.info.ContactInfo;
import com.android.callassistant.info.RecordInfo;
import com.android.callassistant.manager.RecordFileManager;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.util.Log;

import java.util.ArrayList;

public class CustomerDetailFragment extends Fragment implements OnClickListener, TextWatcher {

    private int mContactId;
    private ContactInfo mContact;
    private ArrayList<RecordInfo> mRecordList;
    private TextView mPhoneNumberView;
    private EditText mCustomerNameView;
    private CallLogListView mCallLogListView;
    private View mEditSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_detail_layout, null);
        mPhoneNumberView = (TextView) view.findViewById(R.id.customer_number);
        mCustomerNameView = (EditText) view.findViewById(R.id.customer_name);
        mCustomerNameView.addTextChangedListener(this);
        mCallLogListView = (CallLogListView) view.findViewById(R.id.call_log_listview);
        View dialNumber = view.findViewById(R.id.dial_number);
        dialNumber.setOnClickListener(this);
        mEditSave = view.findViewById(R.id.edit_save);
        mEditSave.setOnClickListener(this);
        return view;
    }

    public void setContactId(int id) {
        mContactId = id;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Log.TAG, "CustomerDetailFragment onActivityCreated mBaseId = " + mContactId);
        mRecordList = new ArrayList<RecordInfo>();
        updateUI();
        getActivity().getContentResolver().registerContentObserver(DBConstant.RECORD_URI, true, mRecordObserver);
    }

    private void updateUI() {
        mRecordList = RecordFileManager.getInstance(getActivity()).getRecordsFromDB(mRecordList, mContactId);
        mContact = RecordFileManager.getInstance(getActivity()).getSingleContact(mContactId);
        mPhoneNumberView.setText(mContact.contactNumber);
        mCustomerNameView.setText(mContact.contactName);
        int len = 0;
        if (!TextUtils.isEmpty(mContact.contactName)) {
            len = mContact.contactName.length();
        }
        mCustomerNameView.setEnabled(!mContact.contactModifyName);
        mCustomerNameView.setSelection(len);
        mCallLogListView.setCallLogList(mRecordList);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCallLogListView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallLogListView.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(mRecordObserver);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_save) {
            String newName = mCustomerNameView.getText().toString();
            Log.d(Log.TAG, "newName = " + newName);
            if (!TextUtils.isEmpty(newName)) {
                ContentValues values = new ContentValues();
                values.put(DBConstant.CONTACT_NAME, newName);
                Uri uri = ContentUris.withAppendedId(DBConstant.CONTACT_URI, mContactId);
                int ret = getActivity().getContentResolver().update(uri, values, null, null);
                String message = null;
                if (ret > 0) {
                    message = getActivity().getResources().getString(R.string.save_success);
                } else {
                    message = getActivity().getResources().getString(R.string.save_failure);
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                Log.getLog(getActivity()).recordOperation("Save record name : " + newName);
            }
        } else if (v.getId() == R.id.dial_number) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mContact.contactNumber));
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
    }

    @Override
    public void afterTextChanged(Editable s) {
        String newName = mCustomerNameView.getText().toString();
        if (newName != null && newName.equals(mContact.contactName == null ? "" : mContact.contactName)) {
            mEditSave.setEnabled(false);
        } else {
            mEditSave.setEnabled(true);
        }
    }
    
    private RecordObserver mRecordObserver = new RecordObserver();
    private class RecordObserver extends ContentObserver {
        public RecordObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(Log.TAG, "onChange selfChange = " + selfChange + " , uri = " + uri);
            updateUI();
        }
        
    }
}
