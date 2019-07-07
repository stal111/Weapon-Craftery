package com.stal111.weapon_craftery.item;

import com.google.common.collect.Multimap;
import com.stal111.weapon_craftery.Main;
import com.stal111.weapon_craftery.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WebBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Random;

public class ModSwordItem extends Item {

    private final IItemTier tier;

    private final float attackSpeed;
    private final float attackDamage;

    public ModSwordItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(properties.maxStackSize(1));
        this.addPropertyOverride(new ResourceLocation("level"), (stack, world, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                return (float) this.getLevel(stack) / 10;
            }
        });

        this.tier = tier;
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage + tier.getAttackDamage();
    }

    public IItemTier getTier() {
        return this.tier;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return stack.hasTag() ? tier.getMaxUses() + this.getWeaponRarityDurabilityBonus(stack) : 0;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return this.tier.getEnchantability() + this.getWeaponRarityEnchantabilityBonus(stack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.getTag() != null) {
            return new TranslationTextComponent(this.getName().getString() + " ").appendSibling(new TranslationTextComponent("rarity." + Main.MOD_ID + "." + this.getWeaponRarity(stack).toString().toLowerCase()).applyTextStyle(this.getWeaponRarityColor(stack)));

        } else {
            return super.getDisplayName(stack);
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return this.tier.getRepairMaterial().test(p_82789_2_) || super.getIsRepairable(p_82789_1_, p_82789_2_);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getTag() == null) {
            this.createTag(stack, this.getRandomWeaponRarity());
        } else if (stack.getTag().getString("rarity") == "???") {
            this.createTag(stack, stack.getTag().getInt("level"), this.getRandomWeaponRarity());
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (group == Main.WEAPON_CRAFTERY) {
            for (int i = 1; i <= this.getMaxLevel(); i++) {
                ItemStack stack = new ItemStack(this);
                this.createTag(stack, "???");
                stack.getTag().putInt("level", i);
                items.add(stack);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getTag() != null) {
            if (!this.isMaxLevel(stack)) {
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".level").applyTextStyle(this.getWeaponRarityColor(stack)).appendText("§7: " + this.getLevel(stack)));
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".xp").applyTextStyle(TextFormatting.GRAY).appendText(": " + this.getXP(stack) + "/" + this.getNeededXP(stack)));
            } else {
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".level").applyTextStyle(this.getWeaponRarityColor(stack)).appendText("§7: " + this.getLevel(stack) + " §2(MAX)"));
            }
            tooltip.add(new TranslationTextComponent(""));

            if (this.getWeaponRarityEnchantabilityBonus(stack) != 0) {
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".enchantability").applyTextStyle(TextFormatting.GRAY).appendText(": " + this.tier.getEnchantability()).appendText(" + §" + this.getWeaponRarityColorAsChar(stack) + this.getWeaponRarityEnchantabilityBonus(stack)));
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".durability").applyTextStyle(TextFormatting.GRAY).appendText(": " + (this.getMaxDamage(stack) - this.getDamage(stack)) + "/" + this.tier.getMaxUses()).appendText(" + §" + this.getWeaponRarityColorAsChar(stack) + this.getWeaponRarityDurabilityBonus(stack)));
            } else {
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".enchantability").applyTextStyle(TextFormatting.GRAY).appendText("§7: " + this.tier.getEnchantability()));
                tooltip.add(new TranslationTextComponent("tooltip." + Main.MOD_ID + ".durability").applyTextStyle(TextFormatting.GRAY).appendText(": " + (this.getMaxDamage(stack) - this.getDamage(stack)) + "/" + this.tier.getMaxUses()));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        final Block lvt_3_1_ = state.getBlock();
        if (lvt_3_1_ instanceof WebBlock) {
            return 15.0f;
        }
        final Material lvt_4_1_ = state.getMaterial();
        if (lvt_4_1_ == Material.PLANTS || lvt_4_1_ == Material.TALL_PLANTS || lvt_4_1_ == Material.CORAL || state.isIn(BlockTags.LEAVES) || lvt_4_1_ == Material.GOURD) {
            return 1.5f;
        }
        return 1.0f;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos,
                                    LivingEntity entityLiving) {
        if (state.getBlockHardness(world, pos) != 0.0f) {
            stack.damageItem(2, entityLiving, p_220044_0_ -> p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(BlockState block) {
        return block.getBlock() instanceof WebBlock;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        System.out.println(stack.getTag());
        return super.onItemRightClick(world, player, hand);
    }

    private String getWeaponRarity(ItemStack stack) {
        return stack.getTag().getString("rarity");
    }

    private String getRandomWeaponRarity() {
        int i = new Random().nextInt(6);
        switch (i) {
            case 1:
                return "common";
            case 2:
                return "uncommon";
            case 3:
                return "rare";
            case 4:
                return "epic";
            case 5:
                return "legendary";
        }
        return "common";
    }

    private TextFormatting getWeaponRarityColor(ItemStack stack) {
        switch (stack.getTag().getString("rarity")) {
            case "common":
                return TextFormatting.WHITE;
            case "uncommon":
                return TextFormatting.YELLOW;
            case "rare":
                return TextFormatting.AQUA;
            case "epic":
                return TextFormatting.LIGHT_PURPLE;
            case "legendary":
                return TextFormatting.GOLD;
        }
        return TextFormatting.WHITE;
    }

    private char getWeaponRarityColorAsChar(ItemStack stack) {
        switch (stack.getTag().getString("rarity")) {
            case "common":
                return 'f';
            case "uncommon":
                return 'e';
            case "rare":
                return 'b';
            case "epic":
                return 'd';
            case "legendary":
                return '6';
        }
        return 'f';
    }

    private int getWeaponRarityEnchantabilityBonus(ItemStack stack) {
        String string = this.getWeaponRarity(stack);

        switch (string) {
            case "uncommon":
                return 2;
            case "rare":
                return 5;
            case "epic":
                return 8;
            case "legendary":
                return 12;
            default:
                return 0;
        }
    }

    private int getWeaponRarityDurabilityBonus(ItemStack stack) {
        String string = this.getWeaponRarity(stack);

        switch (string) {
            case "uncommon":
                return 7;
            case "rare":
                return 13;
            case "epic":
                return 19;
            case "legendary":
                return 27;
            default:
                return 0;
        }
    }

    private int getLevel(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt("level") : 1;
    }

    private void addLevel(ItemStack stack) {
        stack.getTag().putInt("level", this.getLevel(stack) + 1);
    }

    private boolean isMaxLevel(ItemStack stack) {
        return this.getLevel(stack) == this.getMaxLevel();
    }

    private int getMaxLevel() {
        return 3;
    }

    private int getXP(ItemStack stack) {
        return stack.getTag().getInt("xp");
    }

    private void addXP(ItemStack stack, int count) {
        stack.getTag().putInt("xp", this.getXP(stack) + count);

        if (this.getXP(stack) >= this.getNeededXP(stack)) {
            this.removeXP(stack, this.getNeededXP(stack));
            this.addLevel(stack);
        }
    }

    private void removeXP(ItemStack stack, int count) {
        if (this.getXP(stack) - count <= 0) {
            stack.getTag().putInt("xp", 0);
        } else {
            stack.getTag().putInt("xp", this.getXP(stack) - count);
        }
    }

    private int getNeededXP(ItemStack stack) {
        if (this.getWeaponMaterialAsInt(stack.getItem()) == 3) {
            return 10 * this.getLevel(stack) * this.getWeaponMaterialAsInt(stack.getItem()) + 5 * this.getLevel(stack);
        } else if (this.getWeaponMaterialAsInt(stack.getItem()) == 4) {
            return 20 * this.getLevel(stack) * this.getWeaponMaterialAsInt(stack.getItem()) + 5 * this.getLevel(stack);
        } else {
            return 5 * this.getLevel(stack) * this.getWeaponMaterialAsInt(stack.getItem());
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.getHealth() <= 0) {
            if (!this.isMaxLevel(stack)) {
                if (target instanceof MonsterEntity) {
                    this.addXP(stack, 2);
                } else {
                    this.addXP(stack, 1);
                }
            }
        }
        stack.damageItem(1, attacker, p_220045_0_ -> p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    private String getWeaponMaterial(Item item) {
        if (item == ModItems.oak_sword || item == ModItems.spruce_sword || item == ModItems.birch_sword || item == ModItems.jungle_sword || item == ModItems.acacia_sword || item == ModItems.dark_oak_sword) {
            return "wood";
        } else if (item == ModItems.stone_sword) {
            return "stone";
        } else if (item == ModItems.iron_sword) {
            return "iron";
        } else if (item == ModItems.diamond_sword) {
            return "diamond";
        } else {
            return "gold";
        }
    }

    private int getWeaponMaterialAsInt(Item item) {
        if (item == ModItems.oak_sword || item == ModItems.spruce_sword || item == ModItems.birch_sword || item == ModItems.jungle_sword || item == ModItems.acacia_sword || item == ModItems.dark_oak_sword) {
            return 1;
        } else if (item == ModItems.stone_sword) {
            return 2;
        } else if (item == ModItems.iron_sword) {
            return 3;
        } else if (item == ModItems.diamond_sword) {
            return 4;
        } else {
            return 5;
        }
    }

    private void createTag(ItemStack stack, String rarity) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("rarity", rarity);
        nbt.putInt("level", 1);
        nbt.putInt("xp", 0);
        stack.setTag(nbt);
    }

    private void createTag(ItemStack stack, int level, String rarity) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("rarity", rarity);
        nbt.putInt("level", level);
        nbt.putInt("xp", 0);
        stack.setTag(nbt);
    }

    private float getLevelDamageBonus(ItemStack stack) {
        return 0.5F * (this.getLevel(stack) - 1);
    }

    private float getLevelSpeedBonus(ItemStack stack) {
        return 0.05F * (this.getLevel(stack) - 1);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        final Multimap<String, AttributeModifier> lvt_2_1_ = super.getAttributeModifiers(slot);
        if (slot == EquipmentSlotType.MAINHAND) {
            lvt_2_1_.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    (@Nullable AttributeModifier) new AttributeModifier(ModSwordItem.ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage + this.getLevelDamageBonus(stack), AttributeModifier.Operation.ADDITION));
            lvt_2_1_.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    (@Nullable AttributeModifier) new AttributeModifier(ModSwordItem.ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed + this.getLevelSpeedBonus(stack), AttributeModifier.Operation.ADDITION));
        }
        return lvt_2_1_;
    }
}
