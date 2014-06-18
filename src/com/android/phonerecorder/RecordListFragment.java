package com.android.phonerecorder;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.phonerecorder.service.RecordFileManager;

public class RecordListFragment extends ListFragment {

    private ArrayAdapter<String> mListAdapter;

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    
    private void updateUI() {
        String []files = RecordFileManager.getInstance(getActivity()).listRecordFiles();
        mListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, files);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setAdapter(mListAdapter);
        setListShown(true);
    }
}
