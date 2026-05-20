package chenmod.powers;

import chenmod.ChenMod;
import chenmod.actions.DoubleSwordsAction;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DoubleSwordsPower extends BasePower {
    public static final String POWER_ID = ChenMod.makeID(DoubleSwordsPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;

    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public DoubleSwordsPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if(card.hasTag(CustomTags.CHIXIAO)){
            this.addToBot(
                    new DoubleSwordsAction(
                        new DamageInfo(AbstractDungeon.player, this.amount, DamageInfo.DamageType.THORNS)
            ));
        }
    }

}
