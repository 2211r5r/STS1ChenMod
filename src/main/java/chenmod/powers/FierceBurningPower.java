package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class FierceBurningPower extends BasePower{

    // Power唯一ID
    public static final String POWER_ID = ChenMod.makeID(FierceBurningPower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    private static final boolean TURN_BASED = true;

    public FierceBurningPower(AbstractCreature owner, final int amount) {

        super(POWER_ID, TYPE, TURN_BASED, owner, amount);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {

        ChenMod.logger.info("烈焰分析应该减少一层");

        if(!isPlayer){
            return;
        }

        if (this.amount == 1) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }

    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * 0.5F;
        } else {
            return damage;
        }
    }
}
