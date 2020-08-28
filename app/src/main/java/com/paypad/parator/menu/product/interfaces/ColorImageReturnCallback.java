package com.paypad.parator.menu.product.interfaces;

import com.paypad.parator.model.pojo.PhotoSelectUtil;

public interface ColorImageReturnCallback {
    void OnColorReturn(int colorId);
    void OnImageReturn(byte[] itemPictureByteArray, PhotoSelectUtil photoSelectUtil);
}