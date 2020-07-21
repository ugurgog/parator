package com.paypad.vuk507.menu.reports.interfaces;

import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.model.pojo.ReportDate;

public interface ReturnReportSelectionCallback {
    void OnReturnReportSelection(ReportDate reportDate, ReportSelectionEnum reportSelectionType);
}
