package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_304;
import net.minecraft.class_3965;

public class SafeAnchor extends Module implements TickListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 1, false)).setDescription(EncryptedString.of("Key that activates safe anchoring"));
   private int originalSlot = -1;
   private boolean shouldSwitchBack = false;

   public SafeAnchor() {
      super(EncryptedString.of("SafeAnchor"), EncryptedString.of("Anchor without taking any damage"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.activateKey});
   }

   public void onEnable() {
      Lucid.INSTANCE.getEventManager().add(TickListener.class, this);
   }

   public void onDisable() {
      Lucid.INSTANCE.getEventManager().remove(TickListener.class, this);
      this.unpress();
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         if (!KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            this.unpress();
         } else if (!(mc.field_1724.method_36455() < 15.0F)) {
            if (!mc.field_1724.method_6115()) {
               class_239 cr = mc.field_1765;
               if (cr instanceof class_3965) {
                  class_3965 hit = (class_3965)cr;
                  class_2338 pos = hit.method_17777();
                  if (BlockUtils.isAnchorNotCharged(pos)) {
                     class_1269 actionResult;
                     if (mc.field_1724.method_24518(class_1802.field_8801)) {
                        actionResult = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
                        if (actionResult.method_23665() && class_1269.field_21466.method_23666()) {
                           mc.field_1724.method_6104(class_1268.field_5808);
                        }

                        return;
                     }

                     if (this.originalSlot == -1) {
                        this.originalSlot = mc.field_1724.method_31548().field_7545;
                     }

                     InventoryUtils.selectItemFromHotbar(class_1802.field_8801);
                     actionResult = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
                     if (actionResult.method_23665() && class_1269.field_21466.method_23666()) {
                        mc.field_1724.method_6104(class_1268.field_5808);
                     }

                     if (BlockUtils.isAnchorCharged(pos)) {
                        this.setPressed(mc.field_1690.field_1832, true);
                        this.shouldSwitchBack = true;
                     }
                  } else if (BlockUtils.isBlock(pos, class_2246.field_10171)) {
                     InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
                     if (mc.field_1724.method_6047().method_31574(class_1802.field_8288)) {
                        this.unpress();
                     } else {
                        this.unpress();
                     }
                  }
               }

               if (this.shouldSwitchBack && this.originalSlot != -1 && !mc.field_1690.field_1832.method_1434()) {
                  mc.field_1724.method_31548().field_7545 = this.originalSlot;
                  this.originalSlot = -1;
                  this.shouldSwitchBack = false;
               }

            }
         }
      }
   }

   private void unpress() {
      this.setPressed(mc.field_1690.field_1832, false);
   }

   private void setPressed(class_304 key, boolean pressed) {
      key.method_23481(pressed);
   }
}
