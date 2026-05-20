package chenmod.powers;

import chenmod.ChenMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

public class BrokenScabbardPower extends BasePower{

    public static final String POWER_ID = ChenMod.makeID(BrokenScabbardPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //为回合制效果（回合结束后移除）

    private int counter = 0;

    private boolean justApplied = false;

    public BrokenScabbardPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onInitialApplication() {
        this.justApplied = true;
//        updateHandCosts();
    }

//    @Override
//    public void stackPower(int stackAmount) {
//        super.stackPower(stackAmount);
//        updateHandCosts();
//    }

    @Override
    public void onCardDraw(AbstractCard c) {

        if(this.counter >= this.amount) {
            this.flash();
            return;
        }

        if (c.cost < 0) { // X牌 或 不能打出的牌
            return;
        }

        c.costForTurn = 0;

        c.isCostModifiedForTurn = (c.costForTurn != c.cost);

        this.counter++;
    }

    private void updateHandCosts() {

        for (AbstractCard c : ((AbstractPlayer) this.owner).hand.group) {

            if (c.cost < 0) { // X牌 或 不能打出的牌
                continue;
            }
            c.costForTurn = 0;

            c.isCostModifiedForTurn = (c.costForTurn != c.cost);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {

            if(this.justApplied) {
                this.justApplied = false;
                return;
            }

            this.amount--;
            this.counter = 0;

            this.flash();

            resetHandCosts();
            updateDescription();

            if (this.amount == 0) {
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            }else {
                AbstractDungeon.effectList.add(
                        new TextAboveCreatureEffect(
                                ((AbstractPlayer) this.owner).hb.cX,
                                ((AbstractPlayer) this.owner).hb.cY + 50.0F,
                                DESCRIPTIONS[2],
                                Color.RED
                        )
                );
            }
        }

    }

    private void resetHandCosts() {
        for (AbstractCard c : ((AbstractPlayer) this.owner).hand.group) {
            c.costForTurn = c.cost;
            c.isCostModifiedForTurn = false;
        }
    }
}
