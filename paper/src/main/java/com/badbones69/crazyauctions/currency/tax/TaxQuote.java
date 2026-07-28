package com.badbones69.crazyauctions.currency.tax;

/**
 * The monetary breakdown of an auction transaction after taxes are applied.
 *
 * @param price the listed price before tax
 * @param buyerTax the tax charged in addition to the listed price
 * @param sellerTax the tax deducted from the seller's proceeds
 * @param buyerTotal the total withdrawn from the buyer
 * @param sellerProceeds the amount deposited to the seller
 * @param totalTax the amount deposited to the configured tax account
 */
public record TaxQuote(
        long price,
        long buyerTax,
        long sellerTax,
        long buyerTotal,
        long sellerProceeds,
        long totalTax
) {
}
