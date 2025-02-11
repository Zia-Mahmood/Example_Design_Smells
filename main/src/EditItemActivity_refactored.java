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

public class EditItemActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private ItemController itemController;
    private ContactListController contactListController;

    private ImageView photo;
    private Switch status;

    private String titleStr;
    private String makerStr;
    private String descriptionStr;
    private String lengthStr;
    private String widthStr;
    private String heightStr;

    private boolean onUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        initialize();
    }

    private void initialize() {
        pos = getIntent().getIntExtra("position", 0);

        itemController = new ItemController(getItem());
        contactListController = new ContactListController(getContactList());

        itemController.addObserver(this);
        contactListController.addObserver(this);

        initializeViews();
    }

    private void initializeViews() {
        Item item = itemController.getItem();
        Bitmap image = itemController.getImage();

        EditText title = findViewById(R.id.title);
        title.setText(item.getTitle());
        EditText maker = findViewById(R.id.maker);
        maker.setText(item.getMaker());
        EditText description = findViewById(R.id.description);
        description.setText(item.getDescription());

        EditText length = findViewById(R.id.length);
        length.setText(itemController.getLength());
        EditText width = findViewById(R.id.width);
        width.setText(itemController.getWidth());
        EditText height = findViewById(R.id.height);
        height.setText(itemController.getHeight());

        Spinner borrowerSpinner = findViewById(R.id.borrower_spinner);
        TextView borrowerTv = findViewById(R.id.borrower_tv);
        photo = findViewById(R.id.image_view);
        status = findViewById(R.id.available_switch);

        String statusStr = itemController.getStatus();
        if (statusStr.equals("Borrowed")) {
            status.setChecked(false);
            borrowerTv.setVisibility(View.VISIBLE);
            borrowerSpinner.setVisibility(View.VISIBLE);
        } else {
            borrowerTv.setVisibility(View.GONE);
            borrowerSpinner.setVisibility(View.GONE);
        }

        if (image != null) {
            photo.setImageBitmap(image);
        } else {
            photo.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                contactListController.getAllUsernames());
        borrowerSpinner.setAdapter(adapter);

        Contact contact = itemController.getBorrower();
        if (contact != null){
            int contactPos = contactListController.getIndex(contact);
            borrowerSpinner.setSelection(contactPos);
        }
    }

    private Item getItem() {
        // Get item from database or other data source
        return new Item();
    }

    private ContactList getContactList() {
        // Get contact list from database or other data source
        return new ContactList();
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void deletePhoto(View view) {
        itemController.setImage(null);
        photo.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent intent){
        if (request_code == REQUEST_CODE && result_code == RESULT_OK){
            Bundle extras = intent.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            photo.setImageBitmap(image);
        }
    }

    public void deleteItem(View view) {
        boolean success = itemController.deleteItem(this);
        if (!success) {
            return;
        }

        itemController.removeObserver(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void saveItem(View view) {
        titleStr = getTitle();
        makerStr = getMaker();
        descriptionStr = getDescription();
        lengthStr = getLength();
        widthStr = getWidth();
        heightStr = getHeight();

        Contact contact = null;
        if (!status.isChecked()) {
            String borrowerStr = getBorrower();
            contact = contactListController.getContactByUsername(borrowerStr);
        }

        if(!validateInput()) return;

        String id = itemController.getId(); // Reuse the item id
        Item updatedItem = new Item(titleStr, makerStr, descriptionStr, itemController.getImage(), id);
        ItemController updatedItemController = new ItemController(updatedItem);
        updatedItemController.setDimensions(lengthStr, widthStr, heightStr);

        boolean checked = status.isChecked();
        if (!checked) {
            updatedItemController.setStatus("Borrowed");
            updatedItemController.setBorrower(contact);
        }

        boolean success = itemController.editItem(updatedItem, this);
        if (!success) {
            return;
        }

        itemController.removeObserver(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String getTitle() {
        return ((EditText) findViewById(R.id.title)).getText().toString();
    }

    private String getMaker() {
        return ((EditText) findViewById(R.id.maker)).getText().toString();
    }

    private String getDescription() {
        return ((EditText) findViewById(R.id.description)).getText().toString();
    }

    private String getLength() {
        return ((EditText) findViewById(R.id.length)).getText().toString();
    }

    private String getWidth() {
        return ((EditText) findViewById(R.id.width)).getText().toString();
    }

    private String getHeight() {
        return ((EditText) findViewById(R.id.height)).getText().toString();
    }

    private String getBorrower() {
        return ((Spinner) findViewById(R.id.borrower_spinner)).getSelectedItem().toString();
    }

    public boolean validateInput() {
        if (titleStr.equals("")) {
            ((EditText) findViewById(R.id.title)).setError("Empty field!");
            return false;
        }

        if (makerStr.equals("")) {
            ((EditText) findViewById(R.id.maker)).setError("Empty field!");
            return false;
        }

        if (descriptionStr.equals("")) {
            ((EditText) findViewById(R.id.description)).setError("Empty field!");
            return false;
        }

        if (lengthStr.equals("")) {
            ((EditText) findViewById(R.id.length)).setError("Empty field!");
            return false;
        }

        if (widthStr.equals("")) {
            ((EditText) findViewById(R.id.width)).setError("Empty field!");
            return false;
        }

        if (heightStr.equals("")) {
            ((EditText) findViewById(R.id.height)).setError("Empty field!");
            return false;
        }

        return true;
    }

    public void toggleSwitch(View view){
        if (status.isChecked()) {
            ((Spinner) findViewById(R.id.borrower_spinner)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.borrower_tv)).setVisibility(View.GONE);
            itemController.setBorrower(null);
            itemController.setStatus("Available");
        } else {
            if (contactListController.getSize()==0){
                status.setChecked(true); 
            } else {
                ((Spinner) findViewById(R.id.borrower_spinner)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.borrower_tv)).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void update() {
        if (onUpdate){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(