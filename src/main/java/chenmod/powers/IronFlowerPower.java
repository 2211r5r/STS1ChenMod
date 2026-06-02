package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class IronFlowerPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(IronFlowerPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public IronFlowerPower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
    }

    @Override
    public void updateDescription() {
        this.description = this.owner.currentHealth > (int)(this.owner.maxHealth * 0.5f)
                ? String.format(DESCRIPTIONS[1], this.amount)
                : String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {
            this.flashWithoutSound();
            this.owner.heal(this.amount);
        }
    }

    @Override
    public int onHeal(int healAmount) {

        if (this.owner.currentHealth > (int)(this.owner.maxHealth * 0.5f)){
            this.flashWithoutSound();
            this.owner.addBlock(healAmount);
            return 0;
        }

        return healAmount;
    }

    @Override
    public void update(int slot){
        super.update(slot);
        updateDescription();
    }
}