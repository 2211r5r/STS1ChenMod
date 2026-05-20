package chenmod.powers;

import chenmod.ChenMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

public class InterrogationPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(InterrogationPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.DEBUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = true;

    public InterrogationPower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        updateDescription();
    }

    @Override
    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_SHACKLE", 0.05f);
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void atEndOfTurn(boolean isPlayer) {

        if(!isPlayer){

            this.amount --;
            this.flash();
            CardCrawlGame.sound.play("POWER_SHACKLE", 0.05f);
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, 1), 1));

            if (this.amount == 0) {
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            }
        }

        updateDescription();
    }
}