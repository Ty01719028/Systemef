package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.mixin.J;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.ModeSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1802;
import net.minecraft.class_437;
import net.minecraft.class_490;

public class AInvTotem extends Module implements TickListener {
   private final ModeSetting<AInvTotem.Mode> mode;
   private final NumberSetting swapDelay;
   private final BooleanSetting offhand;
   private final NumberSetting totemSlot;
   private final BooleanSetting workOnKey;
   private final KeybindSetting activateKey;
   private int swapClock;

   public AInvTotem() {
      super(EncryptedString.of("Asteria Inv Totem"), EncryptedString.of("Automatically put totems in slots (Asteria inv totem"), -1, Category.CPVP);
      this.mode = new ModeSetting(EncryptedString.of("Mode"), AInvTotem.Mode.NORMAL, AInvTotem.Mode.class);
      this.swapDelay = new NumberSetting(EncryptedString.of("Swap Delay"), 0.0D, 10.0D, 0.0D, 1.0D);
      this.offhand = new BooleanSetting(EncryptedString.of("Offhand"), true);
      this.totemSlot = new NumberSetting(EncryptedString.of("Totem Slot"), 1.0D, 9.0D, 1.0D, 1.0D);
      this.workOnKey = new BooleanSetting(EncryptedString.of("Work On Key"), false);
      this.activateKey = new KeybindSetting(EncryptedString.of("Activate Key"), -1, false);
      this.swapClock = 0;
      this.addSettings(new Setting[]{this.mode, this.swapDelay, this.offhand, this.totemSlot, this.workOnKey, this.activateKey});
   }

   public void onEnable() {
      Lucid.INSTANCE.getEventManager().add(TickListener.class, this);
      this.swapClock = 0;
   }

   public void onDisable() {
      Lucid.INSTANCE.getEventManager().remove(TickListener.class, this);
      this.swapClock = 0;
   }

   public boolean searchTotems() {
      class_1661 inventory = mc.field_1724.method_31548();
      return InventoryUtils.countItem(class_1802.field_8288) > 0 && (!inventory.method_5438(this.totemSlot.getValueInt() - 1).method_31574(class_1802.field_8288) || !inventory.method_5438(40).method_31574(class_1802.field_8288) && this.offhand.getValue());
   }

   public void onTick() {
      if (!this.workOnKey.getValue() || KeyUtils.isKeyPressed(this.activateKey.getKey())) {
         class_437 var2 = mc.field_1755;
         if (var2 instanceof class_490) {
            class_490 inventoryScreen = (class_490)var2;
            class_1735 focusedSlot = ((J)inventoryScreen).getFocusedSlot();
            if (focusedSlot == null) {
               return;
            }

            class_1661 inventory = mc.field_1724.method_31548();
            if (!focusedSlot.method_7677().method_31574(class_1802.field_8288)) {
               return;
            }

            int targetSlot = -1;
            if (this.workOnKey.getValue() && this.offhand.getValue() && !inventory.method_5438(40).method_31574(class_1802.field_8288)) {
               targetSlot = 40;
            } else if (!inventory.method_5438(this.totemSlot.getValueInt() - 1).method_31574(class_1802.field_8288)) {
               targetSlot = this.totemSlot.getValueInt() - 1;
            } else if (this.offhand.getValue() && !inventory.method_5438(40).method_31574(class_1802.field_8288)) {
               targetSlot = 40;
            }

            if (targetSlot == -1) {
               return;
            }

            if (this.swapClock < this.swapDelay.getValueInt()) {
               ++this.swapClock;
               return;
            }

            mc.field_1761.method_2906(((class_1723)inventoryScreen.method_17577()).field_7763, focusedSlot.method_34266(), targetSlot, class_1713.field_7791, mc.field_1724);
            this.swapClock = 0;
         }

      }
   }

   public static enum Mode {
      NORMAL,
      LEGIT;

      // $FF: synthetic method
      private static AInvTotem.Mode[] $values() {
         return new AInvTotem.Mode[]{NORMAL, LEGIT};
      }
   }
}
