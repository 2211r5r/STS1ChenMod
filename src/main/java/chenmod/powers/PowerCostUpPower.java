package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PowerCostUpPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(PowerCostUpPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.DEBUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = true;

    public PowerCostUpPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0]);
    }

    @Override
    public void onInitialApplication() {
        // 应用时立即修改当前手牌
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type == AbstractCard.CardType.POWER && c.cost >= 0) {
                c.setCostForTurn(c.costForTurn + 1);
                c.isCostModifiedForTurn = true;
            }
        }
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        // 新抽到的牌也要加费
        if (card.type == AbstractCard.CardType.POWER && card.cost >= 0) {
            card.setCostForTurn(card.costForTurn + 1);
            card.isCostModifiedForTurn = true;
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        // 回合结束后移除自己
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}