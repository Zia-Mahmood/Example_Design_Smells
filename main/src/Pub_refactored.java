--- REFACTORED CODE ---
import java.util.HashMap;
import java.util.Map;

public class Pub {

    private static final PriceList PRICE_LIST = new PriceList();

    public int computeCost(Drink drink, boolean student, int amount) {
        if (amount > 2 && drink.isMaxTwo()) {
            throw new RuntimeException("Too many drinks, max 2.");
        }
        Price price = PRICE_LIST.getDrinkPrice(drink);
        return price.computeCost(student, amount);
    }

}

class PriceList {

    private static final Map<String, Price> DRINK_PRICES = new HashMap<>();
    private static final Map<String, Integer> STUDENT_DISCOUNTS = new HashMap<>();

    static {
        DRINK_PRICES.put("ONE_BEER", new Price(74));
        DRINK_PRICES.put("ONE_CIDER", new Price(103));
        DRINK_PRICES.put("A_PROPER_CIDER", new Price(110));

        STUDENT_DISCOUNTS.put("ONE_BEER", 10);
        STUDENT_DISCOUNTS.put("ONE_CIDER", 10);
        STUDENT_DISCOUNTS.put("A_PROPER_CIDER", 10);
    }

    public Price getDrinkPrice(Drink drink) {
        return DRINK_PRICES.get(drink.getName());
    }

    public int getStudentDiscount(Drink drink) {
        return STUDENT_DISCOUNTS.get(drink.getName());
    }

}

class Price {

    private int price;

    public Price(int price) {
        this.price = price;
    }

    public int computeCost(boolean student, int amount) {
        int price = this.price;
        if (student) {
            price -= price * STUDENT_DISCOUNTS.get(price) / 100;
        }
        return price * amount;
    }

}

class Drink {

    private String name;
    private boolean maxTwo;

    public Drink(String name, boolean maxTwo) {
        this.name = name;
        this.maxTwo = maxTwo;
    }

    public String getName() {
        return name;
    }

    public boolean isMaxTwo() {
        return maxTwo;
    }

}