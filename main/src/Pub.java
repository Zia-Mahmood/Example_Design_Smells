--- REFACTOR ---
package nerdschool.bar.refactored;

import java.util.HashMap;
import java.util.Map;

public class Pub {
    private final Map<String, Drink> drinks = new HashMap<>();
    private final Map<String, Ingredient> ingredients = new HashMap<>();

    public Pub() {
        // Initialize drinks
        drinks.put("hansa", new Drink("hansa", 74));
        drinks.put("grans", new Drink("grans", 103));
        drinks.put("strongbow", new Drink("strongbow", 110));

        // Initialize ingredients
        ingredients.put("rum", new Ingredient("rum", 65));
        ingredients.put("grenadine", new Ingredient("grenadine", 10));
        ingredients.put("lime juice", new Ingredient("lime juice", 10));
        ingredients.put("green stuff", new Ingredient("green stuff", 10));
        ingredients.put("tonic water", new Ingredient("tonic water", 20));
        ingredients.put("gin", new Ingredient("gin", 85));
    }

    public int computeCost(String drink, boolean student, int amount) {
        DrinkInfo drinkInfo = getDrinkInfo(drink);
        int cost = drinkInfo.getCost() * amount;

        if (student) {
            cost -= cost / 10;
        }

        return cost;
    }

    public int getIngredientPrice(String ingredient) {
        return ingredients.getOrDefault(ingredient, new Ingredient(ingredient, 0)).getPrice();
    }

    private DrinkInfo getDrinkInfo(String drink) {
        Drink drinkInfo = drinks.get(drink);
        if (drinkInfo == null) {
            throw new RuntimeException("No such drink exists");
        }

        if (amount > 2 && (drink.equals("gt") || drink.equals("bacardi_special"))) {
            throw new RuntimeException("Too many drinks, max 2.");
        }

        return drinkInfo;
    }

    private static class DrinkInfo {
        private final String name;
        private final int cost;

        public DrinkInfo(String name, int cost) {
            this.name = name;
            this.cost = cost;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return cost;
        }
    }

    private static class Ingredient {
        private final String name;
        private final int price;

        public Ingredient(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }
    }
}