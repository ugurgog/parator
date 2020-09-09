package com.paypad.parator.utils;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class NfcUtil {

    private static boolean hasNfc(Context context) {
        boolean bRet = false;
        if (context == null) {
            return false;
        }
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        if (manager == null) {
            return false;
        }
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            bRet = true;
        }
        return bRet;
    }

}
