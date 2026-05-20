package chenmod.powers;

import chenmod.ChenMod;
import chenmod.util.DistanceCache;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LastMarchPower extends BasePower{

    public static final String POWER_ID = ChenMod.makeID(LastMarchPower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public LastMarchPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {

        float times = DistanceCache.getTimesFromMax((AbstractMonster) this.owner);

        return type == DamageInfo.DamageType.NORMAL ? damage * Math.min(times, 1.5f) : damage;
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0]);
    }

}
