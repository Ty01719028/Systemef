package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.mixin.J;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1802;
import net.minecraft.class_437;
import net.minecraft.class_490;

public final class HoverTotem extends Module implements TickListener {
   private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final BooleanSetting hotbar = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Hotbar"), true)).setDescription(EncryptedString.of("Puts a totem in your hotbar as well, if enabled (Setting below will work if this is enabled)"));
   private final NumberSetting slot = (NumberSetting)(new NumberSetting(EncryptedString.of("Totem Slot"), 1.0D, 9.0D, 1.0D, 1.0D)).setDescription(EncryptedString.of("Your preferred totem slot"));
   private final BooleanSetting autoSwitch = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Auto Switch"), false)).setDescription(EncryptedString.of("Switches to totem slot when going inside the inventory"));
   private int clock;

   public HoverTotem() {
      super(EncryptedString.of("Hover Totem"), EncryptedString.of("Equips a totem in your totem and offhand slots if a totem is hovered"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.delay, this.hotbar, this.slot, this.autoSwitch});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.clock = 0;
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   public void onTick() {
      class_437 var2 = mc.field_1755;
      if (var2 instanceof class_490) {
         class_490 inv = (class_490)var2;
         class_1735 hoveredSlot = ((J)inv).getFocusedSlot();
         if (this.autoSwitch.getValue()) {
            mc.field_1724.method_31548().field_7545 = this.slot.getValueInt() - 1;
         }

         if (hoveredSlot != null) {
            int slot = hoveredSlot.method_34266();
            if (slot > 35) {
               return;
            }

            int totem = this.slot.getValueInt() - 1;
            if (hoveredSlot.method_7677().method_7909() == class_1802.field_8288) {
               if (this.hotbar.getValue() && mc.field_1724.method_31548().method_5438(totem).method_7909() != class_1802.field_8288) {
                  if (this.clock > 0) {
                     --this.clock;
                     return;
                  }

                  mc.field_1761.method_2906(((class_1723)inv.method_17577()).field_7763, slot, totem, class_1713.field_7791, mc.field_1724);
                  this.clock = this.delay.getValueInt();
               } else if (!mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
                  if (this.clock > 0) {
                     --this.clock;
                     return;
                  }

                  mc.field_1761.method_2906(((class_1723)inv.method_17577()).field_7763, slot, 40, class_1713.field_7791, mc.field_1724);
                  this.clock = this.delay.getValueInt();
               }
            }
         }
      } else {
         this.clock = this.delay.getValueInt();
      }

   }
}
