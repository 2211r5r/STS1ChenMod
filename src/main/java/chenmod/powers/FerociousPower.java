package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.List;

public class FerociousPower extends BasePower{

    public static final String POWER_ID = ChenMod.makeID(FerociousPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    private final List<String> powerIdList = new ArrayList<>();

    public FerociousPower(AbstractCreature owner, int amount){
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 只在自己获得 BUFF 时触发
        if (target == this.owner && power.type == PowerType.BUFF && !powerIdList.contains(power.ID)) {
            flash();
            AbstractDungeon.actionManager.addToBottom(
                    new DrawCardAction(owner, this.amount)
            );
            this.powerIdList.add(power.ID);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.powerIdList.clear();
    }

    public void updateDescription() {
        this.description = String.format(powerStrings.DESCRIPTIONS[0], this.amount);
    }

}
