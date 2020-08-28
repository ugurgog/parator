package com.paypad.parator.menu.reports.interfaces;

import com.paypad.parator.enums.ReportSelectionEnum;
import com.paypad.parator.model.pojo.ReportDate;

public interface ReturnReportSelectionCallback {
    void OnReturnReportSelection(ReportDate reportDate, ReportSelectionEnum reportSelectionType);
}
