package chenmod.powers;

import chenmod.ChenMod;
import chenmod.monsters.Mephisto;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.Objects;

public class FocusPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(FocusPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = true;

    public FocusPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        this.amount2 = 1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount, this.amount2);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.amount2++;
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action){

        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() < 2){
            ChenMod.logger.info("【FocusPower】onAfterUseCard()--牌还是用的太少了。");
            return;
        }

        AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2);
        AbstractCard thisCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);

        if(thisCard.type == lastCard.type) {
            this.addToBot(new DrawCardAction(this.amount));
        }

        ChenMod.logger.info("【thisCard.originalName】=" + thisCard.originalName + "; 【lastCard.originalName】=" + lastCard.originalName);

        if(Objects.equals(thisCard.originalName, lastCard.originalName)) {
            this.addToBot(new GainEnergyAction(this.amount2));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if(isPlayer){
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }
}