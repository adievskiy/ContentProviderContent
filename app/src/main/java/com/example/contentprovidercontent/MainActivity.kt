package com.example.contentprovidercontent

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.contentprovidercontent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var customAdapter: RecyclerAdapter
    private val contactModelList = mutableListOf<ContactModel>()

    private val requestContactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadContacts()
                Toast.makeText(this, "Доступ к контактам получен", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Доступа к контактам нет", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            loadContacts()
        }
    }

    private fun loadContacts() {
        val contentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)
                contactModelList.add(ContactModel(name, phone))
            }
        }
        customAdapter = RecyclerAdapter(contactModelList, this)
        binding.contactsRV.adapter = customAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.saveBTN.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionWriteContact.launch(Manifest.permission.WRITE_CONTACTS)
            } else {
                addContact()
                loadContacts()
                customAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addContact() {
        val newName = binding.nameET.text.toString()
        val newPhone = binding.phoneET.text.toString()
        val listCPO = ArrayList<ContentProviderOperation>()

        listCPO.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        listCPO.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, newName)
                .build()
        )

        listCPO.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, newPhone)
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build()
        )
        Toast.makeText(this, "$newName добавлен в контакты", Toast.LENGTH_LONG).show()
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, listCPO)
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
    }

    private val permissionWriteContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Доступ к записи получен", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Доступ к записи не получен", Toast.LENGTH_LONG).show()
        }
    }
}