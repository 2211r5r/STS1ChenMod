package chenmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import chenmod.ChenMod;
import chenmod.cards.YuanShiBaoFaCard;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

// 修正版：每次受伤时减少指定数值的伤害，回合开始时（抽牌前）移除
public class YuanShiBaoFaPower extends BasePower {
    // Power唯一ID
    public static final String POWER_ID = ChenMod.makeID(YuanShiBaoFaPower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public boolean isUpgraded = false;

    public YuanShiBaoFaPower(AbstractCreature owner, final int amount, boolean isUpgraded) {

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

    // 回合开始时且抽排后，向手牌中添加若干张【源石爆发】
    @Override
    public void atStartOfTurnPostDraw(){

        this.addToBot(new MakeTempCardInHandAction(new YuanShiBaoFaCard(this.isUpgraded), this.amount));

    }

}