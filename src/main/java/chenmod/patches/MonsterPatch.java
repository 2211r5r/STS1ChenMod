package chenmod.patches;

import chenmod.ChenMod;
import chenmod.util.ChenModConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import java.util.ArrayList;

public class MonsterPatch {

//    @SpirePatch(clz = TheCity.class, method = "generateWeakEnemies")
//    public static class PatchTheCityWeakEnemies {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> prefix(TheCity __instance, int count) {
//            ChenMod.logger.info("[MonsterPatch] 覆盖 generateWeakEnemies()");
//
//            ArrayList<MonsterInfo> monsters = new ArrayList<>();
//
//            if (ChenModConfig.CHANGE_MONSTERS) {
//                // ====== 你的自定义弱怪 ======
//                monsters.add(new MonsterInfo("Spheric Guardian", 2.0F));
//                monsters.add(new MonsterInfo("Chosen", 2.0F));
//                monsters.add(new MonsterInfo("Shell Parasite", 2.0F));
//                monsters.add(new MonsterInfo("3 Byrds", 2.0F));
//                monsters.add(new MonsterInfo("2 Thieves", 2.0F));
//                ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 添加弱怪: My Weak Monster A");
//            }else{
//                // ====== 是否保留原版弱怪 ======
//                monsters.add(new MonsterInfo("Spheric Guardian", 2.0F));
//                monsters.add(new MonsterInfo("Chosen", 2.0F));
//                monsters.add(new MonsterInfo("Shell Parasite", 2.0F));
//                monsters.add(new MonsterInfo("3 Byrds", 2.0F));
//                monsters.add(new MonsterInfo("2 Thieves", 2.0F));
//            }
//
//            MonsterInfo.normalizeWeights(monsters);
//            __instance.populateMonsterList(monsters, count, false);
//
//            ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 最终弱怪列表 → {}", monsters);
//
//            return SpireReturn.Return(null);
//        }
//    }
//
//    @SpirePatch(clz = TheCity.class, method = "generateStrongEnemies")
//    public static class PatchTheCityStrongEnemies {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> prefix(TheCity __instance, int count) {
//            ChenMod.logger.info("[MonsterPatch] 覆盖 TheCity generateStrongEnemies()");
//
//            ArrayList<MonsterInfo> monsters = new ArrayList<>();
//
//            // ====== 你的自定义强怪 ======
//            if (ChenModConfig.CHANGE_MONSTERS) {
//                monsters.add(new MonsterInfo("Chosen and Byrds", 2.0F));
//                monsters.add(new MonsterInfo("Sentry and Sphere", 2.0F));
//                monsters.add(new MonsterInfo("Snake Plant", 6.0F));
//                monsters.add(new MonsterInfo("Snecko", 4.0F));
//                monsters.add(new MonsterInfo("Centurion and Healer", 6.0F));
//                monsters.add(new MonsterInfo("Cultist and Chosen", 3.0F));
//                monsters.add(new MonsterInfo("3 Cultists", 3.0F));
//                monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
//                ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 添加自定义强怪");
//            }else{
//                monsters.add(new MonsterInfo("Chosen and Byrds", 2.0F));
//                monsters.add(new MonsterInfo("Sentry and Sphere", 2.0F));
//                monsters.add(new MonsterInfo("Snake Plant", 6.0F));
//                monsters.add(new MonsterInfo("Snecko", 4.0F));
//                monsters.add(new MonsterInfo("Centurion and Healer", 6.0F));
//                monsters.add(new MonsterInfo("Cultist and Chosen", 3.0F));
//                monsters.add(new MonsterInfo("3 Cultists", 3.0F));
//                monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
//            }
//
//            MonsterInfo.normalizeWeights(monsters);
//
//            // 排除逻辑
//            ArrayList<String> exclusions = new ArrayList<>();
//            String last = AbstractDungeon.monsterList.get(AbstractDungeon.monsterList.size() - 1);
//
//            if(ChenModConfig.CHANGE_MONSTERS){
//                // 在这里写自己的
//                switch (last) {
//                    case "Spheric Guardian":
//                        exclusions.add("Sentry and Sphere");
//                        break;
//                    case "3 Byrds":
//                        exclusions.add("Chosen and Byrds");
//                        break;
//                    case "Chosen":
//                        exclusions.add("Chosen and Byrds");
//                        exclusions.add("Cultist and Chosen");
//                        break;
//                }
//            }else{
//                // 原版的逻辑
//                switch (last) {
//                    case "Spheric Guardian":
//                        exclusions.add("Sentry and Sphere");
//                        break;
//                    case "3 Byrds":
//                        exclusions.add("Chosen and Byrds");
//                        break;
//                    case "Chosen":
//                        exclusions.add("Chosen and Byrds");
//                        exclusions.add("Cultist and Chosen");
//                        break;
//                }
//            }
//
//            // 原版逻辑：先 populateFirstStrongEnemy，再 populateMonsterList
//            __instance.populateFirstStrongEnemy(monsters, exclusions);
//            __instance.populateMonsterList(monsters, count, false);
//
//            ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 最终强怪列表 → {}", monsters);
//
//            return SpireReturn.Return(null);
//        }
//    }
//
//    @SpirePatch(clz = TheCity.class, method = "generateElites")
//    public static class PatchTheCityElites {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> prefix(TheCity __instance, int count) {
//            ChenMod.logger.info("[MonsterPatch] 覆盖 TheCity generateElites()");
//
//            ArrayList<MonsterInfo> monsters = new ArrayList<>();
//
//            // ====== 你的自定义精英 ======
//            if (ChenModConfig.CHANGE_MONSTERS) {
//                monsters.add(new MonsterInfo("Gremlin Leader", 1.0F));
//                monsters.add(new MonsterInfo("Slavers", 1.0F));
//                monsters.add(new MonsterInfo("Book of Stabbing", 1.0F));
//                ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 更改精英.");
//            }else{
//                monsters.add(new MonsterInfo("Gremlin Leader", 1.0F));
//                monsters.add(new MonsterInfo("Slavers", 1.0F));
//                monsters.add(new MonsterInfo("Book of Stabbing", 1.0F));
//                ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 保留了原版的精英.");
//            }
//
//            MonsterInfo.normalizeWeights(monsters);
//            __instance.populateMonsterList(monsters, count, true);
//
//            ChenMod.logger.info("[MonsterPatch][DEBUG] TheCity 最终精英列表 → {}", monsters);
//
//            return SpireReturn.Return(null);
//        }
//    }

    @SpirePatch(clz = TheBeyond.class, method = "generateElites")
    public static class PatchTheBeyondElites {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(TheBeyond __instance, int count) {
            ChenMod.logger.info("[MonsterPatch] 覆盖 TheBeyond generateElites()");

            ArrayList<MonsterInfo> monsters = new ArrayList<>();

            // ====== 你的自定义精英 ======
            if (ChenModConfig.CHANGE_MONSTERS) {
                monsters.add(new MonsterInfo("Frantic Zombies", 2.0F));
                monsters.add(new MonsterInfo("Zombie Sarkaz Centurion", 2.0F));
                monsters.add(new MonsterInfo("Guerrilla Team", 2.0F));
                ChenMod.logger.info("[MonsterPatch][DEBUG] TheBeyond 更改精英.");
            }else{
                monsters.add(new MonsterInfo("Giant Head", 2.0F));
                monsters.add(new MonsterInfo("Nemesis", 2.0F));
                monsters.add(new MonsterInfo("Reptomancer", 2.0F));
                ChenMod.logger.info("[MonsterPatch][DEBUG] TheBeyond 保留了原版的精英.");
            }

            MonsterInfo.normalizeWeights(monsters);
            __instance.populateMonsterList(monsters, count, true);

            ChenMod.logger.info("[MonsterPatch][DEBUG] TheBeyond 最终精英列表 → {}", monsters);

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = TheEnding.class, method = "generateMonsters")
    public static class PatchEndingMonsters {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(TheEnding __instance) {
            ChenMod.logger.info("[EndingPatch] 覆盖 TheEnding.generateMonsters()");

            // ====== 自定义普通怪物池 ======
            AbstractDungeon.monsterList = new ArrayList<>();

            if (ChenModConfig.CHANGE_MONSTERS) {
                AbstractDungeon.monsterList.add("Mercenary Sarkaz");
                AbstractDungeon.monsterList.add("Mercenary Sarkaz");
                AbstractDungeon.monsterList.add("Mercenary Sarkaz");
                ChenMod.logger.info("[EndingPatch][DEBUG] 添加终幕精英: Mercenary Sarkaz");
            }else{
                AbstractDungeon.monsterList.add("Shield and Spear");
                AbstractDungeon.monsterList.add("Shield and Spear");
                AbstractDungeon.monsterList.add("Shield and Spear");
            }

            // ====== 自定义精英怪物池 ======
            AbstractDungeon.eliteMonsterList = new ArrayList<>();

            if (ChenModConfig.CHANGE_MONSTERS) {
                AbstractDungeon.eliteMonsterList.add("Mercenary Sarkaz");
                AbstractDungeon.eliteMonsterList.add("Mercenary Sarkaz");
                AbstractDungeon.eliteMonsterList.add("Mercenary Sarkaz");
                ChenMod.logger.info("[EndingPatch][DEBUG] 修改 TheEnding 精英: Mercenary Sarkaz");
            }else{
                AbstractDungeon.eliteMonsterList.add("Shield and Spear");
                AbstractDungeon.eliteMonsterList.add("Shield and Spear");
                AbstractDungeon.eliteMonsterList.add("Shield and Spear");
            }

            ChenMod.logger.info("[EndingPatch][DEBUG] TheEnding 怪物列表 → {}", AbstractDungeon.monsterList);
            ChenMod.logger.info("[EndingPatch][DEBUG] TheEnding 精英列表 → {}", AbstractDungeon.eliteMonsterList);

            return SpireReturn.Return(null);
        }
    }


}
