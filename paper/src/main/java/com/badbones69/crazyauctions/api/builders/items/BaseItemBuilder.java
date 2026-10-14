package com.badbones69.crazyauctions.api.builders.items;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.builders.items.plugins.ICustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.VanillaItemStack;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.cosmetics.HMCCustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.custom.CraftEngineCustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.custom.ItemsAdderCustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.custom.NexoCustomItem;
import com.badbones69.crazyauctions.api.builders.items.plugins.types.custom.OraxenCustomItem;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.items.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.SkullBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.custom.CustomBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.fireworks.FireworkBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.fireworks.FireworkStarBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.tools.ToolBuilder;
import com.ryderbelserion.fusion.paper.enums.ItemState;
import com.ryderbelserion.fusion.paper.utils.ColorUtils;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.MapItemColor;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {

    private static final Set<String> tools = Set.of(
        ItemType.WOODEN_SWORD.key().asString(),
        ItemType.WOODEN_PICKAXE.key().asString(),
        ItemType.WOODEN_AXE.key().asString(),
        ItemType.WOODEN_HOE.key().asString(),
        ItemType.WOODEN_SHOVEL.key().asString(),

        ItemType.STONE_SWORD.key().asString(),
        ItemType.STONE_PICKAXE.key().asString(),
        ItemType.STONE_AXE.key().asString(),
        ItemType.STONE_HOE.key().asString(),
        ItemType.STONE_SHOVEL.key().asString(),

        ItemType.IRON_SWORD.key().asString(),
        ItemType.IRON_PICKAXE.key().asString(),
        ItemType.IRON_AXE.key().asString(),
        ItemType.IRON_HOE.key().asString(),
        ItemType.IRON_SHOVEL.key().asString(),

        ItemType.DIAMOND_SWORD.key().asString(),
        ItemType.DIAMOND_PICKAXE.key().asString(),
        ItemType.DIAMOND_AXE.key().asString(),
        ItemType.DIAMOND_HOE.key().asString(),
        ItemType.DIAMOND_SHOVEL.key().asString(),

        ItemType.GOLDEN_SWORD.key().asString(),
        ItemType.GOLDEN_PICKAXE.key().asString(),
        ItemType.GOLDEN_AXE.key().asString(),
        ItemType.GOLDEN_HOE.key().asString(),
        ItemType.GOLDEN_SHOVEL.key().asString(),

        ItemType.NETHERITE_SWORD.key().asString(),
        ItemType.NETHERITE_PICKAXE.key().asString(),
        ItemType.NETHERITE_AXE.key().asString(),
        ItemType.NETHERITE_HOE.key().asString(),
        ItemType.NETHERITE_SHOVEL.key().asString(),

        ItemType.COPPER_PICKAXE.key().asString(),
        ItemType.COPPER_SHOVEL.key().asString(),
        ItemType.COPPER_SWORD.key().asString(),
        ItemType.COPPER_AXE.key().asString(),
        ItemType.COPPER_HOE.key().asString()
    );

    private static final Set<String> leather_items = Set.of(
        ItemType.LEATHER_HELMET.key().asString(),
        ItemType.LEATHER_CHESTPLATE.key().asString(),
        ItemType.LEATHER_LEGGINGS.key().asString(),
        ItemType.LEATHER_BOOTS.key().asString(),
        ItemType.LEATHER_HORSE_ARMOR.key().asString()
    );

    private static final Set<String> armor = Set.of(
        ItemType.CHAINMAIL_HELMET.key().asString(),
        ItemType.CHAINMAIL_CHESTPLATE.key().asString(),
        ItemType.CHAINMAIL_LEGGINGS.key().asString(),
        ItemType.CHAINMAIL_BOOTS.key().asString(),

        ItemType.IRON_HELMET.key().asString(),
        ItemType.IRON_CHESTPLATE.key().asString(),
        ItemType.IRON_LEGGINGS.key().asString(),
        ItemType.IRON_BOOTS.key().asString(),

        ItemType.DIAMOND_HELMET.key().asString(),
        ItemType.DIAMOND_CHESTPLATE.key().asString(),
        ItemType.DIAMOND_LEGGINGS.key().asString(),
        ItemType.DIAMOND_BOOTS.key().asString(),

        ItemType.GOLDEN_HELMET.key().asString(),
        ItemType.GOLDEN_CHESTPLATE.key().asString(),
        ItemType.GOLDEN_LEGGINGS.key().asString(),
        ItemType.GOLDEN_BOOTS.key().asString(),

        ItemType.NETHERITE_HELMET.key().asString(),
        ItemType.NETHERITE_CHESTPLATE.key().asString(),
        ItemType.NETHERITE_LEGGINGS.key().asString(),
        ItemType.NETHERITE_BOOTS.key().asString(),

        ItemType.TURTLE_HELMET.key().asString(),

        ItemType.COPPER_HELMET.key().asString(),
        ItemType.COPPER_CHESTPLATE.key().asString(),
        ItemType.COPPER_LEGGINGS.key().asString(),
        ItemType.COPPER_BOOTS.key().asString(),

        ItemType.LEATHER_HELMET.key().asString(),
        ItemType.LEATHER_CHESTPLATE.key().asString(),
        ItemType.LEATHER_LEGGINGS.key().asString(),
        ItemType.LEATHER_BOOTS.key().asString(),
        ItemType.LEATHER_HORSE_ARMOR.key().asString()
    );

    private static final Set<String> potions = Set.of(
        ItemType.POTION.key().asString(),
        ItemType.SPLASH_POTION.key().asString(),
        ItemType.LINGERING_POTION.key().asString()
    );

    protected final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();
    
    protected ItemStack itemStack;
    protected ItemType itemType;

    private DataComponentType.Valued<@NonNull Component> type = DataComponentTypes.CUSTOM_NAME;
    private Map<String, String> placeholders = new HashMap<>();
    private List<String> displayLore = new ArrayList<>();
    private String displayName = "";

    public BaseItemBuilder(@NonNull final ItemType itemType, final int amount, @NonNull final Consumer<BaseItemBuilder> consumer) {
        this(itemType.createItemStack(Math.min(amount, 1))); // create ItemStack, and populate item type.

        consumer.accept(this); // apply anything else to the class.
    }

    public BaseItemBuilder(@NonNull final ItemType itemType, @NonNull final Consumer<BaseItemBuilder> consumer) {
        this(itemType, 1, consumer);
    }

    public BaseItemBuilder(@NonNull final ItemType itemType) {
        this(itemType, 1, item -> {});
    }

    public BaseItemBuilder(@NonNull final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemType = this.itemStack.getType().asItemType();
    }

    public BaseItemBuilder(@NonNull final String itemStack) {
        withCustomItem(itemStack);
    }

    public B build() {
        return (B) this;
    }

    public @NonNull final GuiItem asGuiItem(@NonNull final Audience audience, @NonNull final GuiAction<@NonNull InventoryClickEvent> action) {
        return new GuiItem(asItemStack(audience), action);
    }

    public @NonNull final GuiItem asGuiItem(@NonNull final GuiAction<@NonNull InventoryClickEvent> action) {
        return new GuiItem(asItemStack(), action);
    }

    public @NonNull final GuiItem asGuiItem(@NonNull final Audience audience) {
        return asGuiItem(audience, _ -> {});
    }

    public @NonNull final GuiItem asGuiItem() {
        return asGuiItem(Audience.empty());
    }

    public @NonNull ItemStack asItemStack(@Nullable final Audience audience) {
        final Audience safeAudience = audience == null ? Audience.empty() : audience;

        this.itemStack.editMeta(itemMeta -> {
            if (!this.displayName.isEmpty()) {
                itemMeta.setDisplayName(Methods.color(this.fusion.replacePlaceholders(this.fusion.papi(safeAudience, this.displayName), this.placeholders)));
            }

            if (!this.displayLore.isEmpty()) {
                final List<String> components = new ArrayList<>(this.displayLore.size());

                this.displayLore.forEach(line -> components.add(Methods.color(this.fusion.replacePlaceholders(this.fusion.papi(safeAudience, line), this.placeholders))));

                itemMeta.setLore(components);
            }
        });

        build();

        return this.itemStack;
    }

    public @NonNull ItemStack getItemStack() {
        return this.itemStack;
    }

    public @NonNull ItemStack asItemStack() {
        return asItemStack(Audience.empty());
    }

    public @NonNull B withConsumer(@NonNull final Consumer<B> consumer) {
        consumer.accept((B) this);

        return (B) this;
    }

    public @NonNull B displayName(@NonNull final Component displayName, @NonNull final ItemState itemState) {
        this.type = switch (itemState) {
            case ITEM_NAME -> DataComponentTypes.ITEM_NAME;
            case CUSTOM_NAME -> DataComponentTypes.CUSTOM_NAME;
        };

        this.itemStack.setData(this.type, displayName);

        return (B) this;
    }

    public @NonNull B withDisplayName(@NonNull final String displayName, @NonNull final ItemState itemState) {
        this.displayName = displayName;

        this.type = switch (itemState) {
            case ITEM_NAME -> DataComponentTypes.ITEM_NAME;
            case CUSTOM_NAME -> DataComponentTypes.CUSTOM_NAME;
        };

        return (B) this;
    }

    public @NonNull B withDisplayName(@NonNull final String displayName) {
        return withDisplayName(displayName, ItemState.CUSTOM_NAME);
    }

    public @NonNull B withDisplayLore(@NonNull final List<String> displayLore) {
        this.displayLore = displayLore;

        return (B) this;
    }

    public @NonNull B addDisplayLore(@NonNull final String displayLore) {
        if (displayLore.isEmpty()) return (B) this;

        this.displayLore.add(displayLore);

        return (B) this;
    }

    public @NonNull B withCustomItem(@NonNull final String item) {
        final String plugin = this.fusion.getItemsPlugin().toLowerCase();

        final ICustomItem customItem = switch (plugin) {
            case "craftengine" -> new CraftEngineCustomItem(this, item, false).init();
            case "itemsadder" -> new ItemsAdderCustomItem(this, item, false).init();
            case "oraxen" -> new OraxenCustomItem(this, item, false).init();
            case "nexo" -> new NexoCustomItem(this, item, false).init();
            case "hmcwraps" -> new HMCCustomItem(this, item, false).init();
            default -> {
                if (item.contains("@")) {
                    final String[] split = item.split("@");
                    final String namespace = split[0];
                    final String id = split[1];

                    yield switch (namespace) {
                        case "craftengine" -> new CraftEngineCustomItem(this, id, false).init();
                        case "itemsadder" -> new ItemsAdderCustomItem(this, id, false).init();
                        case "oraxen" -> new OraxenCustomItem(this, id, false).init();
                        case "nexo" -> new NexoCustomItem(this, id, false).init();
                        case "hmcwraps" -> new HMCCustomItem(this, id, false).init();
                        default -> new VanillaItemStack(this, item).init();
                    };
                }

                if (this.fusion.isPluginEnabled("CraftEngine")) {
                    yield new CraftEngineCustomItem(this, item, false).init();
                }

                if (this.fusion.isPluginEnabled("ItemsAdder")) {
                    yield new ItemsAdderCustomItem(this, item, true).init();
                }

                if (this.fusion.isPluginEnabled("Oraxen")) {
                    yield new OraxenCustomItem(this, item, true).init();
                }

                if (this.fusion.isPluginEnabled("Nexo")) {
                    yield new NexoCustomItem(this, item, true).init();
                }

                if (this.fusion.isPluginEnabled("HMCWraps")) {
                    yield new HMCCustomItem(this, item, true).init();
                }

                yield new VanillaItemStack(this, item).init();
            }
        };

        final Optional<ItemStack> origin = customItem.getItemStack();

        final String implementation = customItem.getImpl();

        if (origin.isEmpty()) {
            this.fusion.log(Level.WARNING, "%s item could not be used to create an ItemStack using implementation %s. It was likely an invalid material.", item, implementation);

            return (B) this;
        }

        withItemStack(origin.get());

        return (B) this;
    }

    public @NonNull B withBase64(@NonNull final String itemStack) {
        if (itemStack.isEmpty()) return (B) this;

        try {
            this.itemStack = ItemUtils.fromBase64(itemStack);
        } catch (final Exception exception) {
            final ItemType stone = ItemType.STONE;

            this.itemStack = stone.createItemStack();
            this.itemType = stone;
        }

        this.itemType = this.itemStack.getType().asItemType();

        return (B) this;
    }

    public @NonNull B withItemStack(@NonNull final ItemStack itemStack) {
        if (itemStack.isEmpty()) return (B) this;

        this.itemStack = itemStack;
        this.itemType = this.itemStack.getType().asItemType();

        return (B) this;
    }

    public @NonNull B withType(@NonNull final ItemType itemType, final int amount) {
        final ItemStack itemStack = itemType.createItemStack(Math.max(amount, 1));

        this.itemStack = this.itemStack == null ? itemStack : this.itemStack.withType(itemStack.getType());
        this.itemStack.setAmount(itemStack.getAmount());

        this.itemType = itemType;

        return (B) this;
    }

    public @NonNull B withType(@NonNull final ItemType itemType) {
        return withType(itemType, 1);
    }

    public @NonNull B addFoodComponent(@NonNull final Consumer<FoodProperties.Builder> consumer) {
        if (!this.itemStack.hasData(DataComponentTypes.CONSUMABLE)) return (B) this;

        final FoodProperties.Builder foodProperties = FoodProperties.food();

        consumer.accept(foodProperties);

        this.itemStack.setData(DataComponentTypes.FOOD, foodProperties);

        return (B) this;
    }

    public @NonNull B setAmount(final int amount) {
        this.itemStack.setAmount(Math.max(amount, 1));

        return (B) this;
    }

    public @NonNull B addEnchantments(@NonNull final Map<String, Integer> enchantments) {
        for (final Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            addEnchantment(entry.getKey(), entry.getValue());
        }

        return (B) this;
    }

    public @NonNull B addEnchantment(@NonNull final String enchant, final int level) {
        if (enchant.isEmpty()) return (B) this;

        final Enchantment enchantment = ItemUtils.getEnchantment(enchant);

        if (enchantment == null) return (B) this;

        final ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();

        if (isBook() && this.itemStack.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
            final ItemEnchantments enchantments = this.itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS);

            if (enchantments != null) {
                builder.addAll(enchantments.enchantments());
            }
        } else {
            final Map<Enchantment, Integer> enchantments = this.itemStack.getEnchantments();

            if (!enchantments.isEmpty()) {
                builder.addAll(enchantments);
            }
        }

        builder.add(enchantment, level);

        this.itemStack.setData(isBook() ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS, builder.build());

        return (B) this;
    }

    public @NonNull B removeEnchantment(@NonNull final String enchant) {
        if (enchant.isEmpty()) return (B) this;

        final Enchantment enchantment = ItemUtils.getEnchantment(enchant);

        if (enchantment == null) return (B) this;

        this.itemStack.removeEnchantment(enchantment);

        return (B) this;
    }

    public @NonNull String getPlainName() {
        Component component = Component.empty();

        if (this.itemStack.hasData(DataComponentTypes.ITEM_NAME)) {
            final Component itemName = this.itemStack.getData(DataComponentTypes.ITEM_NAME);

            if (itemName != null) component = itemName;
        } else if (this.itemStack.hasData(DataComponentTypes.CUSTOM_NAME)) {
            final Component customName = this.itemStack.getData(DataComponentTypes.CUSTOM_NAME);

            if (customName != null) component = customName;
        }

        return PlainTextComponentSerializer.plainText().serializeOr(component, "");
    }

    public @NonNull List<String> getPlainLore() {
        final List<String> plainLore = new ArrayList<>();

        if (this.itemStack.hasData(DataComponentTypes.LORE)) {
            final ItemLore lore = this.itemStack.getData(DataComponentTypes.LORE);

            if (lore != null) lore.lines().forEach(line -> plainLore.add(PlainTextComponentSerializer.plainText().serialize(line)));
        }

        return plainLore;
    }

    public @NonNull B addEnchantGlint() {
        this.itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        return (B) this;
    }

    public @NonNull B removeEnchantGlint() {
        if (!this.itemStack.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) return (B) this;

        this.itemStack.unsetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);

        return (B) this;
    }

    public @NonNull B hideToolTip() {
        this.itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder().hideTooltip(true).build());

        return (B) this;
    }

    public @NonNull B showToolTip() {
        if (!this.itemStack.hasData(DataComponentTypes.TOOLTIP_DISPLAY)) return (B) this;

        this.itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder().hideTooltip(false).build());

        return (B) this;
    }

    public @NonNull B hideComponents(@NonNull final List<String> components) {
        for (final String component : components) {
            hideComponent(component);
        }

        return (B) this;
    }

    // i.e. minecraft:banner_patterns without minecraft
    public @NonNull B hideComponent(@NonNull final String component) {
        if (component.isEmpty()) return (B) this;

        final Optional<DataComponentType> type = ItemUtils.getDataComponentType(component);

        if (type.isEmpty()) return (B) this;

        final TooltipDisplay.Builder display = TooltipDisplay.tooltipDisplay();

        if (this.itemStack.hasData(DataComponentTypes.TOOLTIP_DISPLAY)) {
            final TooltipDisplay components = this.itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);

            if (components != null) display.hiddenComponents(components.hiddenComponents());
        }

        display.addHiddenComponents(type.get());

        this.itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);

        return (B) this;
    }

    public @NonNull B setUnbreakable(final boolean isUnbreakable) {
        if (isUnbreakable && !this.itemStack.hasData(DataComponentTypes.UNBREAKABLE)) {
            this.itemStack.setData(DataComponentTypes.UNBREAKABLE);

            return (B) this;
        }

        if (this.itemStack.hasData(DataComponentTypes.UNBREAKABLE)) this.itemStack.unsetData(DataComponentTypes.UNBREAKABLE);

        return (B) this;
    }

    public @NonNull B setRepairCost(final int repairCost) {
        if (repairCost == -1) return (B) this;

        this.itemStack.setData(DataComponentTypes.REPAIR_COST, repairCost);

        return (B) this;
    }

    public @NonNull B setTrim(@NonNull final String pattern, @NonNull final String material) {
        if (pattern.isEmpty() || material.isEmpty()) return (B) this;

        final TrimMaterial trimMaterial = ItemUtils.getTrimMaterial(material);

        if (trimMaterial == null) return (B) this;

        final TrimPattern trimPattern = ItemUtils.getTrimPattern(pattern);

        if (trimPattern == null) return (B) this;

        final ItemArmorTrim.Builder builder = ItemArmorTrim.itemArmorTrim(new ArmorTrim(trimMaterial, trimPattern));

        this.itemStack.setData(DataComponentTypes.TRIM, builder.build());

        return (B) this;
    }

    public @NonNull B setColor(@NonNull final String value) {
        if (value.isEmpty()) return (B) this;

        if (isMap()) {
            final Color color = value.contains(",") ? ColorUtils.getRGB(value) : ColorUtils.getColor(value);

            if (color != null) {
                this.itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build());
            }
        } else if (isLeather()) {
            final Color color = value.contains(",") ? ColorUtils.getRGB(value) : ColorUtils.getColor(value);

            if (color != null) {
                this.itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(color).build());
            }
        } else if (isShield()) {
            this.itemStack.setData(DataComponentTypes.BASE_COLOR, ColorUtils.getDyeColor(value));
        } else if (isPotion() || isTippedArrow()) {
            asPotionBuilder().setColor(value).build();
        }

        return (B) this;
    }

    public @NonNull B setItemDamage(final int damage) {
        if (damage == -1) return (B) this;

        this.itemStack.setData(DataComponentTypes.DAMAGE, Math.min(damage, this.itemType.getMaxDurability()));

        return (B) this;
    }

    public @NonNull B withSkull(@NonNull final String skull) {
        if (skull.isEmpty()) return (B) this;

        final Optional<HeadDatabaseAPI> key = this.fusion.getHeadApi();

        ItemStack itemStack = ItemType.PLAYER_HEAD.createItemStack();

        if (key.isPresent()) {
            final HeadDatabaseAPI api = key.get();

            if (api.isHead(skull)) itemStack = api.getItemHead(skull);
        }

        if (itemStack.hasData(DataComponentTypes.PROFILE)) {
            final ResolvableProfile profile = itemStack.getData(DataComponentTypes.PROFILE);

            if (profile != null) {
                this.itemStack.setData(DataComponentTypes.PROFILE, profile);
            }
        }

        return (B) this;
    }

    public @NonNull B addPlaceholder(@NonNull final String placeholder, @NonNull final String value) {
        this.placeholders.put(placeholder, value);

        return (B) this;
    }

    public @NonNull B setPlaceholders(@NonNull final Map<String, String> placeholders) {
        this.placeholders = placeholders;

        return (B) this;
    }

    public @NonNull B removePlaceholder(@NonNull final String placeholder) {
        this.placeholders.remove(placeholder);

        return (B) this;
    }

    public boolean hasPlaceholder(@NonNull final String placeholder) {
        return this.placeholders.containsKey(placeholder);
    }

    public @NonNull final B setPersistentDouble(@NonNull final NamespacedKey key, final double value) {
        this.itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.DOUBLE, value));

        return (B) this;
    }

    public @NonNull final B setPersistentInteger(@NonNull final NamespacedKey key, final int value) {
        this.itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.INTEGER, value));

        return (B) this;
    }

    public @NonNull final B setPersistentBoolean(@NonNull final NamespacedKey key, final boolean value) {
        this.itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.BOOLEAN, value));

        return (B) this;
    }

    public @NonNull final B setPersistentString(@NonNull final NamespacedKey key, @NonNull final String value) {
        this.itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.STRING, value));

        return (B) this;
    }

    public @NonNull final B setPersistentList(@NonNull final NamespacedKey key, @NonNull final List<String> values) {
        this.itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), values));

        return (B) this;
    }

    public final boolean getBoolean(@NonNull final NamespacedKey key) {
        return getContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false);
    }

    public final double getDouble(@NonNull final NamespacedKey key) {
        return getContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    public final int getInteger(@NonNull final NamespacedKey key) {
        return getContainer().getOrDefault(key, PersistentDataType.INTEGER, 0);
    }

    public @NonNull final List<String> getList(@NonNull final NamespacedKey key) {
        return getContainer().getOrDefault(key, PersistentDataType.LIST.strings(), Collections.emptyList());
    }

    public @NonNull final String getString(@NonNull final NamespacedKey key) {
        return getContainer().getOrDefault(key, PersistentDataType.STRING, "");
    }

    public @NonNull final B removePersistentKey(@Nullable final NamespacedKey key) {
        if (key == null) return (B) this;

        this.itemStack.editPersistentDataContainer(container -> {
            if (container.has(key)) container.remove(key);
        });

        return (B) this;
    }

    public final boolean hasKey(@NonNull final NamespacedKey key) {
        return getContainer().has(key);
    }

    public @NonNull final PersistentDataContainerView getContainer() {
        return this.itemStack.getPersistentDataContainer();
    }

    public @NonNull final FireworkBuilder asFireworkBuilder() {
        if (!isFirework()) throw new FusionException("This item type is not a firework rocket.");

        return new FireworkBuilder(this.itemStack);
    }

    public @NonNull final FireworkStarBuilder asFireworkStarBuilder() {
        if (!isFireworkStar()) throw new FusionException("This item type is not a firework star.");

        return new FireworkStarBuilder(this.itemStack);
    }

    public @NonNull final PatternBuilder asPatternBuilder() {
        if (isShield() || isBanner()) return new PatternBuilder(this.itemStack);

        throw new FusionException("This item type is not a shield/banner.");
    }

    public @NonNull final SkullBuilder asSkullBuilder() {
        if (!isPlayerHead()) throw new FusionException("This item type is not a skull.");

        return new SkullBuilder(this.itemStack);
    }

    public @NonNull final PotionBuilder asPotionBuilder() {
        if (isPotion() || isTippedArrow()) return new PotionBuilder(this.itemStack);

        throw new FusionException("This item type is not a potion / tipped arrow.");
    }

    public @NonNull final CustomBuilder asCustomBuilder() {
        return new CustomBuilder(this.itemStack);
    }

    public @NonNull final ToolBuilder asToolBuilder() {
        return new ToolBuilder(this.itemStack);
    }

    public void setItemToInventory(@NonNull final Audience audience, @NonNull final Inventory inventory, final int slot) {
        inventory.setItem(slot, asItemStack(audience));
    }

    public void addItemToInventory(@NonNull final Audience audience, @NonNull final Inventory inventory) {
        inventory.addItem(asItemStack(audience));
    }

    public void setItemToInventory(@NonNull final Inventory inventory, final int slot) {
        setItemToInventory(Audience.empty(), inventory, slot);
    }

    public void addItemToInventory(@NonNull final Inventory inventory) {
        addItemToInventory(Audience.empty(), inventory);
    }

    public final boolean isDyeable() {
        return isTippedArrow() || isShield() || isLeather() || isMap();
    }

    public final boolean isPlayerHead() {
        return asString().equalsIgnoreCase(ItemType.PLAYER_HEAD.key().asString());
    }

    public final boolean isFireworkStar() {
        return asString().equalsIgnoreCase(ItemType.FIREWORK_STAR.key().asString());
    }

    public final boolean isTippedArrow() {
        return asString().equalsIgnoreCase(ItemType.TIPPED_ARROW.key().asString());
    }

    public final boolean isFirework() {
        return asString().equalsIgnoreCase(ItemType.FIREWORK_ROCKET.key().asString());
    }

    public final boolean isSpawner() {
        return asString().equalsIgnoreCase(ItemType.SPAWNER.key().asString());
    }

    public final boolean isShield() {
        return asString().equalsIgnoreCase(ItemType.SHIELD.key().asString());
    }

    public final boolean isEdible() {
        return this.itemType.isEdible();
    }

    public final boolean isArmor() {
        return armor.contains(asString());
    }

    public final boolean isTool() {
        return tools.contains(asString());
    }

    public final boolean isLeather() {
        return leather_items.contains(asString());
    }

    public final boolean isPotion() {
        return potions.contains(asString());
    }

    public final boolean isBanner() {
        return getId().endsWith("_banner");
    }

    public final boolean isBook() {
        return asString().equalsIgnoreCase(ItemType.ENCHANTED_BOOK.key().asString());
    }

    public final boolean isMap() {
        return asString().equalsIgnoreCase(ItemType.FILLED_MAP.key().asString());
    }

    public @NonNull final String getTranslationKey() {
        return this.itemType.translationKey();
    }

    public @NonNull final ItemType getType() {
        return this.itemType;
    }

    public @NonNull final Key getKey() {
        return this.itemType.key();
    }

    public final String getNamespace() {
        return getKey().namespace();
    }

    public final String asString() {
        return getKey().asString();
    }

    public final String getId() {
        return getKey().value();
    }

    private TooltipDisplay.@NonNull Builder builder() {
        final TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

        if (this.itemStack.hasData(DataComponentTypes.TOOLTIP_DISPLAY)) {
            final TooltipDisplay display = this.itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);

            if (display != null) {
                builder.hideTooltip(display.hideTooltip());
                builder.hiddenComponents(display.hiddenComponents());
            }
        }

        return builder;
    }
}