--- REFACTORING STARTS HERE ---
// Item model (platform-independent)
abstract class Item {

    private String title;
    private String maker;
    private String description;

    public Item(String title, String maker, String description) {
        this.title = title;
        this.maker = maker;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract Image getImage();
}

// Android platform-specific Item implementation
class AndroidItem implements Item {

    private Bitmap image;

    public AndroidItem(String title, String maker, String description, Bitmap image) {
        this.title = title;
        this.maker = maker;
        this.description = description;
        this.image = image;
    }



    @Override
    public Image getImage() {
        return image;
    }
}
// Image interface (platform-independent)
interface Image {
    // Methods to get and set the image
}

// Interface for Item validation
interface ItemValidator {
    boolean validateItem(Item item);
}

// Interface for Item creation
interface ItemCreator {
    boolean saveItem(Item item);
}

// Item list controller interface
class ItemListController {

    private final ItemList item_list;

    public ItemListController(ItemList item_list) {
        this.item_list = item_list;
    }

    public boolean addItem(Item item) {
        return item_list.add(item);
    }
}

// Item model (platform-independent)
abstract class Item {

    private String title;
    private String maker;
    private String description;

    public Item(String title, String maker, String description) {
        this.title = title;
        this.maker = maker;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract Image getImage();
}

// Android platform-specific Item implementation
class AndroidItem implements Item {

    private Bitmap image;

    public AndroidItem(String title, String maker, String description, Bitmap image) {
        this.title = title;
        this.maker = maker;
        this.description = description;
        this.image = image;
    }



    @Override
    public Image getImage() {
        return image;
    }
}

// Item validation module
class ItemValidator implements ItemValidator {

    public boolean validateItem(Item item) {
        if (item.getTitle().isEmpty()) {
            return false;
        }

        if (item.getMaker().isEmpty()) {
            return false;
        }

        if (item.getDescription().isEmpty()) {
            return false;
        }

        if (item instanceof AndroidItem) {
            AndroidItem androidItem = (AndroidItem) item;
            if (androidItem.getImage() == null) {
                return false;
            }
        }

        return true;
    }
}

// Item creation and saving module
class ItemCreator implements ItemCreator {

    private final ItemListController item_list_controller;

    public ItemCreator(ItemListController item_list_controller) {
        this.item_list_controller = item_list_controller;
    }

    public boolean saveItem(Item item) {

        // Add item
        boolean success = item_list_controller.addItem(item);
        return success;
    }
}

// AddItemActivity
public class AddItemActivity extends AppCompatActivity {

    private EditText title;
    private EditText maker;
    private EditText description;
    private ImageView photo;
    private Bitmap image;
    private int REQUEST_CODE = 1;
    private ItemCreator itemCreator;
    private ItemValidator itemValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        title = (EditText) findViewById(R.id.title);
        maker = (EditText) findViewById(R.id.maker);
        description = (EditText) findViewById(R.id.description);
        photo = (ImageView) findViewById(R.id.image_view);

        photo.setImageResource(android.R.drawable.ic_menu_gallery);

        ItemList item_list = new ItemList();
        ItemListController item_list_controller = new ItemListController(item_list);
        itemCreator = new ItemCreator(item_list_controller);
        itemValidator = new ItemValidator();
    }

    public void saveItem (View view) {
        String title_str = title.getText().toString();
        String maker_str = maker.getText().toString();
        String description_str = description.getText().toString();

        if(!itemValidator.validateItem(new AndroidItem(title_str, maker_str, description_str, image))) {
            return;
        }

        Item item = new AndroidItem(title_str, maker_str, description_str, image);
        itemCreator.saveItem(item);
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
}
--- REFACTORING ENDS HERE ---