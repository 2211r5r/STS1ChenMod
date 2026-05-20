package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class UlpianusPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(UlpianusPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public UlpianusPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, 0);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    public int onAttacked(DamageInfo info, int damageAmount) {

        if(this.amount > 0) {
            this.owner.heal(this.amount);
            this.flash();
        }

        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        this.amount = (int) (this.amount * 0.5f);
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if(card.baseBlock > 0){
            this.amount += 3;
            this.flash();
            updateDescription();
        }
    }

    @Override
    public float modifyBlockLast(final float blockAmount) {
        return 0.0f;
    }

}