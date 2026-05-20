package chenmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

// 修正版：每次受伤时减少指定数值的伤害，回合开始时（抽牌前）移除
public class LieZhenPower_temp extends BasePower {
    // Power唯一ID
    public static final String POWER_ID = ChenMod.makeID(LieZhenPower_temp.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = true; //是都为回合制效果（回合结束后移除）

    // 构造方法2：自定义减伤数值（满足「若干点」的需求）
    public LieZhenPower_temp(AbstractCreature owner, final int amount) {

        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));

        updateDescription();
    }

    // 更新描述文本，关联实际的减伤数值
    @Override
    public void updateDescription() {
        // DESCRIPTIONS[0] 建议配置为："每次受到伤害时，减少#b%d点伤害。回合结束时移除。"
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 核心逻辑：受伤时减少伤害（完全对齐TungstenRod的实现逻辑）
    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.flash();
            return Math.max(0, damageAmount - this.amount);
        }
        return damageAmount;
    }

    // 回合开始时移除Power（避免跨回合残留）
    @Override
    public void atStartOfTurn(){
        this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }

}