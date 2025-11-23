package dev.potato.lucid.module.modules.cpvp;

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
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public class AAutoCrystal extends Module implements TickListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 1, false)).setDescription(EncryptedString.of("Key that does the crystalling"));
   private final BooleanSetting clickSimulation = new BooleanSetting(EncryptedString.of("Click Simulation"), true);
   private final BooleanSetting onRmb = new BooleanSetting(EncryptedString.of("On RMB"), true);
   private final BooleanSetting noCountGlitch = new BooleanSetting(EncryptedString.of("No Count Glitch"), true);
   private final BooleanSetting noBounce = new BooleanSetting(EncryptedString.of("No Bounce"), true);
   private final NumberSetting placeDelay = new NumberSetting(EncryptedString.of("Place Delay"), 0.0D, 10.0D, 0.0D, 1.0D);
   private final NumberSetting breakDelay = new NumberSetting(EncryptedString.of("Break Delay"), 0.0D, 10.0D, 0.0D, 1.0D);
   private final BooleanSetting fastMode = new BooleanSetting(EncryptedString.of("Fast Mode"), true);
   private int placeClock = 0;
   private int breakClock = 0;

   public AAutoCrystal() {
      super(EncryptedString.of("Asteria AutoCrystal"), EncryptedString.of("Automatically crystalling (ported from Phosphor, keybind + old settings)"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.activateKey, this.clickSimulation, this.onRmb, this.noCountGlitch, this.noBounce, this.placeDelay, this.breakDelay, this.fastMode});
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
         if (this.activateKey.getKey() == -1 || KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            if (this.placeClock > 0) {
               --this.placeClock;
            }

            if (this.breakClock > 0) {
               --this.breakClock;
            }

            class_239 var2 = mc.field_1765;
            if (var2 instanceof class_3966) {
               class_3966 hit = (class_3966)var2;
               class_1297 entity = hit.method_17782();
               if (entity instanceof class_1511 && this.breakClock == 0) {
                  if (this.clickSimulation.getValue()) {
                     MouseSimulation.mouseClick(0);
                  }

                  WorldUtils.hitEntity(entity, true);
                  this.breakClock = this.breakDelay.getValueInt();
               }
            }

            var2 = mc.field_1765;
            if (var2 instanceof class_3965) {
               class_3965 hit = (class_3965)var2;
               if (mc.field_1765.method_17783() == class_240.field_1332 && (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540) || BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)) && CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777()) && this.placeClock == 0 && mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
                  if (this.clickSimulation.getValue()) {
                     MouseSimulation.mouseClick(1);
                  }

                  WorldUtils.placeBlock(hit, true);
                  this.placeClock = this.placeDelay.getValueInt();
               }
            }

         }
      }
   }
}
