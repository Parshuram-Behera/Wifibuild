package com.example.wifibuild.permissionManager;

public interface MyPermissionListener {

    void onPermissionDenied(String permission);

    void onPermissionGranted(String permission);

    void onPermissionPermanentlyDenied(String permission);
}
