--- AddContactActivity ---

    package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Add a new contact
 */
public class AddContactActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private ContactListControllerInterface contactListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        contactListController = ContactListControllerFactory.createContactListController();
    }

    public void saveContact(View view) {

        String username_str = username.getText().toString();
        String email_str = email.getText().toString();

        if(!validateInput()) return;

        Contact contact = new Contact(username_str, email_str, null);

        // Add contact
        boolean success = contactListController.addContact(contact, this);
        if (!success) {
            Toast.makeText(this, "Failed to add contact", Toast.LENGTH_LONG).show();
            return;
        }

        // End AddContactActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean validateInput() {
        if (username.getText().toString().equals("")) {
            username.setError("Empty field!");
            return false;
        }

        if (!email.getText().toString().contains("@")) {
            email.setError("Must be an email address!");
            return false;
        }

        return true;
    }
}

--- ContactListControllerFactory ---

package com.example.sharingapp;

/**
 * Factory class for creating ContactListController objects.
 */
public class ContactListControllerFactory {

    public static ContactListControllerInterface createContactListController() {
        return new ContactListController();
    }
}

--- ContactListController ---

package com.example.sharingapp;

import android.content.Context;

/**
 * Controller for the contact list
 */
public class ContactListController implements ContactListControllerInterface {

    private ContactList contactList;

    public ContactListController() {
        contactList = new ContactList();
    }

    @Override
    public boolean addContact(Contact contact, Context context) {
        if (contactList.isUsernameAvailable(contact.getUsername())) {
            contactList.addContact(contact);
            return true;
        } else {
            Toast.makeText(context, "Username already taken!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return contactList.isUsernameAvailable(username);
    }

    @Override
    public void loadContacts(Context context) {
        contactList.loadContacts(context);
    }
}