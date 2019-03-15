package com.example.contactsviewer

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ListFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AlertDialog
import android.view.View

class ContactListFragment : ListFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private val PERMISSION_REQUEST_CODE = 100
    private val PERMISSIONS = arrayOf(Manifest.permission.READ_CONTACTS)
    private val CONTACTS_REQUEST_CODE = 101

    private val PROJECTION = arrayOf(ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
    private val FROM_COLUMNS = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
    private val TO_VIEW_IDS = intArrayOf(R.id.tvContactName)

    private lateinit var cursorAdapter: SimpleCursorAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            cursorAdapter = SimpleCursorAdapter(it,
                R.layout.item_contact,
                null,
                FROM_COLUMNS,
                TO_VIEW_IDS,
                0)
            listAdapter = cursorAdapter
            if (ContextCompat.checkSelfPermission(it, PERMISSIONS[0])
                != PackageManager.PERMISSION_GRANTED) handlePermissionRequest() else readContacts() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) readContacts() else explainPermissions()
    }

    private fun explainPermissions() {
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.grant_permission) { _, _ -> handlePermissionRequest() }
                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                setTitle(R.string.permission_dialog_title)
                setMessage(R.string.permission_rationale)
                builder.create() }
        } ?: throw IllegalStateException("Activity cannot be null")
        alertDialog.show()
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null) } ?: throw IllegalStateException("Activity should not be null")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        cursorAdapter.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter.swapCursor(null)
    }

    private fun readContacts() {
        loaderManager.initLoader(
            CONTACTS_REQUEST_CODE,
            null,
            this)
    }

    private fun handlePermissionRequest() {
        requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
    }


}