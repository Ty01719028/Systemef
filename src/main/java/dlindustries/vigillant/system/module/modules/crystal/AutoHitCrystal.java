package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.Lucid;
import dev.potato.lucid.event.events.AttackListener;
import dev.potato.lucid.event.events.ItemUseListener;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.BlockUtils;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MathUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.WorldUtils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public final class AutoHitCrystal extends Module implements TickListener, ItemUseListener, AttackListener {
   private final KeybindSetting activateKey = (KeybindSetting)(new KeybindSetting(EncryptedString.of("Activate Key"), 1, false)).setDescription(EncryptedString.of("Key that does hit crystalling"));
   private final BooleanSetting checkPlace = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Check Place"), false)).setDescription(EncryptedString.of("Checks if you can place the obsidian on that block"));
   private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting switchChance = new NumberSetting(EncryptedString.of("Switch Chance"), 0.0D, 100.0D, 100.0D, 1.0D);
   private final NumberSetting placeDelay = new NumberSetting(EncryptedString.of("Place Delay"), 0.0D, 20.0D, 0.0D, 1.0D);
   private final NumberSetting placeChance = (NumberSetting)(new NumberSetting(EncryptedString.of("Place Chance"), 0.0D, 100.0D, 100.0D, 1.0D)).setDescription(EncryptedString.of("Randomization"));
   private final BooleanSetting workWithTotem = new BooleanSetting(EncryptedString.of("Work With Totem"), false);
   private final BooleanSetting workWithCrystal = new BooleanSetting(EncryptedString.of("Work With Crystal"), false);
   private final BooleanSetting workWithPickaxe = new BooleanSetting(EncryptedString.of("Work With Pickaxe"), false);
   private final BooleanSetting clickSimulation = (BooleanSetting)(new BooleanSetting(EncryptedString.of("Click Simulation"), false)).setDescription(EncryptedString.of("Makes the CPS hud think you're legit"));
   private final BooleanSetting swordSwap = new BooleanSetting(EncryptedString.of("Sword Swap"), true);
   private int placeClock = 0;
   private int switchClock = 0;
   private boolean active;
   private boolean crystalling;
   private boolean crystalSelected;

   public AutoHitCrystal() {
      super(EncryptedString.of("Auto Hit Crystal"), EncryptedString.of("Automatically hit-crystals for you"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.activateKey, this.checkPlace, this.switchDelay, this.switchChance, this.placeDelay, this.placeChance, this.workWithTotem, this.workWithCrystal, this.workWithPickaxe, this.clickSimulation, this.swordSwap});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      this.reset();
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      super.onDisable();
   }

   public void onTick() {
      int randomNum = MathUtils.randomInt(1, 100);
      if (mc.field_1755 == null) {
         if (KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            class_239 var3 = mc.field_1765;
            if (var3 instanceof class_3965) {
               class_3965 hitResult = (class_3965)var3;
               if (mc.field_1765.method_17783() == class_240.field_1332 && !this.active && !BlockUtils.canPlaceBlockClient(hitResult.method_17777()) && this.checkPlace.getValue()) {
                  return;
               }
            }

            class_1799 mainHandStack = mc.field_1724.method_6047();
            if (!(mainHandStack.method_7909() instanceof class_1829) && (!this.workWithTotem.getValue() || !mainHandStack.method_31574(class_1802.field_8288)) && (!this.workWithCrystal.getValue() || !mainHandStack.method_31574(class_1802.field_8301)) && (!this.workWithPickaxe.getValue() || !(mainHandStack.method_7909() instanceof class_1810)) && !this.active) {
               return;
            }

            class_239 var4 = mc.field_1765;
            class_3965 hit;
            if (var4 instanceof class_3965) {
               hit = (class_3965)var4;
               if (!this.active && this.swordSwap.getValue() && mc.field_1765.method_17783() == class_240.field_1332) {
                  class_2248 block = mc.field_1687.method_8320(hit.method_17777()).method_26204();
                  this.crystalling = block == class_2246.field_10540 || block == class_2246.field_9987;
               }
            }

            this.active = true;
            if (!this.crystalling) {
               var4 = mc.field_1765;
               if (var4 instanceof class_3965) {
                  hit = (class_3965)var4;
                  if (hit.method_17783() == class_240.field_1333) {
                     return;
                  }

                  if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)) {
                     if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_23152) && BlockUtils.isAnchorCharged(hit.method_17777())) {
                        return;
                     }

                     mc.field_1690.field_1904.method_23481(false);
                     if (!mc.field_1724.method_24518(class_1802.field_8281)) {
                        if (this.switchClock > 0) {
                           --this.switchClock;
                           return;
                        }

                        if (randomNum <= this.switchChance.getValueInt()) {
                           this.switchClock = this.switchDelay.getValueInt();
                           InventoryUtils.selectItemFromHotbar(class_1802.field_8281);
                        }
                     }

                     if (mc.field_1724.method_24518(class_1802.field_8281)) {
                        if (this.placeClock > 0) {
                           --this.placeClock;
                           return;
                        }

                        if (this.clickSimulation.getValue()) {
                           MouseSimulation.mouseClick(1);
                        }

                        randomNum = MathUtils.randomInt(1, 100);
                        if (randomNum <= this.placeChance.getValueInt()) {
                           WorldUtils.placeBlock(hit, true);
                           this.placeClock = this.placeDelay.getValueInt();
                           this.crystalling = true;
                        }
                     }
                  }
               }
            }

            if (this.crystalling) {
               if (!mc.field_1724.method_24518(class_1802.field_8301) && !this.crystalSelected) {
                  if (this.switchClock > 0) {
                     --this.switchClock;
                     return;
                  }

                  randomNum = MathUtils.randomInt(1, 100);
                  if (randomNum <= this.switchChance.getValueInt()) {
                     this.crystalSelected = InventoryUtils.selectItemFromHotbar(class_1802.field_8301);
                     this.switchClock = this.switchDelay.getValueInt();
                  }
               }

               if (mc.field_1724.method_24518(class_1802.field_8301)) {
                  AutoCrystal autoCrystal = (AutoCrystal)Lucid.INSTANCE.getModuleManager().getModule(AutoCrystal.class);
                  if (!autoCrystal.isEnabled()) {
                     autoCrystal.onTick();
                  }
               }
            }
         } else {
            this.reset();
         }

      }
   }

   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (mc.field_1724.method_6047().method_7909() instanceof class_1829) {
         class_239 var3 = mc.field_1765;
         if (var3 instanceof class_3965) {
            class_3965 hit = (class_3965)var3;
            if (mc.field_1687.method_8320(hit.method_17777()).method_26204() == class_2246.field_10540 && !mc.field_1724.method_24518(class_1802.field_8301)) {
               InventoryUtils.selectItemFromHotbar(class_1802.field_8301);
            }
         }
      }

      class_1799 mainHandStack = mc.field_1724.method_6047();
      if ((mainHandStack.method_31574(class_1802.field_8301) || mainHandStack.method_31574(class_1802.field_8281)) && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) != 1) {
         event.cancel();
      }

   }

   public void reset() {
      this.placeClock = this.placeDelay.getValueInt();
      this.switchClock = this.switchDelay.getValueInt();
      this.active = false;
      this.crystalling = false;
      this.crystalSelected = false;
   }

   public void onAttack(AttackListener.AttackEvent event) {
      if (mc.field_1724.method_6047().method_31574(class_1802.field_8301) && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) != 1) {
         event.cancel();
      }

   }
}
