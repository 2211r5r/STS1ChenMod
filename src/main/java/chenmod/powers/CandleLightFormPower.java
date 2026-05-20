package chenmod.powers;

import chenmod.ChenMod;
import chenmod.cards.CandleLightAttackCard;
import chenmod.cards.YuanShiBaoFaCard;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CandleLightFormPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(CandleLightFormPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public boolean isUpgraded;

    public CandleLightFormPower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        this.isUpgraded = false;
        updateDescription();
    }

    public CandleLightFormPower(AbstractCreature owner, final int amount, boolean isUpgraded) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        this.isUpgraded = isUpgraded;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = this.isUpgraded
                ? String.format(DESCRIPTIONS[1], this.amount)
                : String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void atStartOfTurnPostDraw(){
        this.addToBot(new MakeTempCardInHandAction(new CandleLightAttackCard(this.isUpgraded), this.amount));
    }
}