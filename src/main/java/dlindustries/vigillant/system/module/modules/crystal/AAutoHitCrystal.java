package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.WorldUtils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_239;
import net.minecraft.class_2508;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AAutoHitCrystal extends Module implements TickListener {
   private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", true);
   private final BooleanSetting workWithTotem = new BooleanSetting("Work With Totem", true);
   private final BooleanSetting swordSwap = new BooleanSetting("Sword Swap", true);
   private final NumberSetting placeDelay = new NumberSetting("Obsidian Place Delay", 0.0D, 10.0D, 0.0D, 1.0D);
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0D, 10.0D, 0.0D, 1.0D);
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", 1, false);
   private int placeClock = 0;
   private int switchClock = 0;
   private boolean activated;
   private boolean crystalling;
   private boolean selectedCrystal;
   private boolean swordSelected;

   public AAutoHitCrystal() {
      super("Asteria AutoHitCrystal", "Automatically hit crystals.", -1, Category.CPVP);
      this.addSettings(new Setting[]{this.clickSimulation, this.workWithTotem, this.swordSwap, this.placeDelay, this.switchDelay, this.activateKey});
   }

   public void reset() {
      this.placeClock = this.placeDelay.getValueInt();
      this.switchClock = this.switchDelay.getValueInt();
      this.activated = false;
      this.crystalling = false;
      this.selectedCrystal = false;
      this.swordSelected = false;
   }

   public void onEnable() {
      Lucid.INSTANCE.getEventManager().add(TickListener.class, this);
      this.reset();
   }

   public void onDisable() {
      Lucid.INSTANCE.getEventManager().remove(TickListener.class, this);
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         if (KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            class_1799 mainHand = mc.field_1724.method_6047();
            if (this.activateKey.getKey() == 1) {
               if (!(mainHand.method_7909() instanceof class_1829) && (!this.workWithTotem.getValue() || !mainHand.method_31574(class_1802.field_8288)) && !this.activated) {
                  return;
               }

               this.activated = true;
            }

            class_239 var3 = mc.field_1765;
            class_3965 blockHit;
            if (var3 instanceof class_3965) {
               blockHit = (class_3965)var3;
               if (this.swordSwap.getValue() && mc.field_1765.method_17783() == class_240.field_1332) {
                  class_2248 block = mc.field_1687.method_8320(blockHit.method_17777()).method_26204();
                  this.crystalling = block == class_2246.field_10540 || block == class_2246.field_9987;
               }
            }

            if (!this.crystalling) {
               var3 = mc.field_1765;
               if (var3 instanceof class_3965) {
                  blockHit = (class_3965)var3;
                  if (blockHit.method_17783() == class_240.field_1333) {
                     return;
                  }

                  if (mc.field_1687.method_8320(blockHit.method_17777()).method_26204() instanceof class_2508) {
                     return;
                  }

                  if (!BlockUtils.isBlock(blockHit.method_17777(), class_2246.field_10540)) {
                     mc.field_1690.field_1904.method_23481(false);
                     if (!mainHand.method_31574(class_1802.field_8281)) {
                        if (this.switchClock > 0) {
                           --this.switchClock;
                           return;
                        }

                        InventoryUtils.selectItemFromHotbar(class_1802.field_8281);
                        this.switchClock = this.switchDelay.getValueInt();
                        return;
                     }

                     if (this.placeClock > 0) {
                        --this.placeClock;
                        return;
                     }

                     if (this.clickSimulation.getValue()) {
                        MouseSimulation.mouseClick(1);
                     }

                     WorldUtils.placeBlock(blockHit, true);
                     this.placeClock = this.placeDelay.getValueInt();
                     this.crystalling = true;
                  } else {
                     this.crystalling = true;
                  }
               }
            }

            if (this.crystalling) {
               if (!mc.field_1724.method_6047().method_31574(class_1802.field_8301) && !this.selectedCrystal) {
                  if (this.switchClock > 0) {
                     --this.switchClock;
                     return;
                  }

                  this.selectedCrystal = InventoryUtils.selectItemFromHotbar(class_1802.field_8301);
                  this.switchClock = this.switchDelay.getValueInt();
                  return;
               }

               AutoCrystal autoCrystal = (AutoCrystal)Lucid.INSTANCE.getModuleManager().getModule(AutoCrystal.class);
               if (autoCrystal != null && autoCrystal.isEnabled()) {
                  autoCrystal.onTick();
               }
            }
         } else {
            this.reset();
         }

      }
   }
}
