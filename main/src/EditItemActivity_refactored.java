--- REFACTORED CODE ---
package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class EditItemActivity extends AppCompatActivity implements IObserver {

    private Item item;
    private ItemController itemController;

    private ContactList contactList;
    private ContactListController contactListController;

    private Bitmap image;
    private int REQUEST_CODE = 1;
    private ImageView photo;

    private EditText title;
    private EditText maker;
    private EditText description;
    private EditText length;
    private EditText width;
    private EditText height;
    private Spinner borrowerSpinner;
    private TextView  borrowerTv;
    private Switch status;
    private TextView statusText;

    private String titleStr;
    private String makerStr;
    private String descriptionStr;
    private String lengthStr;
    private String widthStr;
    private String heightStr;

    private boolean onUpdate;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        initialize();

        onUpdate = false;
    }

    private void initialize() {
        pos = getIntent().getIntExtra("position", 0);

        itemController = new ItemController(item);
        contactListController = new ContactListController(contactList);

        itemController.addObserver(this);
        contactListController.addObserver(this);

        initializeViews();
    }

    private void initializeViews() {
        item = itemController.getItem();
        image = itemController.getImage();

        title = findViewById(R.id.title);
        title.setText(item.getTitle());
        maker = findViewById(R.id.maker);
        maker.setText(item.getMaker());
        description = findViewById(R.id.description);
        description.setText(item.getDescription());

        length = findViewById(R.id.length);
        length.setText(itemController.getLength());
        width = findViewById(R.id.width);
        width.setText(itemController.getWidth());
        height = findViewById(R.id.height);
        height.setText(itemController.getHeight());

        borrowerSpinner = findViewById(R.id.borrower_spinner);
        borrowerTv = findViewById(R.id.borrower_tv);
        photo = findViewById(R.id.image_view);
        status = findViewById(R.id.available_switch);

        String statusStr = itemController.getStatus();
        if (statusStr.equals("Borrowed")) {
            status.setChecked(false);
        } else {
            borrowerTv.setVisibility(View.GONE);
            borrowerSpinner.setVisibility(View.GONE);
        }

        if (image != null) {
            photo.setImageBitmap(image);
        } else {
            photo.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                contactListController.getAllUsernames());
        borrowerSpinner.setAdapter(adapter);

        Contact contact = itemController.getBorrower();
        if (contact != null){
            int contactPos = contactListController.getIndex(contact);
            borrowerSpinner.setSelection(contactPos);
        }
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void deletePhoto(View view) {
        image = null;
        photo.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent intent){
        if (request_code == REQUEST_CODE && result_code == RESULT_OK){
            Bundle extras = intent.getExtras();
            image = (Bitmap) extras.get("data");
            photo.setImageBitmap(image);
        }
    }

    public void deleteItem(View view) {

        // Delete item
        ItemController itemController = new ItemController(item);
        boolean success = itemController.deleteItem(context);
        if (!success) {
            return;
        }

        // End EditItemActivity
        itemController.removeObserver(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void saveItem(View view) {

        titleStr = title.getText().toString();
        makerStr = maker.getText().toString();
        descriptionStr = description.getText().toString();
        lengthStr = length.getText().toString();
        widthStr = width.getText().toString();
        heightStr = height.getText().toString();

        Contact contact = null;
        if (!status.isChecked()) {
            String borrowerStr = borrowerSpinner.getSelectedItem().toString();
            contact = contactListController.getContactByUsername(borrowerStr);
        }

        if(!validateInput()) return;

        String id = itemController.getId(); // Reuse the item id
        Item updatedItem = new Item(titleStr, makerStr, descriptionStr, image, id);
        ItemController updatedItemController = new ItemController(updatedItem);
        updatedItemController.setDimensions(lengthStr, widthStr, heightStr);

        boolean checked = status.isChecked();
        if (!checked) {
            updatedItemController.setStatus("Borrowed");
            updatedItemController.setBorrower(contact);
        }

        // Edit item
        boolean success = itemController.editItem(updatedItem, context);
        if (!success) {
            return;
        }

        // End EditItemActivity
        itemController.removeObserver(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean validateInput() {
        if (titleStr.equals("")) {
            title.setError("Empty field!");
            return false;
        }

        if (makerStr.equals("")) {
            maker.setError("Empty field!");
            return false;
        }

        if (descriptionStr.equals("")) {
            description.setError("Empty field!");
            return false;
        }

        if (lengthStr.equals("")) {
            length.setError("Empty field!");
            return false;
        }

        if (widthStr.equals("")) {
            width.setError("Empty field!");
            return false;
        }

        if (heightStr.equals("")) {
            height.setError("Empty field!");
            return false;
        }

        return true;
    }

    /**
     * Checked == "Available"
     * Unchecked == "Borrowed"
     */
    public void toggleSwitch(View view){
        if (status.isChecked()) {
            // Means was previously borrowed, switch was toggled to available
            borrowerSpinner.setVisibility(View.GONE);
            borrowerTv.setVisibility(View.GONE);
            itemController.setBorrower(null);
            itemController.setStatus("Available");

        } else {
            // Means not borrowed
            if (contactList.getSize()==0){
                // No contacts, need to add contacts to be able to add a borrower
                status.setChecked(true); // Set switch to available

            } else {
                // Means was previously available
                borrowerSpinner.setVisibility(View.VISIBLE);
                borrowerTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void update() {
        if (onUpdate){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                    contactListController.getAllUsernames());
            borrowerSpinner.setAdapter(adapter);
        }
    }
}