package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.GainGoldTextEffect;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class TowerFiercePower extends BasePower{
    public static final String POWER_ID = ChenMod.makeID(TowerFiercePower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public TowerFiercePower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }


    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount, this.amount, this.amount);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {

        float exDamage = Math.max(1.0f, damage * this.amount / 100.0f);

        return type == DamageInfo.DamageType.NORMAL ? damage + exDamage : damage;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {

        float exDamage = Math.max(1.0f, damage * this.amount / 100.0f);

        return damageType == DamageInfo.DamageType.NORMAL ? damage + exDamage : damage;
    }

    @Override
    public void onDeath() {

        CardCrawlGame.sound.play("GOLD_JINGLE");

        for (int i = 0; i < this.amount; i++) {
            AbstractDungeon.effectList.add(
                    new GainPennyEffect(
                            AbstractDungeon.player,
                            this.owner.hb.cX, this.owner.hb.cY,
                            AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY,
                            true
                    )
            );
        }

//      AbstractDungeon.effectsQueue.add(new GainGoldTextEffect(0));

        AbstractDungeon.player.gainGold(this.amount);

        this.addToTop(new HealAction(AbstractDungeon.player, this.owner, this.amount));
    }

}
