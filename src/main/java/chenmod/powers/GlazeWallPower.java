package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GlazeWallPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(GlazeWallPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = true;

    public GlazeWallPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, 0);
        updateDescription();
    }

    @Override
    public void updateDescription() {

        this.description = this.amount > 0
                ? String.format(DESCRIPTIONS[1], this.amount, this.amount)
                : String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        this.amount = 0;
        this.flash();
        updateDescription();
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.amount += damageAmount;
            this.flash();
            updateDescription();
        }

        return 0;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {

        if (isPlayer && this.amount > 0){

            AbstractPower explosiveArtPower = this.owner.getPower(ExplosiveArtPower.POWER_ID);

            int selfDamageAmount = this.amount;

            int enemyDamageAmount = explosiveArtPower != null
                    ? (int)((explosiveArtPower.amount * 0.01f + 1) * this.amount)
                    : this.amount ;

            this.addToTop(new DamageAllEnemiesAction(null,
                    DamageInfo.createDamageMatrix(enemyDamageAmount, true),
                    DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL)
            );

            this.addToTop(new DamageAction(owner,
                    new DamageInfo(null, selfDamageAmount, DamageInfo.DamageType.THORNS)
            ));

            this.addToTop(new RemoveSpecificPowerAction(owner, owner, this));

        }
    }

}