package nerdschool.bar.refactored;

import java.util.HashMap;
import java.util.Map;

public class Pub {
    private final Map<String, Drink> drinks = new HashMap<>();
    private final Map<String, Ingredient> ingredients = new HashMap<>();
    private final DiscountCalculator discountCalculator = new DiscountCalculator();

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
        Drink drinkInfo = getDrinkInfo(drink);
        int cost = calculateDrinkCost(amount, drinkInfo.getCost());

        if (student) {
            cost = discountCalculator.applyDiscount(cost, 10);
        }

        return cost;
    }

    private int calculateDrinkCost(int amount, int cost) {
        return cost * amount;
    }

    public int getIngredientPrice(String ingredient) {
        return ingredients.getOrDefault(ingredient, new Ingredient(ingredient, 0)).getPrice();
    }

    private Drink getDrinkInfo(String drink) {
        Drink drinkInfo = drinks.get(drink);
        if (drinkInfo == null) {
            throw new RuntimeException("No such drink exists");
        }
        return drinkInfo;
    }

    private static class Drink {
        private final String name;
        private final int cost;

        private Drink(String name, int cost) {
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

        private Ingredient(String name, int price) {
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

    private static class DiscountCalculator {

        private double discountPercentage;

        public DiscountCalculator() {
            this.discountPercentage = 0.1;
        }

        public int applyDiscount(int cost, double discountPercentage) {
            return (int) (cost - (cost * discountPercentage));
        }
    }
}