package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_239;
import net.minecraft.class_3965;

public final class DoubleAnchor extends Module implements TickListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 71, false)).setDescription(EncryptedString.of("Key that starts double anchoring"));
   private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting totemSlot = new NumberSetting(EncryptedString.of("Totem Slot"), 1.0D, 9.0D, 1.0D, 1.0D);
   private int delayCounter = 0;
   private int step = 0;
   private boolean isAnchoring = false;

   public DoubleAnchor() {
      super(EncryptedString.of("Double Anchor"), EncryptedString.of("Automatically Places 2 anchors"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.switchDelay, this.totemSlot, this.activateKey});
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
      if (mc.field_1755 == null) {
         if (mc.field_1724 != null) {
            if (this.hasRequiredItems()) {
               if (this.isAnchoring || this.checkActivationKey()) {
                  class_239 crosshairTarget = mc.field_1765;
                  if (mc.field_1765 instanceof class_3965 && !BlockUtils.isBlockAtPosition(((class_3965)crosshairTarget).method_17777(), class_2246.field_10124)) {
                     if (this.delayCounter < this.switchDelay.getValueInt()) {
                        ++this.delayCounter;
                     } else {
                        if (this.step == 0) {
                           InventoryUtils.selectItemFromHotbar(class_1802.field_23141);
                        } else if (this.step == 1) {
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                        } else if (this.step == 2) {
                           InventoryUtils.selectItemFromHotbar(class_1802.field_8801);
                        } else if (this.step == 3) {
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                        } else if (this.step == 4) {
                           InventoryUtils.selectItemFromHotbar(class_1802.field_23141);
                        } else if (this.step == 5) {
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                        } else if (this.step == 6) {
                           InventoryUtils.selectItemFromHotbar(class_1802.field_8801);
                        } else if (this.step == 7) {
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                        } else if (this.step == 8) {
                           InventoryUtils.setInvSlot(this.totemSlot.getValueInt() - 1);
                        } else if (this.step == 9) {
                           BlockUtils.interactWithBlock((class_3965)crosshairTarget, true);
                        } else if (this.step == 10) {
                           this.isAnchoring = false;
                           this.step = 0;
                           this.resetState();
                           return;
                        }

                        ++this.step;
                     }
                  } else {
                     this.isAnchoring = false;
                     this.resetState();
                  }
               }
            }
         }
      }
   }

   private boolean hasRequiredItems() {
      boolean b = false;
      boolean b2 = false;

      for(int i = 0; i < 9; ++i) {
         class_1799 getStack = mc.field_1724.method_31548().method_5438(i);
         if (getStack.method_7909().equals(class_1802.field_23141)) {
            b = true;
         }

         if (getStack.method_7909().equals(class_1802.field_8801)) {
            b2 = true;
         }
      }

      return b && b2;
   }

   private boolean checkActivationKey() {
      int d = this.activateKey.getKey();
      if (d != -1 && KeyUtils.isKeyPressed(d)) {
         return this.isAnchoring = true;
      } else {
         this.resetState();
         return false;
      }
   }

   private void resetState() {
      this.delayCounter = 0;
   }

   public boolean isAnchoringActive() {
      return this.isAnchoring;
   }
}
