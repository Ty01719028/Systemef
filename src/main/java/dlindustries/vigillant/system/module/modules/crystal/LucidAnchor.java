package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.ItemUseListener;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.MinMaxSetting;
import dev.potato.lucid.module.setting.NumberSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.InventoryUtils;
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MathUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.TimerUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_2246;
import net.minecraft.class_239;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_4969;

public class LucidAnchor extends Module implements TickListener, ItemUseListener {
   public BooleanSetting charger = new BooleanSetting(EncryptedString.of("Charger"), true);
   public TimerUtils timer = new TimerUtils();
   public BooleanSetting placer = new BooleanSetting(EncryptedString.of("Placer"), false);
   public MinMaxSetting airPlaceChance = new MinMaxSetting(EncryptedString.of("Air Place Chance"), 60.0D, 80.0D, 1.0D, 60.0D, 80.0D);
   public int clickTimer;
   public BooleanSetting use = new BooleanSetting(EncryptedString.of("Use"), true);
   public BooleanSetting safe = new BooleanSetting(EncryptedString.of("Safe"), false);
   public boolean sneak;
   public BooleanSetting exploder = new BooleanSetting(EncryptedString.of("Exploder"), true);
   public BooleanSetting airPlace = new BooleanSetting(EncryptedString.of("Air Place"), false);
   public KeybindSetting airPlaceKey = new KeybindSetting(EncryptedString.of("Air Place Key"), -1, false);
   public int swapTimer2;
   public NumberSetting slot = new NumberSetting(EncryptedString.of("Slot"), 1.0D, 9.0D, 1.0D, 1.0D);
   public MinMaxSetting swapDelay = new MinMaxSetting(EncryptedString.of("Swap Delay"), 2.0D, 4.0D, 1.0D, 2.0D, 4.0D);
   public MinMaxSetting clickDelay = new MinMaxSetting(EncryptedString.of("Click Delay"), 2.0D, 4.0D, 1.0D, 2.0D, 4.0D);
   public int swapTimer;
   public KeybindSetting bind = new KeybindSetting(EncryptedString.of("Bind"), -1, false);
   public int sneakTimer;
   public int swapTimer3;

   public LucidAnchor() {
      super(EncryptedString.of("Lucid Anchor"), EncryptedString.of("Automatically manages respawn anchors"), -1, Category.CPVP);
      this.addSettings(new Setting[]{this.bind, this.use, this.charger, this.exploder, this.swapDelay, this.clickDelay, this.slot, this.safe, this.airPlace, this.airPlaceKey, this.airPlaceChance});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.swapTimer = 0;
      this.swapTimer2 = 0;
      this.swapTimer3 = 0;
      this.clickTimer = 0;
      this.sneakTimer = 0;
      this.sneak = false;
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      if (this.sneak) {
         mc.field_1690.field_1832.method_23481(false);
         this.sneak = false;
      }

      super.onDisable();
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         if (this.sneak && this.sneakTimer > 3) {
            this.sneakTimer = 0;
            this.sneak = false;
            mc.field_1690.field_1832.method_23481(false);
         }

         class_3965 hitResult;
         class_239 var2;
         if (KeyUtils.isKeyPressed(this.bind.getKey()) && (!this.use.getValue() || mc.field_1724.method_6047().method_7976() != class_1839.field_8949 && mc.field_1724.method_6047().method_7976() != class_1839.field_8951 && mc.field_1724.method_6047().method_7976() != class_1839.field_8953 && mc.field_1724.method_6047().method_7976() != class_1839.field_8947 && mc.field_1724.method_6047().method_7976() != class_1839.field_8950 && mc.field_1724.method_6047().method_7976() != class_1839.field_8946) && InventoryUtils.hasItemInHotbar((item) -> {
            return item == class_1802.field_8801;
         }) && InventoryUtils.hasItemInHotbar((item) -> {
            return item == class_1802.field_23141;
         })) {
            label109: {
               if (this.placer.getValue() && this.swapTimer >= MathUtils.randomInt((int)this.swapDelay.getMinValue(), Math.max((int)this.swapDelay.getMinValue() + 1, (int)this.swapDelay.getMaxValue())) && InventoryUtils.selectItemFromHotbar(class_1802.field_23141)) {
                  mc.field_1690.field_1904.method_23481(false);
                  if (this.clickTimer >= MathUtils.randomInt((int)this.clickDelay.getMinValue(), Math.max((int)this.clickDelay.getMinValue() + 1, (int)this.clickDelay.getMaxValue()))) {
                     mc.field_1690.field_1904.method_23481(false);
                     MouseSimulation.mouseClick(1);
                     this.clickTimer = 0;
                     this.swapTimer = 0;
                  }
               }

               if (this.charger.getValue()) {
                  var2 = mc.field_1765;
                  if (var2 instanceof class_3965) {
                     hitResult = (class_3965)var2;
                     if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_23152) && (Integer)mc.field_1687.method_8320(hitResult.method_17777()).method_11654(class_4969.field_23153) == 0 && this.swapTimer2 >= MathUtils.randomInt((int)this.swapDelay.getMinValue(), Math.max((int)this.swapDelay.getMinValue() + 1, (int)this.swapDelay.getMaxValue())) && InventoryUtils.selectItemFromHotbar(class_1802.field_8801) && this.clickTimer >= MathUtils.randomInt((int)this.clickDelay.getMinValue(), Math.max((int)this.clickDelay.getMinValue() + 1, (int)this.clickDelay.getMaxValue()))) {
                        mc.field_1690.field_1904.method_23481(false);
                        MouseSimulation.mouseClick(1);
                        this.clickTimer = 0;
                        this.swapTimer2 = 0;
                        MouseSimulation.mouseClick(0);
                        break label109;
                     }
                  }
               }

               if (this.exploder.getValue()) {
                  class_239 var3 = mc.field_1765;
                  if (var3 instanceof class_3965) {
                     class_3965 hitResult = (class_3965)var3;
                     if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_23152) && (Integer)mc.field_1687.method_8320(hitResult.method_17777()).method_11654(class_4969.field_23153) != 0 && this.swapTimer3 >= MathUtils.randomInt((int)this.swapDelay.getMinValue(), Math.max((int)this.swapDelay.getMinValue() + 1, (int)this.swapDelay.getMaxValue()))) {
                        if (!this.safe.getValue()) {
                           mc.field_1724.method_31548().field_7545 = (int)this.slot.getValue() - 1;
                        }

                        if (this.clickTimer >= MathUtils.randomInt((int)this.clickDelay.getMinValue(), Math.max((int)this.clickDelay.getMinValue() + 1, (int)this.clickDelay.getMaxValue()))) {
                           mc.field_1690.field_1904.method_23481(false);
                           MouseSimulation.mouseClick(1);
                           this.clickTimer = 0;
                           this.swapTimer3 = 0;
                        }
                     }
                  }
               }
            }
         }

         if (this.airPlace.getValue() && KeyUtils.isKeyPressed(this.airPlaceKey.getKey())) {
            if (!this.timer.hasReached(1000.0D)) {
               return;
            }

            var2 = mc.field_1765;
            if (var2 instanceof class_3965) {
               hitResult = (class_3965)var2;
               if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_23152) && (Integer)mc.field_1687.method_8320(hitResult.method_17777()).method_11654(class_4969.field_23153) != 0) {
                  InventoryUtils.selectItemFromHotbar(class_1802.field_23141);
                  if (MathUtils.randomInt(1, 100) <= MathUtils.randomInt((int)this.airPlaceChance.getMinValue(), Math.max((int)this.airPlaceChance.getMinValue() + 1, (int)this.airPlaceChance.getMaxValue())) && mc.field_1724.method_6047().method_31574(class_1802.field_23141)) {
                     mc.field_1690.field_1904.method_23481(false);
                     mc.method_1562().method_52787(new class_2885(class_1268.field_5808, hitResult, 0));
                     this.timer.reset();
                  }
               }
            }
         }

         this.tick();
      }
   }

   void tick() {
      ++this.swapTimer;
      ++this.swapTimer2;
      ++this.swapTimer3;
      ++this.clickTimer;
      ++this.sneakTimer;
   }

   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      class_3965 hitResult;
      class_239 var3;
      if (this.safe.getValue() && mc.field_1724.method_6047().method_31574(class_1802.field_8801)) {
         var3 = mc.field_1765;
         if (var3 instanceof class_3965) {
            hitResult = (class_3965)var3;
            if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_23152) && (Integer)mc.field_1687.method_8320(hitResult.method_17777()).method_11654(class_4969.field_23153) != 0) {
               (new Thread(() -> {
                  mc.field_1690.field_1832.method_23481(true);

                  try {
                     Thread.sleep(50L);
                  } catch (InterruptedException var3) {
                     throw new RuntimeException(var3);
                  }

                  MouseSimulation.mouseClick(1);

                  try {
                     Thread.sleep(50L);
                  } catch (InterruptedException var2) {
                     throw new RuntimeException(var2);
                  }

                  mc.field_1690.field_1832.method_23481(false);
                  mc.field_1724.method_31548().field_7545 = (int)this.slot.getValue() - 1;
               })).start();
            }
         }
      }

      if (mc.field_1724.method_6047().method_31574(class_1802.field_8801)) {
         var3 = mc.field_1765;
         if (var3 instanceof class_3965) {
            hitResult = (class_3965)var3;
            if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_23152) && (Integer)mc.field_1687.method_8320(hitResult.method_17777()).method_11654(class_4969.field_23153) != 0) {
            }
         }
      }

   }
}
