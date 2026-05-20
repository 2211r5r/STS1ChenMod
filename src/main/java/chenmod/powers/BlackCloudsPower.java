package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BlackCloudsPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(BlackCloudsPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public BlackCloudsPower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        updateDescription();
        this.amount2 = 1;
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
    public void atStartOfTurnPostDraw() {
        if(this.owner.powers.stream().anyMatch(p -> p.type == AbstractPower.PowerType.DEBUFF)){
            ChenMod.logger.info("存在负面效果，多摸牌并获得能量(行动点)");
            this.addToBot(new DrawCardAction(this.owner, this.amount));
            this.addToBot(new GainEnergyAction(this.amount2));
            this.flash();
        }
    }
}