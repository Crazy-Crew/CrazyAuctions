package com.badbones69.crazyauctions.currency.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Immutable tax rules that can calculate a transaction without depending on Bukkit.
 */
public final class TaxPolicy {

    private final NavigableMap<Double, Double> progressiveRates;
    private final double basicRate;
    private final Type type;
    private final Target target;

    public TaxPolicy(final Type type, final Target target, final double basicRate, final Map<Double, Double> progressiveRates) {
        this.type = type;
        this.target = target;
        this.basicRate = normalizeRate(basicRate);
        this.progressiveRates = new TreeMap<>();

        progressiveRates.forEach((balance, rate) -> {
            if (balance != null && rate != null && !Double.isNaN(balance)) {
                this.progressiveRates.put(balance, normalizeRate(rate));
            }
        });
    }

    public TaxQuote quote(
            final long price,
            final double buyerBalance,
            final double sellerBalance,
            final boolean buyerExempt,
            final boolean sellerExempt
    ) {
        final long normalizedPrice = Math.max(price, 0);

        final double buyerRate = this.target.taxesBuyer() && !buyerExempt ? getRate(buyerBalance) : 0;
        final double sellerRate = this.target.taxesSeller() && !sellerExempt ? getRate(sellerBalance) : 0;

        final long buyerTax = calculateTax(normalizedPrice, buyerRate);
        final long sellerTax = calculateTax(normalizedPrice, sellerRate);

        return new TaxQuote(
                normalizedPrice,
                buyerTax,
                sellerTax,
                safeAdd(normalizedPrice, buyerTax),
                normalizedPrice - sellerTax,
                safeAdd(buyerTax, sellerTax)
        );
    }

    private double getRate(final double balance) {
        if (this.type == Type.BASIC || this.progressiveRates.isEmpty()) {
            return this.type == Type.BASIC ? this.basicRate : 0;
        }

        final Map.Entry<Double, Double> bracket = this.progressiveRates.ceilingEntry(balance);

        return bracket != null ? bracket.getValue() : this.progressiveRates.lastEntry().getValue();
    }

    private static long calculateTax(final long price, final double rate) {
        if (price == 0 || rate == 0) return 0;

        return BigDecimal.valueOf(price)
                .multiply(BigDecimal.valueOf(rate))
                .setScale(0, RoundingMode.DOWN)
                .longValue();
    }

    private static double normalizeRate(final double rate) {
        if (!Double.isFinite(rate) || rate < 0 || rate > 1) return 0;

        return rate;
    }

    private static long safeAdd(final long first, final long second) {
        try {
            return Math.addExact(first, second);
        } catch (final ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }

    public enum Type {
        BASIC,
        PROGRESSIVE;

        public static Type parse(final String value) {
            try {
                return valueOf(value.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException exception) {
                return BASIC;
            }
        }
    }

    public enum Target {
        PLAYER(true, false),
        SHOP(false, true),
        BOTH(true, true);

        private final boolean buyer;
        private final boolean seller;

        Target(final boolean buyer, final boolean seller) {
            this.buyer = buyer;
            this.seller = seller;
        }

        public static Target parse(final String value) {
            return switch (value.toLowerCase(Locale.ROOT)) {
                case "player", "buyer" -> PLAYER;
                case "both" -> BOTH;
                case "shop", "seller", "payee" -> SHOP;
                default -> SHOP;
            };
        }

        public boolean taxesBuyer() {
            return this.buyer;
        }

        public boolean taxesSeller() {
            return this.seller;
        }
    }
}
