package chenmod.patches;

import chenmod.ChenMod;
import chenmod.util.ChenModConfig;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheEnding;

import java.util.Collections;

public class BossPatch {

    /**
     * 刷新 Boss 图标（使用 BaseMod 的 BossInfo）
     */
    private static void refreshBossIcon() {
        BaseMod.BossInfo info = BaseMod.getBossInfo(AbstractDungeon.bossKey);
        if (info != null) {
            if (com.megacrit.cardcrawl.map.DungeonMap.boss != null) com.megacrit.cardcrawl.map.DungeonMap.boss.dispose();
            if (com.megacrit.cardcrawl.map.DungeonMap.bossOutline != null) com.megacrit.cardcrawl.map.DungeonMap.bossOutline.dispose();
            com.megacrit.cardcrawl.map.DungeonMap.boss = info.loadBossMap();
            com.megacrit.cardcrawl.map.DungeonMap.bossOutline = info.loadBossMapOutline();
            ChenMod.logger.info("[BossPatch] 图标刷新成功");
        } else {
            ChenMod.logger.warn("[BossPatch] 未找到 BossInfo，图标刷新失败");
        }
    }

    // ───────────────────────────────────────────────
    // 二层 TheCity
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheCity.class, method = "initializeBoss")
    public static class PatchCityBoss {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(TheCity __instance) {
            ChenMod.logger.info("[BossPatch] TheCity.initializeBoss() 覆盖逻辑触发");

            AbstractDungeon.bossList.clear();

            if (ChenModConfig.ADD_FROST_NOVA) {
                AbstractDungeon.bossList.add("White Rabbit");
                ChenMod.logger.info("[BossPatch][DEBUG] 添加 Boss: White Rabbit");
            }
            if (ChenModConfig.ADD_MEPHISO_AND_FAUST) {
                AbstractDungeon.bossList.add("Mephisto Faust");
                ChenMod.logger.info("[BossPatch][DEBUG] 添加 Boss: Mephisto Faust");
            }

            if (!ChenModConfig.REMOVE_ORANGEAL_BOSS) {
                AbstractDungeon.bossList.add("Automaton");
                AbstractDungeon.bossList.add("Collector");
                AbstractDungeon.bossList.add("Champ");
            }

            if (AbstractDungeon.bossList.isEmpty()) {
                AbstractDungeon.bossList.add("Automaton");
                AbstractDungeon.bossList.add("Collector");
                AbstractDungeon.bossList.add("Champ");
                ChenMod.logger.warn("[BossPatch] BossList 为空，恢复原版 Boss 列表");
            }

            Collections.shuffle(AbstractDungeon.bossList, new java.util.Random(AbstractDungeon.monsterRng.randomLong()));

            ChenMod.logger.info("[BossPatch][DEBUG] 最终 BossList → {}", AbstractDungeon.bossList);

            return SpireReturn.Return(null);
        }
    }

    // ───────────────────────────────────────────────
    // 三层 TheBeyond
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheBeyond.class, method = "initializeBoss")
    public static class PatchBeyondBoss {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(TheBeyond __instance) {
            ChenMod.logger.info("[BossPatch] TheBeyond.initializeBoss() 覆盖逻辑触发");

            AbstractDungeon.bossList.clear();

            if (ChenModConfig.ADD_BULDROKKASTEE) {
                AbstractDungeon.bossList.add("Death of a Patriot");
                ChenMod.logger.info("[BossPatch][DEBUG] 添加 Boss: Death of a Patriot");
            }
            if (ChenModConfig.ADD_MEPHISO_SINGER) {
                AbstractDungeon.bossList.add("Mephisto Singer");
                ChenMod.logger.info("[BossPatch][DEBUG] 添加 Boss: Mephisto Singer");
            }

            if (!ChenModConfig.REMOVE_ORANGEAL_BOSS) {
                AbstractDungeon.bossList.add("Awakened One");
                AbstractDungeon.bossList.add("Time Eater");
                AbstractDungeon.bossList.add("Donu and Deca");
            }

            if (AbstractDungeon.bossList.isEmpty()) {
                AbstractDungeon.bossList.add("Awakened One");
                AbstractDungeon.bossList.add("Time Eater");
                AbstractDungeon.bossList.add("Donu and Deca");
                ChenMod.logger.warn("[BossPatch] BossList 为空，恢复原版 Boss 列表");
            }

            Collections.shuffle(AbstractDungeon.bossList, new java.util.Random(AbstractDungeon.monsterRng.randomLong()));

            ChenMod.logger.info("[BossPatch][DEBUG] 最终 BossList → {}", AbstractDungeon.bossList);

            return SpireReturn.Return(null);
        }
    }

    // ───────────────────────────────────────────────
    // 四层 TheEnding
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheEnding.class, method = "initializeBoss")
    public static class PatchEndingBoss {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(TheEnding __instance) {
            ChenMod.logger.info("[BossPatch] TheEnding.initializeBoss() 覆盖逻辑触发");

            AbstractDungeon.bossList.clear();

            if (ChenModConfig.ADD_TALULAH) {
                AbstractDungeon.bossList.add("Scorching Sun");
                AbstractDungeon.bossList.add("Scorching Sun");
                AbstractDungeon.bossList.add("Scorching Sun");
                ChenMod.logger.info("[BossPatch][DEBUG] 添加 Boss: Scorching Sun");
            }

            if (!ChenModConfig.REMOVE_ORANGEAL_BOSS) {
                AbstractDungeon.bossList.add("The Heart");
                AbstractDungeon.bossList.add("The Heart");
                AbstractDungeon.bossList.add("The Heart");
            }

            if (AbstractDungeon.bossList.isEmpty()) {
                AbstractDungeon.bossList.add("The Heart");
                AbstractDungeon.bossList.add("The Heart");
                AbstractDungeon.bossList.add("The Heart");
                ChenMod.logger.warn("[BossPatch] BossList 为空，恢复原版 Boss 列表");
            }

            Collections.shuffle(AbstractDungeon.bossList, new java.util.Random(AbstractDungeon.monsterRng.randomLong()));

            ChenMod.logger.info("[BossPatch][DEBUG] 最终 BossList → {}", AbstractDungeon.bossList);

            return SpireReturn.Return(null);
        }
    }
}
