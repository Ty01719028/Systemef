package dev.potato.lucid.module.modules.cpvp;

import dev.potato.lucid.event.events.ItemUseListener;
import dev.potato.lucid.event.events.TickListener;
import dev.potato.lucid.module.Category;
import dev.potato.lucid.module.Module;
import dev.potato.lucid.module.setting.BooleanSetting;
import dev.potato.lucid.module.setting.KeybindSetting;
import dev.potato.lucid.module.setting.MinMaxSetting;
import dev.potato.lucid.module.setting.ModeSetting;
import dev.potato.lucid.module.setting.Setting;
import dev.potato.lucid.utils.EncryptedString;
import dev.potato.lucid.utils.KeyUtils;
import dev.potato.lucid.utils.MathUtils;
import dev.potato.lucid.utils.MouseSimulation;
import dev.potato.lucid.utils.TimerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_638;
import net.minecraft.class_239.class_240;

public class CrystalClicker extends Module implements TickListener, ItemUseListener {
   public boolean broke;
   public BooleanSetting noWeak;
   public KeybindSetting bind = new KeybindSetting(EncryptedString.of("Bind"), -1, false);
   public boolean crystaling;
   public TimerUtils timer;
   public int id;
   public class_1511 target;
   public int breakTick;
   public ModeSetting<CrystalClicker.CrystalMode> mode;
   public class_2338 blockPos;
   public boolean placed;
   public int placeTick;
   public BooleanSetting onGround;
   public MinMaxSetting breakDelay;
   public BooleanSetting pauseOnKill;
   public MinMaxSetting placeDelay;

   public CrystalClicker() {
      super(EncryptedString.of("Crystal Clicker"), EncryptedString.of("Automatically places and breaks crystals"), -1, Category.CPVP);
      this.mode = new ModeSetting(EncryptedString.of("Mode"), CrystalClicker.CrystalMode.NORMAL, CrystalClicker.CrystalMode.class);
      this.placeDelay = new MinMaxSetting(EncryptedString.of("Place Delay"), 3.0D, 4.0D, 1.0D, 3.0D, 4.0D);
      this.breakDelay = new MinMaxSetting(EncryptedString.of("Break Delay"), 3.0D, 4.0D, 1.0D, 3.0D, 4.0D);
      this.pauseOnKill = new BooleanSetting(EncryptedString.of("Pause on Kill"), false);
      this.onGround = new BooleanSetting(EncryptedString.of("On Ground"), false);
      this.noWeak = new BooleanSetting(EncryptedString.of("No Weakness"), false);
      this.timer = new TimerUtils();
      this.addSettings(new Setting[]{this.bind, this.mode, this.placeDelay, this.breakDelay, this.pauseOnKill, this.onGround, this.noWeak});
   }

   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.breakTick = 0;
      this.placeTick = 0;
      this.broke = false;
      this.placed = false;
      this.crystaling = false;
      this.target = null;
      this.blockPos = null;
      super.onEnable();
   }

   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      this.target = null;
      this.blockPos = null;
      super.onDisable();
   }

   boolean killCheck() {
      return mc.field_1687.method_18456().parallelStream().filter((e) -> {
         return mc.field_1724 != e;
      }).filter((e) -> {
         return e.method_5858(mc.field_1724) < 36.0D;
      }).anyMatch(class_1309::method_29504);
   }

   boolean crystalCheck() {
      return mc.field_1724.method_6047().method_31574(class_1802.field_8301);
   }

   boolean isCrystal() {
      class_239 var2 = mc.field_1765;
      if (var2 instanceof class_3966) {
         class_3966 hitResult = (class_3966)var2;
         if (hitResult.method_17782() instanceof class_1511) {
            return true;
         }
      }

      return false;
   }

   boolean isObi() {
      class_239 var2 = mc.field_1765;
      if (var2 instanceof class_3965) {
         class_3965 hitResult = (class_3965)var2;
         if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_10540) || mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_9987)) {
            return true;
         }
      }

      return false;
   }

   public void onTick() {
      if (mc.field_1755 == null) {
         if (this.broke) {
            this.broke = false;
            MouseSimulation.mouseClick(0);
         }

         if (this.placed) {
            this.placed = false;
            MouseSimulation.mouseClick(1);
         }

         if (this.killCheck() && this.pauseOnKill.getValue()) {
            this.timer.reset();
         }

         if (this.timer.hasReached(500.0D) && this.crystalCheck() && KeyUtils.isKeyPressed(1)) {
            CrystalClicker.CrystalMode currentMode = (CrystalClicker.CrystalMode)this.mode.getMode();
            if (currentMode == CrystalClicker.CrystalMode.NORMAL) {
               this.normalCrystal();
            } else if (currentMode == CrystalClicker.CrystalMode.BLATANT) {
               this.blatantCrystal();
            } else if (currentMode == CrystalClicker.CrystalMode.SILENT) {
               this.silentCrystal();
            }

            this.tick();
         }

      }
   }

   void blatantCrystal() {
      mc.field_1690.field_1904.method_23481(false);
      if (this.readyToBreak() && this.isCrystal()) {
         if (!mc.field_1724.method_24828() && this.onGround.getValue()) {
            return;
         }

         class_239 var3 = mc.field_1765;
         if (var3 instanceof class_3966) {
            class_3966 hitResult = (class_3966)var3;
            class_1297 var6 = hitResult.method_17782();
            if (var6 instanceof class_1511) {
               class_1511 crystal = (class_1511)var6;
               if (crystal.method_5628() != this.id) {
                  this.id = crystal.method_5628();
                  mc.field_1761.method_2918(mc.field_1724, crystal);
                  mc.field_1724.method_6104(class_1268.field_5808);
               }
            }
         }

         this.resetBreak();
      }

      if (this.readyToPlace() && this.isObi()) {
         class_239 var5 = mc.field_1765;
         if (var5 instanceof class_3965) {
            class_3965 hitResult = (class_3965)var5;
            if (mc.field_1687.method_8320(hitResult.method_17777()).method_27852(class_2246.field_10540) && mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hitResult).method_23666()) {
               mc.field_1724.method_6104(class_1268.field_5808);
            }
         }

         this.resetPlace();
      }

   }

   void silentCrystal() {
      mc.field_1690.field_1904.method_23481(false);
      if (this.readyToBreak()) {
         if (!mc.field_1724.method_24828() && this.onGround.getValue()) {
            return;
         }

         class_239 var3 = mc.field_1765;
         if (var3 instanceof class_3966) {
            class_3966 hitResult = (class_3966)var3;
            class_1297 var5 = hitResult.method_17782();
            if (var5 instanceof class_1511) {
               class_1511 crystal = (class_1511)var5;
               if (!mc.field_1724.method_24828() && this.onGround.getValue()) {
                  return;
               }

               mc.field_1761.method_2918(mc.field_1724, crystal);
               mc.field_1724.method_6104(class_1268.field_5808);
               this.crystaling = false;
               this.resetBreak();
            }
         }
      }

      if (this.readyToPlace() && this.blockPos != null) {
         class_3965 hitResult = new class_3965((new class_238(this.blockPos)).method_1005(), class_2350.field_11036, this.blockPos, false);
         if (hitResult.method_17783() != class_240.field_1333 && mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hitResult).method_23666()) {
            mc.field_1724.method_6104(class_1268.field_5808);
            this.crystaling = true;
            this.resetPlace();
         }
      }

   }

   void normalCrystal() {
      mc.field_1690.field_1904.method_23481(false);
      if (this.readyToPlace() && this.isObi()) {
         MouseSimulation.mouseClick(1);
         this.resetPlace();
         this.placed = true;
      }

      if (this.readyToBreak() && this.isCrystal()) {
         if (!mc.field_1724.method_24828() && this.onGround.getValue()) {
            return;
         }

         if (this.noWeak.getValue() && mc.field_1724.method_6059(class_1294.field_5911)) {
            for(int i = 0; i < 9; ++i) {
               if (mc.field_1724.method_31548().method_5438(i).method_7909() instanceof class_1829) {
                  mc.field_1724.method_31548().field_7545 = i;
                  break;
               }
            }
         }

         label32: {
            class_239 var3 = mc.field_1765;
            if (var3 instanceof class_3966) {
               class_3966 hitResult = (class_3966)var3;
               class_1297 var5 = hitResult.method_17782();
               if (var5 instanceof class_1511) {
                  class_1511 crystal = (class_1511)var5;
                  mc.field_1761.method_2918(mc.field_1724, crystal);
                  mc.field_1724.method_6104(class_1268.field_5808);
                  break label32;
               }
            }

            MouseSimulation.mouseClick(0);
         }

         this.resetBreak();
         this.broke = true;
      }

   }

   void tick() {
      ++this.placeTick;
      ++this.breakTick;
   }

   boolean readyToBreak() {
      return this.breakTick >= MathUtils.randomInt((int)this.breakDelay.getMinValue(), Math.max((int)this.breakDelay.getMinValue() + 1, (int)this.breakDelay.getMaxValue()));
   }

   boolean readyToPlace() {
      return this.placeTick >= MathUtils.randomInt((int)this.placeDelay.getMinValue(), Math.max((int)this.placeDelay.getMinValue() + 1, (int)this.placeDelay.getMaxValue()));
   }

   void resetPlace() {
      this.placeTick = 0;
   }

   void resetBreak() {
      this.breakTick = 0;
   }

   public List<class_2338> findObsidianAroundPlayer(double radius) {
      ArrayList<class_2338> positions = new ArrayList();
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_638 world = mc.field_1687;
         class_2338 playerPos = mc.field_1724.method_24515();
         int range = (int)Math.ceil(radius);

         for(int x = -range; x <= range; ++x) {
            for(int z = -range; z <= range; ++z) {
               double distanceSquared = (double)(x * x + z * z);
               if (distanceSquared <= radius * radius) {
                  for(int y = -2; y <= 2; ++y) {
                     class_2338 pos = playerPos.method_10069(x, y, z);
                     if (world.method_8320(pos).method_26204() == class_2246.field_10540) {
                        positions.add(pos);
                     }
                  }
               }
            }
         }

         return positions;
      } else {
         return positions;
      }
   }

   public void onItemUse(ItemUseListener.ItemUseEvent event) {
   }

   public static enum CrystalMode {
      NORMAL,
      BLATANT,
      SILENT;

      // $FF: synthetic method
      private static CrystalClicker.CrystalMode[] $values() {
         return new CrystalClicker.CrystalMode[]{NORMAL, BLATANT, SILENT};
      }
   }
}
