package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.ItemUseListener;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.CrystalUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MathUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.WorldUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1621;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class AutoCrystal extends Module implements TickListener, ItemUseListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 1, false)).setDescription(EncryptedString.of("Key that does the crystalling"));
   private final NumberSetting placeDelay = new NumberSetting(EncryptedString.of("Place Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting breakDelay = new NumberSetting(EncryptedString.of("Break Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting placeChance = (NumberSetting)(new NumberSetting(EncryptedString.of("Place Chance"), 0.0D, 100.0D, 100.0D, 1.0D)).setDescription(EncryptedString.of("Randomization"));
   private final NumberSetting breakChance = (NumberSetting)(new NumberSetting(EncryptedString.of("Break Chance"), 0.0D, 100.0D, 100.0D, 1.0D)).setDescription(EncryptedString.of("Randomization"));
   private final BooleanSetting stopOnKill = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Stop on Kill"), false)).setDescription(EncryptedString.of("Won't crystal if a dead player is nearby"));
   private final BooleanSetting fakePunch = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Fake Punch"), false)).setDescription(EncryptedString.of("Will hit every entity and block if you miss a hitcrystal"));
   private final BooleanSetting clickSimulation = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Click Simulation"), false)).setDescription(EncryptedString.of("Makes the CPS hud think you're legit"));
   private final BooleanSetting damageTick = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Damage Tick"), false)).setDescription(EncryptedString.of("Times your crystals for a perfect d-tap"));
   private final BooleanSetting antiWeakness = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Anti-Weakness"), false)).setDescription(EncryptedString.of("Silently switches to a sword and then hits the crystal if you have weakness"));
   private final NumberSetting particleChance = (NumberSetting)(new NumberSetting(EncryptedString.of("Particle Chance"), 0.0D, 100.0D, 20.0D, 1.0D)).setDescription(EncryptedString.of("Adds block breaking particles to make it seem more legit from your POV (Only works with fake punch)"));
   private int placeClock;
   private int breakClock;
   public boolean crystalling;

   public AutoCrystal() {
      super(EncryptedString.of("Auto Crystal"), EncryptedString.of("Automatically crystals fast for you"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.activateKey, this.placeDelay, this.breakDelay, this.placeChance, this.breakChance, this.stopOnKill, this.fakePunch, this.clickSimulation, this.damageTick, this.antiWeakness, this.particleChance});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.placeClock = 0;
      this.breakClock = 0;
      this.crystalling = false;
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      super.onDisable();
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         boolean dontPlace = this.placeClock != 0;
         boolean dontBreak = this.breakClock != 0;
         if (!this.stopOnKill.getValue() || !WorldUtils.isDeadBodyNearby()) {
            int randomInt = MathUtils.randomInt(1, 100);
            if (dontPlace) {
               --this.placeClock;
            }

            if (dontBreak) {
               --this.breakClock;
            }

            if (!mc.field_1724.method_6115()) {
               if (!this.damageTick.getValue() || !this.damageTickCheck()) {
                  if (this.activateKey.getKey() != -1 && !KeyUtils.isKeyPressed(this.activateKey.getKey())) {
                     this.placeClock = 0;
                     this.breakClock = 0;
                     this.crystalling = false;
                  } else {
                     this.crystalling = true;
                     if (mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
                        class_239 var5 = mc.field_1765;
                        if (var5 instanceof class_3965) {
                           class_3965 hit = (class_3965)var5;
                           if (mc.field_1765.method_17783() == class_240.field_1332) {
                              if (!dontPlace && randomInt <= this.placeChance.getValueInt() && (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) || BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987) && CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777()))) {
                                 if (this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(1);
                                 }

                                 WorldUtils.placeBlock(hit, true);
                                 if (this.fakePunch.getValue() && (double)randomInt <= this.particleChance.getValue() && CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777()) && hit.method_17780() == class_2350.field_11036) {
                                    mc.field_1713.method_3054(hit.method_17777(), hit.method_17780());
                                 }

                                 this.placeClock = this.placeDelay.getValueInt();
                              }

                              if (this.fakePunch.getValue()) {
                                 if (!dontBreak && randomInt <= this.breakChance.getValueInt()) {
                                    if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) || BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)) {
                                       return;
                                    }

                                    if (this.clickSimulation.getValue()) {
                                       if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) && !BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)) {
                                          MouseSimulation.mouseClick(0);
                                       } else if (CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777())) {
                                          MouseSimulation.mouseClick(0);
                                       }
                                    }

                                    mc.field_1761.method_2910(hit.method_17777(), hit.method_17780());
                                    mc.field_1724.method_6104(class_1268.field_5808);
                                    mc.field_1713.method_3054(hit.method_17777(), hit.method_17780());
                                    mc.field_1761.method_2902(hit.method_17777(), hit.method_17780());
                                    this.breakClock = this.breakDelay.getValueInt();
                                 }

                                 if (!dontPlace && randomInt <= this.placeChance.getValueInt() && dontBreak && this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(1);
                                 }
                              }
                           }

                           if (mc.field_1765.method_17783() == class_240.field_1333 && this.fakePunch.getValue()) {
                              if (!dontBreak && randomInt <= this.breakChance.getValueInt()) {
                                 if (mc.field_1761.method_2924()) {
                                    mc.field_1771 = 10;
                                 }

                                 if (this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(0);
                                 }

                                 mc.field_1724.method_7350();
                                 mc.field_1724.method_6104(class_1268.field_5808);
                                 this.breakClock = this.breakDelay.getValueInt();
                              }

                              if (!dontPlace && randomInt <= this.placeChance.getValueInt() && dontBreak && this.clickSimulation.getValue()) {
                                 MouseSimulation.mouseClick(1);
                              }
                           }
                        }

                        randomInt = MathUtils.randomInt(1, 100);
                        var5 = mc.field_1765;
                        if (var5 instanceof class_3966) {
                           class_3966 hit = (class_3966)var5;
                           if (!dontBreak && randomInt <= this.breakChance.getValueInt()) {
                              class_1297 entity = hit.method_17782();
                              if (!this.fakePunch.getValue() && !(entity instanceof class_1511) && !(entity instanceof class_1621)) {
                                 return;
                              }

                              int previousSlot = mc.field_1724.method_31548().field_7545;
                              if ((entity instanceof class_1511 || entity instanceof class_1621) && this.antiWeakness.getValue() && this.cantBreakCrystal()) {
                                 InventoryUtils.selectSword();
                              }

                              if (this.clickSimulation.getValue()) {
                                 MouseSimulation.mouseClick(0);
                              }

                              WorldUtils.hitEntity(entity, true);
                              this.breakClock = this.breakDelay.getValueInt();
                              if (this.antiWeakness.getValue()) {
                                 InventoryUtils.setInvSlot(previousSlot);
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

   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
         class_239 var3 = mc.field_1765;
         if (var3 instanceof class_3965) {
            class_3965 h = (class_3965)var3;
            if (mc.field_1765.method_17783() == class_240.field_1332 && (BlockUtils.isBlock(h.method_17777(), class_2246.field_10540) || BlockUtils.isBlock(h.method_17777(), class_2246.field_9987))) {
               event.cancel();
            }
         }
      }

   }

   private boolean cantBreakCrystal() {
      assert mc.field_1724 != null;

      class_1293 weakness = mc.field_1724.method_6112(class_1294.field_5911);
      class_1293 strength = mc.field_1724.method_6112(class_1294.field_5910);
      return weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !WorldUtils.isTool(mc.field_1724.method_6047());
   }

   private boolean damageTickCheck() {
      return mc.field_1687.method_18456().parallelStream().filter((e) -> {
         return e != mc.field_1724;
      }).filter((e) -> {
         return e.method_5858(mc.field_1724) < 36.0D;
      }).filter((e) -> {
         return e.method_49107() == null;
      }).filter((e) -> {
         return !e.method_24828();
      }).anyMatch((e) -> {
         return e.field_6235 >= 2;
      }) && !(mc.field_1724.method_6052() instanceof class_1657);
   }
}
