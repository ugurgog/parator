package com.paypad.vuk507.menu.transactions.model;

import com.paypad.vuk507.model.Refund;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RefundedTrxModel extends RealmObject {

    private String refundGroupId;
    private RealmList<Refund> refunds;

    public String getRefundGroupId() {
        return refundGroupId;
    }

    public void setRefundGroupId(String refundGroupId) {
        this.refundGroupId = refundGroupId;
    }

    public RealmList<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(RealmList<Refund> refunds) {
        this.refunds = refunds;
    }
}
