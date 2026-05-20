package chenmod.patches;

import chenmod.powers.EmbattlePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractCard.class,
        method = "cardPlayable"
)
public class EmbattleTargetPlayablePatch {
    @SpirePrefixPatch
    public static SpireReturn<Boolean> Prefix(AbstractCard __instance, AbstractMonster m) {

        // 只限制攻击牌
        if (__instance.type != AbstractCard.CardType.ATTACK) {
            return SpireReturn.Continue();
        }

        // AOE 或无目标攻击牌不限制
        if (__instance.target == AbstractCard.CardTarget.ALL_ENEMY ||
                __instance.target == AbstractCard.CardTarget.ALL ||
                __instance.target == AbstractCard.CardTarget.NONE) {
            return SpireReturn.Continue();
        }

        // m 为空 → UI 状态，不限制
        if (m == null) {
            return SpireReturn.Continue();
        }

        // 收集所有拥有嘲讽的怪物
        ArrayList<AbstractMonster> tauntMonsters = new ArrayList<>();
        int maxEmbattleAmt = 0;

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo != null &&
                    !mo.isDeadOrEscaped() &&
                    mo.hasPower(EmbattlePower.POWER_ID)) {

                int amt = mo.getPower(EmbattlePower.POWER_ID).amount;
                if (amt > 0) {
                    tauntMonsters.add(mo);
                    if (amt > maxEmbattleAmt) {
                        maxEmbattleAmt = amt;
                    }
                }
            }
        }

        // 没有嘲讽 → 正常使用
        if (tauntMonsters.isEmpty()) {
            return SpireReturn.Continue();
        }

        // 找出所有“最高嘲讽层数”的怪物
        ArrayList<AbstractMonster> highest = new ArrayList<>();
        for (AbstractMonster mo : tauntMonsters) {
            if (mo.getPower(EmbattlePower.POWER_ID).amount == maxEmbattleAmt) {
                highest.add(mo);
            }
        }

        // 如果 m 不在可攻击列表中 → 禁止并显示提示
        if (!highest.contains(m)) {
            __instance.cantUseMessage = "必须攻击拥有最高嘲讽的敌人！";
            return SpireReturn.Return(false);
        }

        return SpireReturn.Continue();
    }
}

