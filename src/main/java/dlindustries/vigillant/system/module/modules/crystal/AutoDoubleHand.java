package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.HudListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.CrystalUtils;
import dev.potato.lucid.utils.DamageUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.RotationUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.class_1511;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class AutoDoubleHand extends Module implements HudListener {
   private final BooleanSetting stopOnCrystal = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Stop On Crystal"), false)).setDescription(EncryptedString.of("Stops while Auto Crystal is running"));
   private final BooleanSetting checkShield = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Check Shield"), false)).setDescription(EncryptedString.of("Checks if you're blocking with a shield"));
   private final BooleanSetting onPop = (BooleanSetting)(new BooleanSetting(EncryptedString.of("On Pop"), false)).setDescription(EncryptedString.of("Switches to a totem if you pop"));
   private final BooleanSetting onHealth = (BooleanSetting)(new BooleanSetting(EncryptedString.of("On Health"), false)).setDescription(EncryptedString.of("Switches to totem if low on health"));
   private final BooleanSetting predict = new BooleanSetting(EncryptedString.of("Predict Damage"), true);
   private final NumberSetting health = (NumberSetting)(new NumberSetting(EncryptedString.of("Health"), 1.0D, 20.0D, 2.0D, 1.0D)).setDescription(EncryptedString.of("Health to trigger at"));
   private final BooleanSetting onGround = (BooleanSetting)(new BooleanSetting(EncryptedString.of("On Ground"), true)).setDescription(EncryptedString.of("Whether crystal damage is checked on ground or not"));
   private final BooleanSetting checkPlayers = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Check Players"), true)).setDescription(EncryptedString.of("Checks for nearby players"));
   private final NumberSetting distance = (NumberSetting)(new NumberSetting(EncryptedString.of("Distance"), 1.0D, 10.0D, 5.0D, 0.1D)).setDescription(EncryptedString.of("Player distance"));
   private final BooleanSetting predictCrystals = new BooleanSetting(EncryptedString.of("Predict Crystals"), false);
   private final BooleanSetting checkAim = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Check Aim"), false)).setDescription(EncryptedString.of("Checks if the opponent is aiming at obsidian"));
   private final BooleanSetting checkItems = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Check Items"), false)).setDescription(EncryptedString.of("Checks if the opponent is holding crystals"));
   private final NumberSetting activatesAbove = (NumberSetting)(new NumberSetting(EncryptedString.of("Activates Above"), 0.0D, 4.0D, 0.2D, 0.1D)).setDescription(EncryptedString.of("Height to trigger at"));
   private boolean belowHealth;
   private boolean offhandHasNoTotem;

   public AutoDoubleHand() {
      super(EncryptedString.of("Auto Double Hand"), EncryptedString.of("Automatically switches to your totem when you're about to pop"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.stopOnCrystal, this.checkShield, this.onPop, this.onHealth, this.predict, this.health, this.onGround, this.checkPlayers, this.distance, this.predictCrystals, this.checkAim, this.checkItems, this.activatesAbove});
      this.belowHealth = false;
      this.offhandHasNoTotem = false;
   }

   public void onEnable() {
      this.eventManager.add(HudListener.class, this);
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(HudListener.class, this);
      super.onDisable();
   }

   public void onRenderHud(HudListener.HudEvent event) {
      if (mc.field_1724 != null) {
         if (!((AutoCrystal)Lucid.INSTANCE.getModuleManager().getModule(AutoCrystal.class)).crystalling || !this.stopOnCrystal.getValue()) {
            double squaredDistance = this.distance.getValue() * this.distance.getValue();
            class_1661 inventory = mc.field_1724.method_31548();
            if (!this.checkShield.getValue() || !mc.field_1724.method_6039()) {
               if (((class_1799)inventory.field_7544.get(0)).method_7909() != class_1802.field_8288 && this.onPop.getValue() && !this.offhandHasNoTotem) {
                  this.offhandHasNoTotem = true;
                  InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
               }

               if (((class_1799)inventory.field_7544.get(0)).method_7909() == class_1802.field_8288) {
                  this.offhandHasNoTotem = false;
               }

               if ((double)mc.field_1724.method_6032() <= this.health.getValue() && this.onHealth.getValue() && !this.belowHealth) {
                  this.belowHealth = true;
                  InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
               }

               if ((double)mc.field_1724.method_6032() > this.health.getValue()) {
                  this.belowHealth = false;
               }

               if (this.predict.getValue()) {
                  if (!(mc.field_1724.method_6032() > 19.0F)) {
                     if (this.onGround.getValue() || !mc.field_1724.method_24828()) {
                        if (!this.checkPlayers.getValue() || !mc.field_1687.method_18456().parallelStream().filter((e) -> {
                           return e != mc.field_1724;
                        }).noneMatch((p) -> {
                           return mc.field_1724.method_5858(p) <= squaredDistance;
                        })) {
                           double above = this.activatesAbove.getValue();
                           int floor = (int)Math.floor(above);

                           for(int i = 1; i <= floor; ++i) {
                              if (!mc.field_1687.method_8320(mc.field_1724.method_24515().method_10069(0, -i, 0)).method_26215()) {
                                 return;
                              }
                           }

                           class_243 playerPos = mc.field_1724.method_19538();
                           class_2338 playerBlockPos = new class_2338((int)playerPos.field_1352, (int)playerPos.field_1351 - (int)above, (int)playerPos.field_1350);
                           if (mc.field_1687.method_8320(new class_2338(playerBlockPos)).method_26215()) {
                              List<class_1511> crystals = this.nearbyCrystals();
                              ArrayList<class_243> pos = new ArrayList();
                              crystals.forEach((e) -> {
                                 pos.add(e.method_19538());
                              });
                              if (this.predictCrystals.getValue()) {
                                 Stream<class_2338> s = BlockUtils.getAllInBoxStream(mc.field_1724.method_24515().method_10069(-6, -8, -6), mc.field_1724.method_24515().method_10069(6, 2, 6)).filter((e) -> {
                                    return mc.field_1687.method_8320(e).method_26204() == class_2246.field_10540 || mc.field_1687.method_8320(e).method_26204() == class_2246.field_9987;
                                 }).filter(CrystalUtils::canPlaceCrystalClient);
                                 if (this.checkAim.getValue()) {
                                    if (this.checkItems.getValue()) {
                                       s = s.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                                    } else {
                                       s = s.filter(this::arePeopleAimingAtBlock);
                                    }
                                 }

                                 s.forEachOrdered((e) -> {
                                    pos.add(class_243.method_24955(e).method_1031(0.0D, 1.0D, 0.0D));
                                 });
                              }

                              Iterator var17 = pos.iterator();

                              while(var17.hasNext()) {
                                 class_243 crys = (class_243)var17.next();
                                 double damage = (double)DamageUtils.crystalDamage(mc.field_1724, crys);
                                 if (damage >= (double)(mc.field_1724.method_6032() + mc.field_1724.method_6067())) {
                                    InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
                                    break;
                                 }
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private List<class_1511> nearbyCrystals() {
      class_243 pos = mc.field_1724.method_19538();
      return mc.field_1687.method_8390(class_1511.class, new class_238(pos.method_1031(-6.0D, -6.0D, -6.0D), pos.method_1031(6.0D, 6.0D, 6.0D)), (e) -> {
         return true;
      });
   }

   private boolean arePeopleAimingAtBlock(class_2338 block) {
      class_243[] eyesPos = new class_243[1];
      class_3965[] hitResult = new class_3965[1];
      return mc.field_1687.method_18456().parallelStream().filter((e) -> {
         return e != mc.field_1724;
      }).anyMatch((e) -> {
         eyesPos[0] = RotationUtils.getEyesPos(e);
         hitResult[0] = mc.field_1687.method_17742(new class_3959(eyesPos[0], eyesPos[0].method_1019(RotationUtils.getPlayerLookVec(e).method_1021(4.5D)), class_3960.field_17558, class_242.field_1348, e));
         return hitResult[0] != null && hitResult[0].method_17777().equals(block);
      });
   }

   private boolean arePeopleAimingAtBlockAndHoldingCrystals(class_2338 block) {
      class_243[] eyesPos = new class_243[1];
      class_3965[] hitResult = new class_3965[1];
      return mc.field_1687.method_18456().parallelStream().filter((e) -> {
         return e != mc.field_1724;
      }).filter((e) -> {
         return e.method_24518(class_1802.field_8301);
      }).anyMatch((e) -> {
         eyesPos[0] = RotationUtils.getEyesPos(e);
         hitResult[0] = mc.field_1687.method_17742(new class_3959(eyesPos[0], eyesPos[0].method_1019(RotationUtils.getPlayerLookVec(e).method_1021(4.5D)), class_3960.field_17558, class_242.field_1348, e));
         return hitResult[0] != null && hitResult[0].method_17777().equals(block);
      });
   }
}
