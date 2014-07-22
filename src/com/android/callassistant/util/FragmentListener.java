package com.android.callassistant.util;

public interface FragmentListener {
    public void finishActionModeIfNeed();
    public boolean onBackPressed();
    public void onFragmentSelected(int pos);
    public boolean isSearching();
}
