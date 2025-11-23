package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.KeyUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2885;
import net.minecraft.class_3965;

public class AirAnchor extends Module implements TickListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 1, false)).setDescription(EncryptedString.of("Key that activates air anchoring"));
   private class_2338 currentBlockPos;
   private int count;

   public AirAnchor() {
      super(EncryptedString.of("AirAnchor"), EncryptedString.of("Makes MINDBLOWING Minecraft Crystal PVP Method easier!"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.activateKey});
   }

   public void onEnable() {
      Lucid.INSTANCE.getEventManager().add(TickListener.class, this);
      this.currentBlockPos = null;
      this.count = 0;
   }

   public void onDisable() {
      Lucid.INSTANCE.getEventManager().remove(TickListener.class, this);
      this.currentBlockPos = null;
      this.count = 0;
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         if (KeyUtils.isKeyPressed(this.activateKey.getKey()) && mc.field_1724 != null && mc.field_1724.method_6047().method_31574(class_1802.field_23141)) {
            class_239 var2 = mc.field_1765;
            if (var2 instanceof class_3965) {
               class_3965 blockHitResult = (class_3965)var2;
               if (BlockUtils.isAnchorCharged(blockHitResult.method_17777())) {
                  if (blockHitResult.method_17777().equals(this.currentBlockPos)) {
                     if (this.count >= 1) {
                        return;
                     }
                  } else {
                     this.currentBlockPos = blockHitResult.method_17777();
                     this.count = 0;
                  }

                  mc.method_1562().method_52787(new class_2885(class_1268.field_5808, blockHitResult, 0));
                  ++this.count;
               }
            }
         }

      }
   }
}
