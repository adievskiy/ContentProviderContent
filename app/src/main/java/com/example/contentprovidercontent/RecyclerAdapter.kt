package com.example.contentprovidercontent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

class RecyclerAdapter(
    private val contactModelList: List<ContactModel>,
    private val context: Context,
) :
    RecyclerView.Adapter<RecyclerAdapter.ContactsViewHolder>() {

    inner class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.nameTV)
        val phoneTV: TextView = view.findViewById(R.id.phoneTV)
        val callButtonIV: ImageView = view.findViewById(R.id.callButtonIV)
        val messageButtonIV: ImageView = view.findViewById(R.id.messageButtonIV)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerAdapter.ContactsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ContactsViewHolder, position: Int) {
        val contact = contactModelList[position]
        holder.nameTV.text = contact.name
        holder.phoneTV.text = contact.phone
        holder.callButtonIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${contact.phone}")
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            } else {
                context.startActivity(intent)
            }
        }
        holder.messageButtonIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${contact.phone}")
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactModelList.size
    }
}