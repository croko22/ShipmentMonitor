package com.kevinchambi.shipmentmonitor.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kevinchambi.shipmentmonitor.R
import com.kevinchambi.shipmentmonitor.data.model.Notification
import com.kevinchambi.shipmentmonitor.databinding.ItemNotificationBinding

class NotificationAdapter(
    val notifications: MutableList<Notification>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        with(holder.binding) {
            tvInvoice.text = "Compra: ${notification.invoice_number}"
            tvStatus.text = notification.status
            tvDate.text = notification.created_at

            when (notification.status) {
                "En Origen" -> tvStatus.setTextColor(
                    holder.itemView.context.getColor(R.color.primary_blue)
                )
                "En Tránsito" -> tvStatus.setTextColor(
                    holder.itemView.context.getColor(R.color.primary_cyan)
                )
                "En Domicilio" -> tvStatus.setTextColor(
                    holder.itemView.context.getColor(R.color.primary_purple)
                )
                "Entregado" -> tvStatus.setTextColor(
                    holder.itemView.context.getColor(R.color.primary_blue)
                )
            }

            btnDelete.setOnClickListener {
                onDeleteClick(notification.id)
            }
        }
    }

    override fun getItemCount() = notifications.size
}
