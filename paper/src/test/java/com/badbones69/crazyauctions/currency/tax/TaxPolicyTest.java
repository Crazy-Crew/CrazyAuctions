package com.badbones69.crazyauctions.currency.tax;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxPolicyTest {

    @Test
    void appliesBasicTaxToSeller() {
        final TaxPolicy policy = new TaxPolicy(
                TaxPolicy.Type.BASIC,
                TaxPolicy.Target.SHOP,
                0.05,
                Map.of()
        );

        assertEquals(new TaxQuote(999, 0, 49, 999, 950, 49), policy.quote(999, 0, 0, false, false));
    }

    @Test
    void appliesProgressiveRatesIndependentlyToBothSides() {
        final TaxPolicy policy = new TaxPolicy(
                TaxPolicy.Type.PROGRESSIVE,
                TaxPolicy.Target.BOTH,
                0,
                Map.of(
                        50_000D, 0.05,
                        250_000D, 0.10,
                        Double.POSITIVE_INFINITY, 0.60
                )
        );

        assertEquals(
                new TaxQuote(1_000, 50, 100, 1_050, 900, 150),
                policy.quote(1_000, 50_000, 50_001, false, false)
        );
    }

    @Test
    void skipsTaxForEachExemptParty() {
        final TaxPolicy policy = new TaxPolicy(
                TaxPolicy.Type.BASIC,
                TaxPolicy.Target.BOTH,
                0.25,
                Map.of()
        );

        assertEquals(
                new TaxQuote(1_000, 0, 250, 1_000, 750, 250),
                policy.quote(1_000, 0, 0, true, false)
        );
        assertEquals(
                new TaxQuote(1_000, 250, 0, 1_250, 1_000, 250),
                policy.quote(1_000, 0, 0, false, true)
        );
    }

    @Test
    void ignoresRatesOutsideTheSupportedRange() {
        final TaxPolicy policy = new TaxPolicy(
                TaxPolicy.Type.BASIC,
                TaxPolicy.Target.BOTH,
                1.01,
                Map.of()
        );

        assertEquals(new TaxQuote(1_000, 0, 0, 1_000, 1_000, 0), policy.quote(1_000, 0, 0, false, false));
    }
}
