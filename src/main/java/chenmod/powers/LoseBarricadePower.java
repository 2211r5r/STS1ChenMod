package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BarricadePower;

public class LoseBarricadePower extends BasePower{

    public static final String POWER_ID = ChenMod.makeID(LoseBarricadePower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public LoseBarricadePower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0]);
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.owner != this.owner && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            this.flash();
            if(this.owner.hasPower(BarricadePower.POWER_ID)){
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, BarricadePower.POWER_ID));
            }
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }


}
