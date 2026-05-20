package chenmod.powers;

import chenmod.ChenMod;
import chenmod.cards.FrozenCard;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.Objects;

public class FrozenPower extends BasePower{
    public static final String POWER_ID = ChenMod.makeID(FrozenPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public FrozenPower(AbstractCreature owner){
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        boolean hasFrozenCard = AbstractDungeon.player.hand.group
                .stream()
                .anyMatch(c -> Objects.equals(c.cardID, FrozenCard.ID));

        // 如果手里有 FrozenCard，则禁止打出非状态牌
        return !hasFrozenCard || card.type == AbstractCard.CardType.STATUS;
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

}
