package com.blib.engine.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.layout.UiRect;
import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Shared client-side draft for the recipe authoring panels. The outliner loads a recipe into this draft, the item
 * browser supplies draggable item stacks, and the editor owns the crafting-grid slot rects used for drops.
 */
@ApiStatus.Internal
public final class RecipeAuthoringState {

    public enum SlotKind {
        INPUT,
        OUTPUT
    }

    public enum RecipeDraftType {
        CRAFTING_SHAPED("minecraft:crafting_shaped", "Shaped", true),
        CRAFTING_SHAPELESS("minecraft:crafting_shapeless", "Shapeless", true),
        SMELTING("minecraft:smelting", "Smelt", true),
        BLASTING("minecraft:blasting", "Blast", true),
        SMOKING("minecraft:smoking", "Smoke", true),
        CAMPFIRE_COOKING("minecraft:campfire_cooking", "Campfire", true),
        STONECUTTING("minecraft:stonecutting", "Stonecut", true),
        SMITHING_TRANSFORM("minecraft:smithing_transform", "Smithing", true),
        SMITHING_TRIM("minecraft:smithing_trim", "Trim", true),
        UNSUPPORTED("", "Read-only", false);

        private static final List<RecipeDraftType> AUTHORABLE = Arrays
            .stream(values())
            .filter(RecipeDraftType::authorable)
            .toList();

        private final String typeId;

        private final String label;

        private final boolean authorable;

        RecipeDraftType(String typeId, String label, boolean authorable) {
            this.typeId = typeId;
            this.label = label;
            this.authorable = authorable;
        }

        public String typeId() {
            return typeId;
        }

        public String label() {
            return label;
        }

        public boolean authorable() {
            return authorable;
        }

        public static List<RecipeDraftType> authorableTypes() {
            return AUTHORABLE;
        }

        public static RecipeDraftType fromRecipe(RecipeHolder<?> holder) {
            var serializer = holder.value().getSerializer();
            if (serializer == RecipeSerializer.SHAPED_RECIPE) {
                return CRAFTING_SHAPED;
            }
            if (serializer == RecipeSerializer.SHAPELESS_RECIPE) {
                return CRAFTING_SHAPELESS;
            }
            if (serializer == RecipeSerializer.SMELTING_RECIPE) {
                return SMELTING;
            }
            if (serializer == RecipeSerializer.BLASTING_RECIPE) {
                return BLASTING;
            }
            if (serializer == RecipeSerializer.SMOKING_RECIPE) {
                return SMOKING;
            }
            if (serializer == RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
                return CAMPFIRE_COOKING;
            }
            if (serializer == RecipeSerializer.STONECUTTER) {
                return STONECUTTING;
            }
            if (serializer == RecipeSerializer.SMITHING_TRANSFORM) {
                return SMITHING_TRANSFORM;
            }
            if (serializer == RecipeSerializer.SMITHING_TRIM) {
                return SMITHING_TRIM;
            }
            return UNSUPPORTED;
        }
    }

    public record SlotRef(SlotKind kind, int index) {

        public static SlotRef input(int index) {
            return new SlotRef(SlotKind.INPUT, Math.max(0, Math.min(8, index)));
        }

        public static SlotRef output() {
            return new SlotRef(SlotKind.OUTPUT, 0);
        }
    }

    public record DraftSlot(@Nullable ResourceLocation itemId, int count) {

        public static final DraftSlot EMPTY = new DraftSlot(null, 0);

        public static DraftSlot of(Item item, int count) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null || item == Items.AIR) {
                return EMPTY;
            }
            return new DraftSlot(id, count).normalized();
        }

        public static DraftSlot of(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                return EMPTY;
            }
            return of(stack.getItem(), stack.getCount());
        }

        public boolean isEmpty() {
            return itemId == null || item() == Items.AIR;
        }

        public Item item() {
            return itemId == null ? Items.AIR : BuiltInRegistries.ITEM.get(itemId);
        }

        public int maxCount() {
            if (isEmpty()) {
                return 0;
            }
            return new ItemStack(item()).getMaxStackSize();
        }

        public DraftSlot withCount(int nextCount) {
            if (isEmpty()) {
                return EMPTY;
            }
            return new DraftSlot(itemId, nextCount).normalized();
        }

        public ItemStack toStack() {
            if (isEmpty()) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(item(), count);
        }

        private DraftSlot normalized() {
            if (itemId == null) {
                return EMPTY;
            }
            var item = BuiltInRegistries.ITEM.get(itemId);
            if (item == Items.AIR) {
                return EMPTY;
            }
            var max = Math.max(1, new ItemStack(item).getMaxStackSize());
            return new DraftSlot(itemId, Math.max(1, Math.min(max, count)));
        }
    }

    public record DragStack(ResourceLocation itemId, int count) {

        public DraftSlot toDraftSlot() {
            return new DraftSlot(itemId, count).normalized();
        }

        public ItemStack toStack() {
            return toDraftSlot().toStack();
        }
    }

    private static final DraftSlot[] GRID = new DraftSlot[9];

    private static final UiRect[] GRID_RECTS = new UiRect[9];

    private static DraftSlot output = DraftSlot.EMPTY;

    private static @Nullable UiRect outputRect;

    private static @Nullable ResourceLocation selectedRecipeId;

    private static @Nullable SlotRef selectedSlot;

    private static @Nullable DragStack draggedStack;

    private static RecipeDraftType recipeType = RecipeDraftType.CRAFTING_SHAPED;

    private static String recipeIdText = "";

    private static String status = "Create a recipe or select one from the outliner.";

    private static float experience;

    private static int cookingTime = defaultCookingTime(recipeType);

    private static boolean dirty;

    static {
        clearGrid();
    }

    private RecipeAuthoringState() {}

    public static DraftSlot gridSlot(int index) {
        return GRID[Math.max(0, Math.min(8, index))];
    }

    public static DraftSlot outputSlot() {
        return output;
    }

    public static RecipeDraftType recipeType() {
        return recipeType;
    }

    public static float experience() {
        return experience;
    }

    public static void setExperience(float nextExperience) {
        experience = Math.max(0.0F, nextExperience);
        dirty = true;
    }

    public static int cookingTime() {
        return cookingTime;
    }

    public static void setCookingTime(int nextCookingTime) {
        cookingTime = Math.max(1, nextCookingTime);
        dirty = true;
    }

    public static DraftSlot slot(SlotRef ref) {
        return ref.kind() == SlotKind.OUTPUT ? output : gridSlot(ref.index());
    }

    public static String slotLabel(SlotRef ref) {
        if (ref.kind() == SlotKind.OUTPUT) {
            return "Output";
        }
        return switch (recipeType) {
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> "Ingredient";
            case STONECUTTING -> "Input";
            case SMITHING_TRANSFORM, SMITHING_TRIM -> switch (ref.index()) {
                case 0 -> "Template";
                case 1 -> "Base";
                case 2 -> "Addition";
                default -> "Input";
            };
            default -> "Ingredient " + (ref.index() + 1);
        };
    }

    public static @Nullable SlotRef selectedSlot() {
        return selectedSlot;
    }

    public static void selectSlot(@Nullable SlotRef ref) {
        selectedSlot = ref;
    }

    public static @Nullable ResourceLocation selectedRecipeId() {
        return selectedRecipeId;
    }

    public static String recipeIdText() {
        return recipeIdText;
    }

    public static void setRecipeIdText(String text) {
        recipeIdText = text == null ? "" : text.trim();
        dirty = true;
    }

    public static String status() {
        return status;
    }

    public static void setStatus(String message) {
        status = message == null || message.isBlank() ? "" : message;
    }

    public static boolean dirty() {
        return dirty;
    }

    public static void newDraft() {
        newDraft(recipeType);
    }

    public static void newDraft(RecipeDraftType type) {
        recipeType = type == null || type == RecipeDraftType.UNSUPPORTED ? RecipeDraftType.CRAFTING_SHAPED : type;
        clearGrid();
        output = DraftSlot.EMPTY;
        selectedRecipeId = null;
        selectedSlot = null;
        recipeIdText = defaultNamespace() + ":new_recipe";
        experience = 0.0F;
        cookingTime = defaultCookingTime(recipeType);
        status = "New " + recipeType.label().toLowerCase(Locale.ROOT) + " recipe draft.";
        dirty = false;
    }

    public static void loadRecipe(RecipeHolder<?> holder) {
        recipeType = RecipeDraftType.fromRecipe(holder);
        clearGrid();
        output = DraftSlot.EMPTY;
        selectedRecipeId = holder.id();
        recipeIdText = holder.id().toString();
        selectedSlot = null;
        experience = 0.0F;
        cookingTime = defaultCookingTime(recipeType);

        var recipe = holder.value();
        var level = Minecraft.getInstance().level;
        if (level != null) {
            output = DraftSlot.of(recipe.getResultItem(level.registryAccess()));
        }

        switch (recipeType) {
            case CRAFTING_SHAPED -> {
                var ingredients = recipe.getIngredients();
                if (recipe instanceof ShapedRecipe shaped) {
                    var width = Math.min(3, shaped.getWidth());
                    var height = Math.min(3, shaped.getHeight());
                    for (var row = 0; row < height; row++) {
                        for (var col = 0; col < width; col++) {
                            var ingredientIndex = row * shaped.getWidth() + col;
                            if (ingredientIndex >= 0 && ingredientIndex < ingredients.size()) {
                                GRID[row * 3 + col] = firstStack(ingredients.get(ingredientIndex));
                            }
                        }
                    }
                }
            }
            case CRAFTING_SHAPELESS -> {
                var ingredients = recipe.getIngredients();
                for (var i = 0; i < Math.min(9, ingredients.size()); i++) {
                    GRID[i] = firstStack(ingredients.get(i));
                }
            }
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> {
                if (recipe instanceof AbstractCookingRecipe cooking) {
                    var ingredients = cooking.getIngredients();
                    if (!ingredients.isEmpty()) {
                        GRID[0] = firstStack(ingredients.getFirst());
                    }
                    experience = cooking.getExperience();
                    cookingTime = cooking.getCookingTime();
                }
            }
            case STONECUTTING -> {
                if (recipe instanceof SingleItemRecipe single) {
                    var ingredients = single.getIngredients();
                    if (!ingredients.isEmpty()) {
                        GRID[0] = firstStack(ingredients.getFirst());
                    }
                }
            }
            case SMITHING_TRANSFORM, SMITHING_TRIM -> {
                GRID[0] = firstStack(readIngredientField(recipe, "template"));
                GRID[1] = firstStack(readIngredientField(recipe, "base"));
                GRID[2] = firstStack(readIngredientField(recipe, "addition"));
            }
            case UNSUPPORTED -> {
                // Unsupported/custom recipes intentionally stay read-only; do not project their ingredients into a
                // wrong editor surface.
            }
        }

        status = recipeType == RecipeDraftType.UNSUPPORTED
            ? "Loaded " + holder.id() + " as read-only. This serializer is not editable yet."
            : "Loaded " + holder.id() + ".";
        dirty = false;
    }

    public static void setSlot(SlotRef ref, DraftSlot slot) {
        if (ref.kind() == SlotKind.OUTPUT) {
            output = slot == null ? DraftSlot.EMPTY : slot.normalized();
        } else {
            GRID[ref.index()] = slot == null ? DraftSlot.EMPTY : slot.normalized();
        }
        selectedSlot = ref;
        dirty = true;
    }

    public static void clearSlot(SlotRef ref) {
        setSlot(ref, DraftSlot.EMPTY);
    }

    public static void changeCount(SlotRef ref, int delta) {
        var slot = slot(ref);
        if (slot.isEmpty()) {
            return;
        }
        setSlot(ref, slot.withCount(slot.count() + delta));
    }

    public static void setCount(SlotRef ref, int count) {
        var slot = slot(ref);
        if (slot.isEmpty()) {
            return;
        }
        setSlot(ref, slot.withCount(count));
    }

    public static void publishEditorSlots(UiRect[] gridRects, UiRect outputSlotRect) {
        for (var i = 0; i < GRID_RECTS.length; i++) {
            GRID_RECTS[i] = i < gridRects.length ? gridRects[i] : null;
        }
        outputRect = outputSlotRect;
    }

    public static @Nullable SlotRef slotAt(double mouseX, double mouseY) {
        if (outputRect != null && outputRect.contains(mouseX, mouseY)) {
            return SlotRef.output();
        }
        for (var i = 0; i < GRID_RECTS.length; i++) {
            var rect = GRID_RECTS[i];
            if (rect != null && rect.contains(mouseX, mouseY)) {
                return SlotRef.input(i);
            }
        }
        return null;
    }

    public static void beginDrag(Item item) {
        var id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || item == Items.AIR) {
            draggedStack = null;
            return;
        }
        draggedStack = new DragStack(id, 1);
    }

    public static boolean hasDrag() {
        return draggedStack != null;
    }

    public static @Nullable DragStack draggedStack() {
        return draggedStack;
    }

    public static void clearDrag() {
        draggedStack = null;
    }

    public static boolean dropDraggedAt(double mouseX, double mouseY) {
        var drag = draggedStack;
        if (drag == null) {
            return false;
        }
        var target = slotAt(mouseX, mouseY);
        if (target == null) {
            return false;
        }
        setSlot(target, drag.toDraftSlot());
        return true;
    }

    public static @Nullable Path saveToProject(String recipeIdInput) throws IOException {
        recipeIdText = recipeIdInput == null ? "" : recipeIdInput.trim();
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            status = "Open a project before saving recipes.";
            return null;
        }
        if (!recipeType.authorable()) {
            status = "This recipe type is read-only.";
            return null;
        }

        var id = ResourceLocation.tryParse(recipeIdText);
        if (id == null) {
            status = "Invalid recipe id.";
            return null;
        }

        var json = switch (recipeType) {
            case CRAFTING_SHAPED -> buildShapedRecipeJson();
            case CRAFTING_SHAPELESS -> buildShapelessRecipeJson();
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> buildCookingRecipeJson();
            case STONECUTTING -> buildStonecuttingRecipeJson();
            case SMITHING_TRANSFORM -> buildSmithingTransformRecipeJson();
            case SMITHING_TRIM -> buildSmithingTrimRecipeJson();
            case UNSUPPORTED -> null;
        };
        if (json == null) {
            return null;
        }

        var relPath = "data/" + id.getNamespace() + "/recipe/" + id.getPath() + ".json";
        var saved = EngineProjectIO.writeDataJson(project, relPath, json);
        selectedRecipeId = id;
        dirty = false;
        status = hasInputCountOverOne()
            ? "Saved " + relPath + ". Input slot counts are editor-only for vanilla crafting recipes."
            : "Saved " + relPath + ".";
        return saved;
    }

    private static JsonObject buildShapedRecipeJson() {
        if (output.isEmpty()) {
            status = "Choose an output item before saving.";
            return null;
        }
        if (!hasAnyIngredient()) {
            status = "Add at least one crafting ingredient before saving.";
            return null;
        }

        var bounds = ingredientBounds();
        var minRow = bounds[0];
        var maxRow = bounds[1];
        var minCol = bounds[2];
        var maxCol = bounds[3];

        var symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var keys = new LinkedHashMap<ResourceLocation, Character>();
        var pattern = new JsonArray();
        for (var row = minRow; row <= maxRow; row++) {
            var line = new StringBuilder();
            for (var col = minCol; col <= maxCol; col++) {
                var slot = GRID[row * 3 + col];
                if (slot.isEmpty()) {
                    line.append(' ');
                    continue;
                }
                var symbol = keys.computeIfAbsent(slot.itemId(), ignored -> symbols.charAt(keys.size()));
                line.append(symbol);
            }
            pattern.add(line.toString());
        }

        var keyObj = new JsonObject();
        for (Map.Entry<ResourceLocation, Character> entry : keys.entrySet()) {
            keyObj.add(String.valueOf(entry.getValue()), ingredientJson(entry.getKey()));
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.addProperty("category", "misc");
        json.add("pattern", pattern);
        json.add("key", keyObj);
        json.add("result", resultJson(output));
        return json;
    }

    private static JsonObject buildShapelessRecipeJson() {
        if (output.isEmpty()) {
            status = "Choose an output item before saving.";
            return null;
        }
        if (!hasAnyIngredient()) {
            status = "Add at least one crafting ingredient before saving.";
            return null;
        }

        var ingredients = new JsonArray();
        for (var slot : GRID) {
            if (!slot.isEmpty()) {
                ingredients.add(ingredientJson(slot.itemId()));
            }
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.addProperty("category", "misc");
        json.add("ingredients", ingredients);
        json.add("result", resultJson(output));
        return json;
    }

    private static JsonObject buildCookingRecipeJson() {
        if (!requireSlot(0, "ingredient") || !requireOutput()) {
            return null;
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.addProperty("category", "misc");
        json.add("ingredient", ingredientJson(GRID[0].itemId()));
        json.add("result", resultJson(output));
        json.addProperty("experience", experience);
        json.addProperty("cookingtime", cookingTime);
        return json;
    }

    private static JsonObject buildStonecuttingRecipeJson() {
        if (!requireSlot(0, "input") || !requireOutput()) {
            return null;
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.add("ingredient", ingredientJson(GRID[0].itemId()));
        json.add("result", resultJson(output));
        return json;
    }

    private static JsonObject buildSmithingTransformRecipeJson() {
        if (!requireSlot(0, "template") || !requireSlot(1, "base") || !requireSlot(2, "addition") || !requireOutput()) {
            return null;
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.add("template", ingredientJson(GRID[0].itemId()));
        json.add("base", ingredientJson(GRID[1].itemId()));
        json.add("addition", ingredientJson(GRID[2].itemId()));
        json.add("result", resultJson(output));
        return json;
    }

    private static JsonObject buildSmithingTrimRecipeJson() {
        if (!requireSlot(0, "template") || !requireSlot(1, "base") || !requireSlot(2, "addition")) {
            return null;
        }

        var json = new JsonObject();
        json.addProperty("type", recipeType.typeId());
        json.add("template", ingredientJson(GRID[0].itemId()));
        json.add("base", ingredientJson(GRID[1].itemId()));
        json.add("addition", ingredientJson(GRID[2].itemId()));
        return json;
    }

    private static boolean requireSlot(int index, String label) {
        if (GRID[index].isEmpty()) {
            status = "Choose a " + label + " item before saving.";
            return false;
        }
        return true;
    }

    private static boolean requireOutput() {
        if (output.isEmpty()) {
            status = "Choose an output item before saving.";
            return false;
        }
        return true;
    }

    private static JsonObject ingredientJson(ResourceLocation itemId) {
        var ingredient = new JsonObject();
        ingredient.addProperty("item", itemId.toString());
        return ingredient;
    }

    private static JsonObject resultJson(DraftSlot slot) {
        var result = new JsonObject();
        result.addProperty("id", slot.itemId().toString());
        if (slot.count() != 1) {
            result.addProperty("count", slot.count());
        }
        return result;
    }

    private static int[] ingredientBounds() {
        var minRow = 3;
        var maxRow = -1;
        var minCol = 3;
        var maxCol = -1;
        for (var i = 0; i < GRID.length; i++) {
            if (GRID[i].isEmpty()) {
                continue;
            }
            var row = i / 3;
            var col = i % 3;
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minCol = Math.min(minCol, col);
            maxCol = Math.max(maxCol, col);
        }
        return new int[] { minRow, maxRow, minCol, maxCol };
    }

    private static boolean hasAnyIngredient() {
        for (var slot : GRID) {
            if (!slot.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasInputCountOverOne() {
        for (var slot : GRID) {
            if (!slot.isEmpty() && slot.count() > 1) {
                return true;
            }
        }
        return false;
    }

    private static DraftSlot firstStack(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return DraftSlot.EMPTY;
        }
        for (var stack : ingredient.getItems()) {
            if (!stack.isEmpty()) {
                return DraftSlot.of(stack);
            }
        }
        return DraftSlot.EMPTY;
    }

    private static Ingredient readIngredientField(Object recipe, String fieldName) {
        var type = recipe.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                var value = field.get(recipe);
                if (value instanceof Ingredient ingredient) {
                    return ingredient;
                }
            } catch (ReflectiveOperationException ignored) {
                // Try the superclass; smithing recipe ingredient fields are not exposed by public accessors.
            }
            type = type.getSuperclass();
        }
        return Ingredient.EMPTY;
    }

    private static void clearGrid() {
        for (var i = 0; i < GRID.length; i++) {
            GRID[i] = DraftSlot.EMPTY;
        }
    }

    private static String defaultNamespace() {
        var project = ProjectSession.activeProjectName();
        var raw = project.isEmpty() ? "blib" : project.toLowerCase(Locale.ROOT);
        var normalized = raw.replaceAll("[^a-z0-9_.-]", "_");
        return ResourceLocation.isValidNamespace(normalized) ? normalized : "blib";
    }

    private static int defaultCookingTime(RecipeDraftType type) {
        return switch (type) {
            case BLASTING, SMOKING -> 100;
            case CAMPFIRE_COOKING -> 600;
            default -> 200;
        };
    }
}
