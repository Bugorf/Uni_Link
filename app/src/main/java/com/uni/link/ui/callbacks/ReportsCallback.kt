package com.uni.link.ui.callbacks

import com.uni.link.data.models.Report

interface ReportsCallback {
    fun onReportClicked(report: Report)
}