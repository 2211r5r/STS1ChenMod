package chenmod.util;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.Map;

public class DistanceCache {

    private static final Map<AbstractMonster, Double> distanceMap = new HashMap<>();
    private static final Map<AbstractMonster, Float> minTimesMap = new HashMap<>();
    private static final Map<AbstractMonster, Float> maxTimesMap = new HashMap<>();

    private static double minDistance = 1.0;
    private static double maxDistance = 1.0;

    private static int lastMonsterCount = -1;

    /** 每帧调用（在 Mod 主类 receivePostUpdate 中） */
    public static void update() {

        int currentCount = AbstractDungeon.getCurrRoom().monsters.monsters.size();

        if (currentCount != lastMonsterCount) {
            rebuild();
            lastMonsterCount = currentCount;
        }
    }

    /** 重建缓存：计算每个怪物距离 + 最小距离 + 最大距离 + 预计算倍率 */
    public static void rebuild() {
        distanceMap.clear();
        minTimesMap.clear();
        maxTimesMap.clear();

        minDistance = Double.MAX_VALUE;
        maxDistance = 0.0;

        // 1. 计算距离
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {

            double dist = Math.sqrt(
                    Math.pow(m.hb.cX - AbstractDungeon.player.hb.cX, 2) +
                            Math.pow(m.hb.cY - AbstractDungeon.player.hb.cY, 2)
            );

            distanceMap.put(m, dist);

            if (dist < minDistance) minDistance = dist;
            if (dist > maxDistance) maxDistance = dist;
        }

        if (minDistance <= 0) minDistance = 1.0;
        if (maxDistance <= 0) maxDistance = 1.0;

        // 2. 预计算倍率
        for (AbstractMonster m : distanceMap.keySet()) {
            double dist = distanceMap.get(m);

            float minTimes = (float)Math.ceil(dist / minDistance * 100) / 100.0f;   // 距离越远倍率越大
            float maxTimes = (float)Math.ceil(maxDistance / dist * 100) / 100.0f;   // 距离越近倍率越大

            minTimesMap.put(m, minTimes);
            maxTimesMap.put(m, maxTimes);
        }

        ChenMod.logger.info("距离缓存建立：" + distanceMap);
        ChenMod.logger.info("【距离越远倍率越大】缓存建立：" + minTimesMap);
        ChenMod.logger.info("【距离越近倍率越大】缓存建立：" + maxTimesMap);
    }

    /** ① 从最小距离计算倍率（已预计算） */
    public static float getTimesFromMin(AbstractMonster m) {
        return minTimesMap.getOrDefault(m, 1.0f);
    }

    /** ② 从最大距离计算倍率（已预计算） */
    public static float getTimesFromMax(AbstractMonster m) {
        return maxTimesMap.getOrDefault(m, 1.0f);
    }

    /** ③ 默认使用 maxDistance 逻辑（距离越近伤害越高） */
    public static float getTimesFor(AbstractMonster m, boolean upgraded) {
        float times = getTimesFromMax(m);

        if (!upgraded) {
            times = Math.min(times, 1.5f);
        }

        return times;
    }
}
