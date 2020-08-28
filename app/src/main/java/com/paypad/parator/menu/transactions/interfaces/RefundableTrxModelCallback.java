package com.paypad.parator.menu.transactions.interfaces;

import com.paypad.parator.menu.transactions.model.RefundableTrxModel;

import java.util.List;

public interface RefundableTrxModelCallback {
    void OnReturnTrxList(List<RefundableTrxModel> refundableTrxModels);
}
