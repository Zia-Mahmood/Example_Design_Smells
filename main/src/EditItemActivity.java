--- REFACTOR CODE ---
package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sharingapp.model.Item;
import com.example.sharingapp.model.ItemList;
import com.example.sharingapp.model.ItemService;
import com.example.sharingapp.model.UserService;
import com.example.sharingapp.utils.Observer;

/**
 * Editing a pre-existing item consists of deleting the old item and adding a new item with the old
 * item's id.
 */
public class EditItemActivity extends AppCompatActivity implements Observer {

    private ItemList itemList;
    private UserService userService;
    private ItemService itemService;

    private Item item;
    protected Context context;

    private ImageView photo;
    private int REQUEST_CODE = 1;

    private EditText title;
    private EditText maker;
    private EditText description;
    private EditText length;
    private EditText width;
    private EditText height;
    private Spinner borrowerSpinner;
    private TextView borrowerTv;
    private Switch availableSwitch;

    private String titleStr;
    private String makerStr;
    private String descriptionStr;
    private String lengthStr;
    private String widthStr;
    private String heightStr;

    private ArrayAdapter<String> adapter;
    private boolean on_create_update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        title = findViewById(R.id.title);
        maker = findViewById(R.id.maker);
        description = findViewById(R.id.description);
        length = findViewById(R.id.length);
        width = findViewById(R.id.width);
        height = findViewById(R.id.height);
        borrowerSpinner = findViewById(R.id.borrower_spinner);
        borrowerTv = findViewById(R.id.borrower_tv);
        photo = findViewById(R.id.image_view);
        availableSwitch = findViewById(R.id.available_switch);

        Intent intent = getIntent(); 

        int pos = intent.getIntExtra("position", 0);

        context = getApplicationContext();

        userService = UserService.getInstance(context);
        userService.addObserver(this);
        userService.loadContacts(context);

        itemService = ItemService.getInstance(context);
        itemService.addObserver(this);
        itemService.loadItems(context);

        on_create_update = true;

        item = itemService.getItem(pos);
        update();

        on_create_update = false;
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
        boolean success = itemService.deleteItem(item, context);
        if (!success) {
            return;
        }

        // End EditItemActivity
        itemService.removeObserver(this);
        userService.removeObserver(this);

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
        if (!availableSwitch.isChecked()) {
            String borrowerStr = borrowerSpinner.getSelectedItem().toString();
            contact = userService.getContactByUsername(borrowerStr);
        }

        if(!validateInput()) return;

        String id = item.getId(); 
        Item updatedItem = new Item(titleStr, makerStr, descriptionStr, image, id);
        updatedItem.setDimensions(lengthStr, widthStr, heightStr);

        boolean checked = availableSwitch.isChecked();
        if (!checked) {
            updatedItem.setStatus("Borrowed");
            updatedItem.setBorrower(contact);
        }

        // Edit item
        boolean success = itemService.editItem(item, updatedItem, context);
        if (!success) {
            return;
        }

        // End EditItemActivity
        itemService.removeObserver(this);
        userService.removeObserver(this);

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
        if (availableSwitch.isChecked()) {
            // Means was previously borrowed, switch was toggled to available
            borrowerSpinner.setVisibility(View.GONE);
            borrowerTv.setVisibility(View.GONE);
            itemService.setBorrower(null, item, context);

        } else {
            // Means not borrowed
            if (userService.getSize()==0){
                // No contacts, need to add contacts to be able to add a borrower
                invisible.setEnabled(false);
                invisible.setVisibility(View.VISIBLE);
                invisible.requestFocus();
                invisible.setError("No contacts available! Must add borrower to contacts.");
                availableSwitch.setChecked(true); // Set switch to available

            } else {
                // Means was previously available
                borrowerSpinner.setVisibility(View.VISIBLE);
                borrowerTv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Only need to update the view from the onCreate method
     */
    public void update() {
        if (on_create_update){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                    userService.getAllUsernames());
            borrowerSpinner.setAdapter(adapter);

            item = itemService.getItem(pos);
            itemService = new ItemService(item);

            Contact contact = itemService.getBorrower();
            if (contact != null){
                int contactPos = userService.getIndex(contact);
                borrowerSpinner.setSelection(contactPos);
            }

            title.setText(itemService.getTitle());
            maker.setText(itemService.getMaker());
            description.setText(itemService.getDescription());

            length.setText(itemService.getLength());
            width.setText(itemService.getWidth());
            height.setText(itemService.getHeight());

            String statusStr = itemService.getStatus();
            if (statusStr.equals("Borrowed")) {
                availableSwitch.setChecked(false);
            } else {
                borrowerTv.setVisibility(View.GONE);
                borrowerSpinner.setVisibility(View.GONE);
            }

            image = itemService.getImage();
            if (image != null) {
                photo.setImageBitmap(image);
            } else {
                photo.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}