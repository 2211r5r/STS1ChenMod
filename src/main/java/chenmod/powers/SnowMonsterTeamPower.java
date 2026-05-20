package chenmod.powers;

import chenmod.ChenMod;
import chenmod.cards.FrozenCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.Objects;

public class SnowMonsterTeamPower extends BasePower{
    public static final String POWER_ID = ChenMod.makeID(SnowMonsterTeamPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）
    public SnowMonsterTeamPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0]);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {

        boolean hasFrozenCard = AbstractDungeon.player.hand.group
                .stream()
                .anyMatch(c -> Objects.equals(c.cardID, FrozenCard.ID));

        if(hasFrozenCard && AbstractDungeon.player.hasPower(FrozenPower.POWER_ID)) {
            damage = damage * 1.5f;
        }

        return damage;
    }


}
