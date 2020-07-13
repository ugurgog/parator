package com.paypad.vuk507.menu.product.interfaces;

import android.net.Uri;

import com.paypad.vuk507.model.pojo.PhotoSelectUtil;

public interface ColorImageReturnCallback {
    void OnColorReturn(int colorId);
    void OnImageReturn(byte[] itemPictureByteArray, PhotoSelectUtil photoSelectUtil);
}