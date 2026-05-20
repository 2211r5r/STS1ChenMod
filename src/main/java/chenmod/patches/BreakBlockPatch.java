package chenmod.patches;

import chenmod.powers.BreakBlockPower_player;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "damage"
)
public class BreakBlockPatch {

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"info", "damageAmount"}
    )
    public static void Insert(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {

        // 攻击者必须存在
        if (info.owner == null) return;

        // 攻击者必须拥有破甲 buff
        AbstractPower breakPower = info.owner.getPower(BreakBlockPower_player.POWER_ID);
        if (breakPower == null) return;

        // ★ 无实体优先：禁止破甲
        if (__instance.hasPower("Intangible") || __instance.hasPower("IntangiblePlayer")) {
            return;
        }

        // ★ 受击前的原始格挡
        int originalBlock = __instance.currentBlock;
        if (originalBlock <= 0) return;

        // ★ 破甲加成
        int bonus = (int)(originalBlock * breakPower.amount * 0.01f);

        damageAmount[0] += bonus;

        breakPower.flash();
    }

    // ★ Locator：告诉 ModTheSpire “插入到 decrementBlock 之前”
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {

            Matcher finalMatcher = new Matcher.MethodCallMatcher(
                    AbstractMonster.class, "decrementBlock"
            );

            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

