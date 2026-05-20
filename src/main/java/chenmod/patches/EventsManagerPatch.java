package chenmod.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.*;
import chenmod.ChenMod;

import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

public class EventsManagerPatch {

    /** 存储每层的事件覆盖信息 */
    private static final HashMap<String, EventOverride> overrideMap = new HashMap<>();

    /** 覆盖信息结构体 */
    public static class EventOverride {
        public final List<String> eventIDs;
        public final BooleanSupplier condition;

        public EventOverride(List<String> eventIDs, BooleanSupplier condition) {
            this.eventIDs = eventIDs;
            this.condition = condition;
        }
    }

    /**
     * 注册一个事件覆盖（带条件）
     * 示例：
     * EventsManager.overrideEvents(TheCity.ID, Arrays.asList("MyEvent1", "MyEvent2"), () -> true);
     */
    public static void overrideEvents(String dungeonID, List<String> eventIDs, BooleanSupplier condition) {
        ChenMod.logger.info("[EventsManager] 注册事件覆盖: 层={} → 事件={}", dungeonID, eventIDs);
        overrideMap.put(dungeonID, new EventOverride(eventIDs, condition));
    }

    /** 获取某层的覆盖信息 */
    public static EventOverride getOverride(String dungeonID) {
        return overrideMap.get(dungeonID);
    }

    /** 实际替换事件池 */
    private static void applyOverride(String dungeonID) {
        EventOverride o = getOverride(dungeonID);

        if (o == null) {
            ChenMod.logger.info("[EventsManager] 无事件覆盖规则: 层={}", dungeonID);
            return;
        }

        boolean ok = o.condition.getAsBoolean();
        ChenMod.logger.info("[EventsManager] 条件检测: {}", ok);

        if (!ok) {
            ChenMod.logger.info("[EventsManager] 条件不满足，不覆盖事件池");
            return;
        }

        ChenMod.logger.info("[EventsManager] 覆盖事件池 → {}", o.eventIDs);

        AbstractDungeon.eventList.clear();
        AbstractDungeon.eventList.addAll(o.eventIDs);

        ChenMod.logger.info("[EventsManager] 事件池覆盖完成: {}", AbstractDungeon.eventList);
    }

    // ───────────────────────────────────────────────
    // Patch：第一层 Exordium
    // ───────────────────────────────────────────────
    @SpirePatch(clz = Exordium.class, method = "initializeEventList")
    public static class PatchExordiumEvents {
        @SpirePostfixPatch
        public static void postfix(Exordium __instance) {
            ChenMod.logger.info("[EventsManager] Exordium.initializeEventList() 触发");
            applyOverride(Exordium.ID);
        }
    }

    // ───────────────────────────────────────────────
    // Patch：第二层 TheCity
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheCity.class, method = "initializeEventList")
    public static class PatchCityEvents {
        @SpirePostfixPatch
        public static void postfix(TheCity __instance) {
            ChenMod.logger.info("[EventsManager] TheCity.initializeEventList() 触发");
            applyOverride(TheCity.ID);
        }
    }

    // ───────────────────────────────────────────────
    // Patch：第三层 TheBeyond
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheBeyond.class, method = "initializeEventList")
    public static class PatchBeyondEvents {
        @SpirePostfixPatch
        public static void postfix(TheBeyond __instance) {
            ChenMod.logger.info("[EventsManager] TheBeyond.initializeEventList() 触发");
            applyOverride(TheBeyond.ID);
        }
    }

    // ───────────────────────────────────────────────
    // Patch：第四层 TheEnding
    // ───────────────────────────────────────────────
    @SpirePatch(clz = TheEnding.class, method = "initializeEventList")
    public static class PatchEndingEvents {
        @SpirePostfixPatch
        public static void postfix(TheEnding __instance) {
            ChenMod.logger.info("[EventsManager] TheEnding.initializeEventList() 触发");
            applyOverride(TheEnding.ID);
        }
    }
}
