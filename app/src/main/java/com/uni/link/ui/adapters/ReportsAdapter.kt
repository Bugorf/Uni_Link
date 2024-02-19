package com.uni.link.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.uni.link.R
import com.uni.link.data.models.Report
import com.uni.link.databinding.ItemReportBinding
import com.uni.link.ui.callbacks.ReportsCallback
import com.uni.link.utils.AppUtils
import com.uni.link.utils.TimeFormatter
import com.uni.link.utils.inflate
import com.uni.link.utils.setDrawable
import com.mikepenz.ionicons_typeface_library.Ionicons


class ReportsAdapter(private val callback: ReportsCallback): PagedListAdapter<Report,
        ReportsAdapter.ReportHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
        return ReportHolder(parent.inflate(R.layout.item_report), callback)
    }

    override fun onBindViewHolder(holder: ReportHolder, position: Int) {
        val report = getItem(position)
        holder.bind(report!!)
    }

    inner class ReportHolder(private val binding: ItemReportBinding, private val callback: ReportsCallback):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            binding.report = report
            binding.callback = callback
            binding.formatter = TimeFormatter()

            binding.time.setDrawable(AppUtils.getDrawable(binding.root.context, Ionicons.Icon.ion_clock, R.color.color_text_secondary, 12))
        }
    }
}