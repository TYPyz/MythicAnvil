package com.typ.mythicanvil.compat.jei;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.block.ModBlocks;
import com.typ.mythicanvil.recipe.RitualRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class RitualRecipeCategory implements IRecipeCategory<RitualRecipe> {
    public static final ResourceLocation ARROW_IMAGE = ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID,
            "textures/gui/arrow_1.png");
    public static final ResourceLocation LONG_ARROW_IMAGE = ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID,
            "textures/gui/long_arrow.png");
    public static final ResourceLocation UPSIDEDOWN_ARROW_IMAGE = ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID,
            "textures/gui/upsidedown_arrow.png");
    public static final ResourceLocation RIGHT_CLICK_IMAGE = ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID,
            "textures/gui/right_click.png");

    private final IDrawable icon;
    private final Component title;

    public RitualRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MYTHIC_ANVIL));
//        this.background = guiHelper.createDrawable(MYIMAGE, 0, 0, 176, 120);
        this.title = Component.translatable("gui.mythicanvil.category.ritual");
    }

    @Override
    @Nonnull
    public RecipeType<RitualRecipe> getRecipeType() {
        return MythicAnvilJEIPlugin.RITUAL_RECIPE_TYPE;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return title;
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 120; // Match the background drawable height
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        // Target block slot (top center)
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 90)
                .addItemStack(new ItemStack(recipe.getTargetBlock().getBlock()))
                .setSlotName("target_block");

        // Trigger item slot (center left)
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 50)
                .addIngredients(recipe.getTriggerItem())
                .setSlotName("trigger_item");

        // Thrown items slots (bottom, spread horizontally)
        int thrownItemsCount = recipe.getThrownItems().size();
        int startX = Math.max(10, 88 - (thrownItemsCount * 18) / 2);

        for (int i = 0; i < thrownItemsCount; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, startX + i * 18, 15)
                    .addIngredients(recipe.getThrownItems().get(i))
                    .setSlotName("thrown_item_" + i);
        }

        // Result slot (center right)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 50)
                .addItemStack(recipe.getResult())
                .setSlotName("result");
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw additional text/info
        Minecraft minecraft = Minecraft.getInstance();

        // Draw "Right-click" text above target block
        Component rightClickText = Component.translatable("gui.mythicanvil.ritual.right_click");
        int textWidth = minecraft.font.width(rightClickText);
        guiGraphics.drawString(minecraft.font, rightClickText, 90 - textWidth / 2, 110, 0x404040, false);

        // Draw "with" text next to trigger item
        Component withText = Component.translatable("gui.mythicanvil.ritual.with");
        guiGraphics.drawString(minecraft.font, withText, 7, 70, 0x404040, false);

        // Draw "Q" text next to trigger item
        Component qText = Component.translatable("gui.mythicanvil.ritual.q");
        guiGraphics.drawString(minecraft.font, qText, 93, 57, 0x404040, false);

        // Draw consume trigger indicator with background - moved under "with" text
        if (recipe.shouldConsumeTrigger()) {
            Component consumeText = Component.translatable("gui.mythicanvil.ritual.consumes_trigger");
            int consumeTextWidth = minecraft.font.width(consumeText);

            // Draw background rectangle for consume trigger text
            guiGraphics.fill(0, 80, 2 + consumeTextWidth + 4, 92, 0x80FF0000); // Semi-transparent red background
            guiGraphics.fill(1, 81, 1 + consumeTextWidth + 3, 91, 0x40FFFFFF); // Light inner highlight

            guiGraphics.drawString(minecraft.font, consumeText, 2, 82, 0xFFFFFF, false); // White text for contrast
        } else {
            Component reuseText = Component.translatable("gui.mythicanvil.ritual.reusable_trigger");
            int reuseTextWidth = minecraft.font.width(reuseText);

            // Draw background rectangle for reusable trigger text
            guiGraphics.fill(0, 80, 2 + reuseTextWidth + 4, 92, 0x8000FF00); // Semi-transparent green background
            guiGraphics.fill(1, 81, 1 + reuseTextWidth + 3, 91, 0x40FFFFFF); // Light inner highlight

            guiGraphics.drawString(minecraft.font, reuseText, 2, 82, 0xFFFFFF, false); // White text for contrast
        }

        // Draw "result"
        Component resultText = Component.translatable("gui.mythicanvil.ritual.result");
        guiGraphics.drawString(minecraft.font, resultText, 137, 70, 0x404040, false);


        // Draw "Items on ground:" text above thrown items
        if (!recipe.getThrownItems().isEmpty()) {
            Component itemsText = Component.translatable("gui.mythicanvil.ritual.items_on_ground");
            int itemsTextWidth = minecraft.font.width(itemsText);
            guiGraphics.drawString(minecraft.font, itemsText, 88 - itemsTextWidth / 2, 5, 0x404040, false);
        }

        // Draw arrow indicating flow from trigger item to result
        guiGraphics.blit(ARROW_IMAGE, 45, 57, 0, 0, 34, 16, 34, 16);
        guiGraphics.blit(RIGHT_CLICK_IMAGE, 57, 53, 0, 0, 9, 13, 9, 13);
        guiGraphics.blit(UPSIDEDOWN_ARROW_IMAGE, 121, 86, 0, 0, 34, 16, 34, 16);
        guiGraphics.blit(LONG_ARROW_IMAGE, 83, 35, 0, 0, 9, 53, 9, 53);

    }
}

