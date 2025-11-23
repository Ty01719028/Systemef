package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.AttackListener;
import dev.potato.lucid.event.events.BlockBreakingListener;
import dev.potato.lucid.event.events.ItemUseListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_239;
import net.minecraft.class_3965;

public final class Prevent extends Module implements ItemUseListener, AttackListener, BlockBreakingListener {
   private final BooleanSetting doubleGlowstone = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Double Glowstone"), true)).setDescription(EncryptedString.of("Makes it so you can't charge the anchor again if it's already charged"));
   private final BooleanSetting glowstoneMisplace = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Glowstone Misplace"), true)).setDescription(EncryptedString.of("Makes it so you can only right-click with glowstone only when aiming at an anchor"));
   private final BooleanSetting anchorOnAnchor = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Anchor on Anchor"), true)).setDescription(EncryptedString.of("Makes it so you can't place an anchor on/next to another anchor unless charged"));
   private final BooleanSetting obiPunch = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Obi Punch"), true)).setDescription(EncryptedString.of("Makes it so you can crystal faster by not letting you left click/start breaking the obsidian"));
   private final BooleanSetting echestClick = (BooleanSetting)(new BooleanSetting(EncryptedString.of("E-chest click"), true)).setDescription(EncryptedString.of("Makes it so you can't click on e-chests with PvP items"));

   public Prevent() {
      super(EncryptedString.of("Prevent"), EncryptedString.of("Prevents you from certain actions"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.doubleGlowstone, this.glowstoneMisplace, this.anchorOnAnchor, this.obiPunch, this.echestClick});
   }

   public void onEnable() {
      this.eventManager.add(BlockBreakingListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(BlockBreakingListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      super.onDisable();
   }

   public void onAttack(AttackListener.AttackEvent event) {
      class_239 var3 = mc.field_1765;
      if (var3 instanceof class_3965) {
         class_3965 hit = (class_3965)var3;
         if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) && this.obiPunch.getValue() && mc.field_1724.method_24518(class_1802.field_8301)) {
            event.cancel();
         }
      }

   }

   public void onBlockBreaking(BlockBreakingListener.BlockBreakingEvent event) {
      class_239 var3 = mc.field_1765;
      if (var3 instanceof class_3965) {
         class_3965 hit = (class_3965)var3;
         if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) && this.obiPunch.getValue() && mc.field_1724.method_24518(class_1802.field_8301)) {
            event.cancel();
         }
      }

   }

   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      class_239 var3 = mc.field_1765;
      if (var3 instanceof class_3965) {
         class_3965 hit = (class_3965)var3;
         if (BlockUtils.isAnchorCharged(hit.method_17777()) && this.doubleGlowstone.getValue() && mc.field_1724.method_24518(class_1802.field_8801)) {
            event.cancel();
         }

         if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_23152) && this.glowstoneMisplace.getValue() && mc.field_1724.method_24518(class_1802.field_8801)) {
            event.cancel();
         }

         if (BlockUtils.isAnchorNotCharged(hit.method_17777()) && this.anchorOnAnchor.getValue() && mc.field_1724.method_24518(class_1802.field_23141)) {
            event.cancel();
         }

         if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10443) && this.echestClick.getValue() && (mc.field_1724.method_6047().method_7909() instanceof class_1829 || mc.field_1724.method_6047().method_7909() == class_1802.field_8301 || mc.field_1724.method_6047().method_7909() == class_1802.field_8281 || mc.field_1724.method_6047().method_7909() == class_1802.field_23141 || mc.field_1724.method_6047().method_7909() == class_1802.field_8801)) {
            event.cancel();
         }
      }

   }
}
