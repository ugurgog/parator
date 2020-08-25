package com.paypad.vuk507.menu.transactions.interfaces;

import com.paypad.vuk507.menu.transactions.model.RefundableTrxModel;

import java.util.List;

public interface RefundableTrxModelCallback {
    void OnReturnTrxList(List<RefundableTrxModel> refundableTrxModels);
}
