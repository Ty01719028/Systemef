package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.ModeSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.FakeInvScreen;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.TimerUtils;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_490;

public final class AutoInventoryTotem extends Module implements TickListener {
   private final ModeSetting<AutoInventoryTotem.Mode> mode;
   private final NumberSetting delay;
   private final BooleanSetting hotbar;
   private final NumberSetting totemSlot;
   private final BooleanSetting autoSwitch;
   private final BooleanSetting forceTotem;
   private final BooleanSetting autoOpen;
   private final NumberSetting stayOpenFor;
   int clock;
   int closeClock;
   TimerUtils openTimer;
   TimerUtils closeTimer;

   public AutoInventoryTotem() {
      super(EncryptedString.of("Auto Inventory Totem"), EncryptedString.of("Automatically equips a totem in your offhand and main hand if empty"), -1, Category.CPVP);
      this.mode = (ModeSetting)(new ModeSetting(EncryptedString.of("Mode"), AutoInventoryTotem.Mode.Blatant, AutoInventoryTotem.Mode.class)).setDescription(EncryptedString.of("Whether to randomize the toteming pattern or no"));
      this.delay = new NumberSetting(EncryptedString.of("Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
      this.hotbar = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Hotbar"), true)).setDescription(EncryptedString.of("Puts a totem in your hotbar as well, if enabled (Setting below will work if this is enabled)"));
      this.totemSlot = (NumberSetting)(new NumberSetting(EncryptedString.of("Totem Slot"), 1.0D, 9.0D, 1.0D, 1.0D)).setDescription(EncryptedString.of("Your preferred totem slot"));
      this.autoSwitch = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Auto Switch"), false)).setDescription(EncryptedString.of("Switches to totem slot when going inside the inventory"));
      this.forceTotem = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Force Totem"), false)).setDescription(EncryptedString.of("Puts the totem in the slot, regardless if its space is taken up by something else"));
      this.autoOpen = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Auto Open"), false)).setDescription(EncryptedString.of("Automatically opens and closes the inventory for you"));
      this.stayOpenFor = new NumberSetting(EncryptedString.of("Stay Open For"), 0.0D, 20.0D, 0.0D, 1.0D);
      this.clock = -1;
      this.closeClock = -1;
      this.openTimer = new TimerUtils();
      this.closeTimer = new TimerUtils();
      this.addSettings(new Setting[]{this.mode, this.delay, this.hotbar, this.totemSlot, this.autoSwitch, this.forceTotem, this.autoOpen, this.stayOpenFor});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.clock = -1;
      this.closeClock = -1;
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   public void onTick() {
      if (this.shouldOpenScreen() && this.autoOpen.getValue()) {
         mc.method_1507(new FakeInvScreen(mc.field_1724));
      }

      if (!(mc.field_1755 instanceof class_490) && !(mc.field_1755 instanceof FakeInvScreen)) {
         this.clock = -1;
         this.closeClock = -1;
      } else {
         if (this.clock == -1) {
            this.clock = this.delay.getValueInt();
         }

         if (this.closeClock == -1) {
            this.closeClock = this.stayOpenFor.getValueInt();
         }

         if (this.clock > 0) {
            --this.clock;
         }

         class_1661 inventory = mc.field_1724.method_31548();
         if (this.autoSwitch.getValue()) {
            inventory.field_7545 = this.totemSlot.getValueInt() - 1;
         }

         if (this.clock <= 0) {
            if (((class_1799)inventory.field_7544.get(0)).method_7909() != class_1802.field_8288) {
               int slot = this.mode.isMode(AutoInventoryTotem.Mode.Blatant) ? InventoryUtils.findTotemSlot() : InventoryUtils.findRandomTotemSlot();
               if (slot != -1) {
                  mc.field_1761.method_2906(((class_1723)((class_490)mc.field_1755).method_17577()).field_7763, slot, 40, class_1713.field_7791, mc.field_1724);
                  return;
               }
            }

            if (this.hotbar.getValue()) {
               class_1799 mainHand = mc.field_1724.method_6047();
               if (mainHand.method_7960() || this.forceTotem.getValue() && mainHand.method_7909() != class_1802.field_8288) {
                  int slot = this.mode.isMode(AutoInventoryTotem.Mode.Blatant) ? InventoryUtils.findTotemSlot() : InventoryUtils.findRandomTotemSlot();
                  if (slot != -1) {
                     mc.field_1761.method_2906(((class_1723)((class_490)mc.field_1755).method_17577()).field_7763, slot, inventory.field_7545, class_1713.field_7791, mc.field_1724);
                     return;
                  }
               }
            }

            if (this.shouldCloseScreen() && this.autoOpen.getValue()) {
               if (this.closeClock != 0) {
                  --this.closeClock;
                  return;
               }

               mc.field_1755.method_25419();
               this.closeClock = this.stayOpenFor.getValueInt();
            }
         }

      }
   }

   public boolean shouldCloseScreen() {
      if (this.hotbar.getValue()) {
         return mc.field_1724.method_31548().method_5438(this.totemSlot.getValueInt() - 1).method_7909() == class_1802.field_8288 && mc.field_1724.method_6079().method_7909() == class_1802.field_8288 && mc.field_1755 instanceof FakeInvScreen;
      } else {
         return mc.field_1724.method_6079().method_7909() == class_1802.field_8288 && mc.field_1755 instanceof FakeInvScreen;
      }
   }

   public boolean shouldOpenScreen() {
      if (this.hotbar.getValue()) {
         return (mc.field_1724.method_6079().method_7909() != class_1802.field_8288 || mc.field_1724.method_31548().method_5438(this.totemSlot.getValueInt() - 1).method_7909() != class_1802.field_8288) && !(mc.field_1755 instanceof FakeInvScreen) && InventoryUtils.countItemExceptHotbar((item) -> {
            return item == class_1802.field_8288;
         }) != 0;
      } else {
         return mc.field_1724.method_6079().method_7909() != class_1802.field_8288 && !(mc.field_1755 instanceof FakeInvScreen) && InventoryUtils.countItemExceptHotbar((item) -> {
            return item == class_1802.field_8288;
         }) != 0;
      }
   }

   public static enum Mode {
      Blatant,
      Random;

      // $FF: synthetic method
      private static AutoInventoryTotem.Mode[] $values() {
         return new AutoInventoryTotem.Mode[]{Blatant, Random};
      }
   }
}
