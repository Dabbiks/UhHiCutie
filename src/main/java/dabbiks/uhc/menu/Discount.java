package dabbiks.uhc.menu;

import java.util.HashMap;
import java.util.Map;

public class Discount {

    public static Map<DiscountType, Double> getDiscounts() {
        Map<DiscountType, Double> discounts = new HashMap<>();
        discounts.put(DiscountType.CHAMPION, 0.3);
        return discounts;
    }

}
