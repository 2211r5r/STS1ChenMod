package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BenYePower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(BenYePower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = true;

    public BenYePower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            this.addToTop(new GainBlockAction(this.owner, this.amount));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if(isPlayer){
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

}