--- REFACTOR ---
package nerdschool.bar;

import java.util.HashMap;
import java.util.Map;

interface DrinkPricing {
  int computePrice(String drink, boolean student, int amount);
}

public class Pub implements DrinkPricing {

  private static final Map<String, Integer> DRINK_PRICES = new HashMap<>();

  static {
    DRINK_PRICES.put("hansa", 74);
    DRINK_PRICES.put("grans", 103);
    DRINK_PRICES.put("strongbow", 110);
  }

  @Override
  public int computePrice(String drink, boolean student, int amount) {

    if (amount > 2 && (drink.equals("gt") || drink.equals("bacardi_special"))) {
      throw new RuntimeException("Too many drinks, max 2.");
    }

    int price = DRINK_PRICES.getOrDefault(drink, -1);

    if (price == -1) {
      throw new RuntimeException("No such drink exists");
    }

    if (student && (drink.equals("hansa") || drink.equals("grans") || drink.equals("strongbow"))) {
      price = price - price / 10;
    }

    return price * amount;
  }
}