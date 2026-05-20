package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

// 修正版：每次受伤时减少指定数值的伤害，回合结束移除
public class LieZhenPower extends BasePower {
    // Power唯一ID
    public static final String POWER_ID = ChenMod.makeID(LieZhenPower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public LieZhenPower(AbstractCreature owner, final int amount) {

        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));

        updateDescription();
    }

    @Override
    public void updateDescription() {
        // DESCRIPTIONS[0] 建议配置为："每次受到伤害时，减少#b%d点伤害。回合结束时移除。"
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {

        if(info.type == DamageInfo.DamageType.HP_LOSS) return damageAmount;

        if (damageAmount > 0) {
            this.flash();
            return Math.max(0, damageAmount - this.amount);
        }
        return damageAmount;
    }

}