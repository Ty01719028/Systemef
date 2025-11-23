package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;

public final class AutoTotem extends Module implements TickListener {
   private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0D, 1000.0D, 250.0D, 10.0D);
   private final NumberSetting mainhandSlotSetting = new NumberSetting(EncryptedString.of("Mainhand Slot"), 1.0D, 9.0D, 1.0D, 1.0D);
   private final BooleanSetting legitMode = new BooleanSetting(EncryptedString.of("Legit Mode"), false);
   private final BooleanSetting blatantMode = new BooleanSetting(EncryptedString.of("Blatant Mode"), false);
   private long lastSwitchTime = 0L;
   private boolean inventoryOpenedLastTick = false;

   public AutoTotem() {
      super(EncryptedString.of("AutoTotem"), EncryptedString.of("Auto totem with instant totem in slot 5 on inventory open."), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.delay, this.mainhandSlotSetting, this.legitMode, this.blatantMode});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   public void onTick() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_437 currentScreen = mc.field_1755;
         boolean inventoryOpen = currentScreen instanceof class_465 && currentScreen.method_25440().getString().equalsIgnoreCase("Inventory");
         if (inventoryOpen && !this.inventoryOpenedLastTick && !this.hasTotemInSlot(4)) {
            int totemSlot = this.findTotemSlot();
            if (totemSlot != -1) {
               this.swapToSlot(totemSlot, 4);
               this.lastSwitchTime = System.currentTimeMillis();
            }
         }

         this.inventoryOpenedLastTick = inventoryOpen;
         if (!this.legitMode.getValue() || currentScreen == null || currentScreen instanceof class_408) {
            long now = System.currentTimeMillis();
            if (this.blatantMode.getValue() || !((double)(now - this.lastSwitchTime) < this.delay.getValue())) {
               boolean didSwap = false;
               int configuredSlot;
               if (mc.field_1724.method_6079().method_7909() != class_1802.field_8288) {
                  configuredSlot = this.findTotemSlot();
                  if (configuredSlot != -1) {
                     this.swapToSlot(configuredSlot, 40);
                     didSwap = true;
                  }
               }

               configuredSlot = (int)this.mainhandSlotSetting.getValue() - 1;
               class_1799 mainhandStack = mc.field_1724.method_31548().method_5438(configuredSlot);
               if (mainhandStack.method_7909() != class_1802.field_8288) {
                  int totemSlot = this.findTotemSlot();
                  if (totemSlot != -1) {
                     this.swapToSlot(totemSlot, configuredSlot);
                     didSwap = true;
                  }
               }

               if (didSwap) {
                  this.lastSwitchTime = now;
               }

            }
         }
      }
   }

   private boolean hasTotemInSlot(int slot) {
      return mc.field_1724.method_31548().method_5438(slot).method_7909() == class_1802.field_8288;
   }

   private int findTotemSlot() {
      for(int i = 0; i < 36; ++i) {
         class_1799 stack = mc.field_1724.method_31548().method_5438(i);
         if (!stack.method_7960() && stack.method_7909() == class_1802.field_8288) {
            return i;
         }
      }

      return -1;
   }

   private void swapToSlot(int fromSlot, int toSlot) {
      int mappedFrom = fromSlot < 9 ? fromSlot + 36 : fromSlot;
      mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, mappedFrom, toSlot, class_1713.field_7791, mc.field_1724);
   }
}
