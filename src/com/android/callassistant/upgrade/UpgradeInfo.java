package com.android.callassistant.upgrade;

public class UpgradeInfo {
    public String app_url;
    public String app_name;
    public int version_code;
    public long file_size;

    public String toString() {
        String str = "";
        str += "app_name     : " + app_url + "\n";
        str += "app_url      : " + app_name + "\n";
        str += "version_code : " + version_code + "\n";
        str += "file_size    : " + file_size + "\n";
        return str;
    }
}
