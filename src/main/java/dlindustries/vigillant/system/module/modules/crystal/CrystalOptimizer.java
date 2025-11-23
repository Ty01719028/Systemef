package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.PacketSendListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.WorldUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1511;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_3966;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2824.class_5908;

public final class CrystalOptimizer extends Module implements PacketSendListener {
   public CrystalOptimizer() {
      super(EncryptedString.of("Crystal Optimizer"), EncryptedString.of("Makes your crystals disappear faster client-side so you can place crystals faster"), -1, Category.CPVP);
   }

   public void onEnable() {
      this.eventManager.add(PacketSendListener.class, this);
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(PacketSendListener.class, this);
      super.onDisable();
   }

   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2824) {
         class_2824 interactPacket = (class_2824)var3;
         interactPacket.method_34209(new class_5908(this) {
            public void method_34219(class_1268 hand) {
            }

            public void method_34220(class_1268 hand, class_243 pos) {
            }

            public void method_34218() {
               if (CrystalOptimizer.mc.field_1765 != null) {
                  if (CrystalOptimizer.mc.field_1765.method_17783() == class_240.field_1331) {
                     class_239 var2 = CrystalOptimizer.mc.field_1765;
                     if (var2 instanceof class_3966) {
                        class_3966 hit = (class_3966)var2;
                        if (hit.method_17782() instanceof class_1511) {
                           class_1293 weakness = CrystalOptimizer.mc.field_1724.method_6112(class_1294.field_5911);
                           class_1293 strength = CrystalOptimizer.mc.field_1724.method_6112(class_1294.field_5910);
                           if (weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !WorldUtils.isTool(CrystalOptimizer.mc.field_1724.method_6047())) {
                              return;
                           }

                           hit.method_17782().method_5768();
                           hit.method_17782().method_31745(class_5529.field_26998);
                           hit.method_17782().method_36209();
                        }
                     }
                  }

               }
            }
         });
      }

   }
}
