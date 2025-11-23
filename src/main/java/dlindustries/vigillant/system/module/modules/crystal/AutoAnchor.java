package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public class AutoAnchor extends Module implements TickListener {
   private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting placeDelay = new NumberSetting(EncryptedString.of("Place Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting cooldown = new NumberSetting(EncryptedString.of("Cooldown"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting itemSwap = new NumberSetting(EncryptedString.of("Item Swap"), 1.0D, 9.0D, 1.0D, 1.0D);
   private final BooleanSetting chargeOnly = new BooleanSetting(EncryptedString.of("Charge Only"), false);
   private boolean hasAnchored = false;
   private int switchClock = 0;
   private int placeClock = 0;
   private int cooldownClock = 0;

   public AutoAnchor() {
      super(EncryptedString.of("AutoAnchor"), EncryptedString.of("Automatically anchors"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.switchDelay, this.placeDelay, this.cooldown, this.itemSwap, this.chargeOnly});
   }

   public void onEnable() {
      Lucid.INSTANCE.getEventManager().add(TickListener.class, this);
      this.reset();
   }

   public void onDisable() {
      Lucid.INSTANCE.getEventManager().remove(TickListener.class, this);
      this.reset();
   }

   private void reset() {
      this.hasAnchored = false;
      this.switchClock = 0;
      this.placeClock = 0;
      this.cooldownClock = 0;
   }

   public void onTick() {
      if (mc.field_1687 != null && mc.field_1724 != null && mc.field_1755 == null) {
         if (this.hasAnchored) {
            if (this.cooldownClock < this.cooldown.getValueInt()) {
               ++this.cooldownClock;
               return;
            }

            this.reset();
         }

         if (GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) != 1) {
            this.reset();
         } else if (!mc.field_1724.method_6115()) {
            class_239 var2 = mc.field_1765;
            if (var2 instanceof class_3965) {
               class_3965 hit = (class_3965)var2;
               if (hit.method_17783() == class_240.field_1333) {
                  return;
               }

               class_2338 pos = hit.method_17777();
               if (BlockUtils.isBlock(pos, class_2246.field_23152)) {
                  mc.field_1690.field_1904.method_23481(false);
                  class_1269 actionResult2;
                  if (BlockUtils.isAnchorNotCharged(pos)) {
                     if (!mc.field_1724.method_24518(class_1802.field_8801)) {
                        if (this.switchClock < this.switchDelay.getValueInt()) {
                           ++this.switchClock;
                           return;
                        }

                        InventoryUtils.selectItemFromHotbar(class_1802.field_8801);
                        this.switchClock = 0;
                     }

                     if (this.placeClock < this.placeDelay.getValueInt()) {
                        ++this.placeClock;
                        return;
                     }

                     actionResult2 = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
                     if (actionResult2.method_23665() && actionResult2.method_23666()) {
                        mc.field_1724.method_6104(class_1268.field_5808);
                     }

                     this.placeClock = 0;
                  }

                  if (BlockUtils.isAnchorCharged(pos) && !this.chargeOnly.getValue()) {
                     if (mc.field_1724.method_31548().field_7545 != this.itemSwap.getValueInt() - 1) {
                        if (this.switchClock < this.switchDelay.getValueInt()) {
                           ++this.switchClock;
                           return;
                        }

                        mc.field_1724.method_31548().field_7545 = this.itemSwap.getValueInt() - 1;
                        this.switchClock = 0;
                     }

                     if (this.placeClock < this.placeDelay.getValueInt()) {
                        ++this.placeClock;
                        return;
                     }

                     actionResult2 = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
                     if (actionResult2.method_23665() && actionResult2.method_23666()) {
                        mc.field_1724.method_6104(class_1268.field_5808);
                     }

                     this.placeClock = 0;
                     this.hasAnchored = true;
                  }
               }
            }

         }
      }
   }
}
