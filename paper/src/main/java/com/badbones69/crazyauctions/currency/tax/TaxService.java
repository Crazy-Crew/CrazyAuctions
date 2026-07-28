package com.badbones69.crazyauctions.currency.tax;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.fusion.core.api.enums.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class TaxService {

    private static final String TAX_PATH = "Settings.Tax";

    private final CrazyAuctions plugin;
    private final VaultSupport support;

    private volatile TaxPolicy policy;
    private volatile String taxAccount;
    private volatile boolean showTax;

    public TaxService(@NotNull final CrazyAuctions plugin, @NotNull final VaultSupport support) {
        this.plugin = plugin;
        this.support = support;

        reload();
    }

    public void reload() {
        final YamlConfiguration configuration = FileKeys.config.getConfiguration();

        if (!configuration.isConfigurationSection(TAX_PATH)) {
            final double legacyRate = configuration.getDouble("Settings.Percent-Tax", 0) / 100;

            this.policy = new TaxPolicy(TaxPolicy.Type.BASIC, TaxPolicy.Target.SHOP, legacyRate, Map.of());
            this.taxAccount = "";
            this.showTax = false;

            return;
        }

        final TaxPolicy.Type type = TaxPolicy.Type.parse(configuration.getString(TAX_PATH + ".Type", "basic"));
        final TaxPolicy.Target target = TaxPolicy.Target.parse(configuration.getString(TAX_PATH + ".Apply-To", "shop"));
        final double basicRate = configuration.getDouble(TAX_PATH + ".Basic.Rate", 0);

        final Map<Double, Double> brackets = new HashMap<>();
        final ConfigurationSection section = configuration.getConfigurationSection(TAX_PATH + ".Progressive.Brackets");

        if (section != null) {
            for (final String key : section.getKeys(false)) {
                try {
                    final double upperBalance = key.equals("-1") ? Double.POSITIVE_INFINITY : Double.parseDouble(key);

                    brackets.put(upperBalance, section.getDouble(key));
                } catch (final NumberFormatException exception) {
                    this.plugin.getFusion().log(Level.WARNING, "Ignoring invalid tax bracket: %s", key);
                }
            }
        }

        this.policy = new TaxPolicy(type, target, basicRate, brackets);
        this.taxAccount = configuration.getString(TAX_PATH + ".Account", "").trim();
        this.showTax = configuration.getBoolean(TAX_PATH + ".Show", false);
    }

    public @NotNull TaxQuote quote(final long price, @NotNull final OfflinePlayer buyer, @NotNull final OfflinePlayer seller) {
        return this.policy.quote(
                price,
                this.support.getMoney(buyer),
                this.support.getMoney(seller),
                isTaxExempt(buyer),
                isTaxExempt(seller)
        );
    }

    public boolean depositTaxes(@NotNull final TaxQuote quote) {
        final String account = this.taxAccount;

        if (quote.totalTax() == 0 || account.isBlank()) return true;

        final boolean success = this.support.addMoney(resolveTaxAccount(account), quote.totalTax());

        if (!success) {
            this.plugin.getFusion().log(Level.WARNING, "Failed to deposit %s in taxes to %s.", quote.totalTax(), account);
        }

        return success;
    }

    public boolean shouldShowTax() {
        return this.showTax;
    }

    public void addPlaceholders(@NotNull final Map<String, String> placeholders, @NotNull final TaxQuote quote) {
        final String legacyTax = String.valueOf(quote.sellerTax());
        final String legacyTaxedPrice = String.valueOf(quote.sellerProceeds());

        placeholders.put("%Tax%", legacyTax);
        placeholders.put("%tax%", legacyTax);
        placeholders.put("%Taxed_Price%", legacyTaxedPrice);
        placeholders.put("%taxed_price%", legacyTaxedPrice);

        putPlaceholder(placeholders, "Buyer_Tax", quote.buyerTax());
        putPlaceholder(placeholders, "Seller_Tax", quote.sellerTax());
        putPlaceholder(placeholders, "Total_Tax", quote.totalTax());
        putPlaceholder(placeholders, "Buyer_Total", quote.buyerTotal());
        putPlaceholder(placeholders, "Seller_Proceeds", quote.sellerProceeds());
    }

    private boolean isTaxExempt(final OfflinePlayer player) {
        return this.support.hasPermission(player, Permissions.tax.getNode())
                || this.support.hasPermission(player, Permissions.bypass.getNode());
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveTaxAccount(final String account) {
        try {
            return this.plugin.getServer().getOfflinePlayer(UUID.fromString(account));
        } catch (final IllegalArgumentException ignored) {
            return this.plugin.getServer().getOfflinePlayer(account);
        }
    }

    private static void putPlaceholder(final Map<String, String> placeholders, final String name, final long value) {
        final String replacement = String.valueOf(value);

        placeholders.put("%" + name + "%", replacement);
        placeholders.put("%" + name.toLowerCase(Locale.ROOT) + "%", replacement);
    }
}
