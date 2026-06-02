package chenmod.actions;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.*;

public class AddCostForTurnAction extends AbstractGameAction
{
    private final AbstractCard targetCard;

    public AddCostForTurnAction(final AbstractCard card, final int amt) {
        this.targetCard = card;
        this.amount = amt;
        this.startDuration = Settings.ACTION_DUR_FASTER;
        this.duration = this.startDuration;
    }

    public AddCostForTurnAction(final AbstractCard card) {
        this.targetCard = card;
        this.amount = 1;
        this.startDuration = Settings.ACTION_DUR_FASTER;
        this.duration = this.startDuration;
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration && this.targetCard.costForTurn >= 0 && this.targetCard.cost >= 0) {
            this.targetCard.setCostForTurn(this.targetCard.costForTurn + this.amount);
        }
        this.tickDuration();
        if (Settings.FAST_MODE) {
            this.isDone = true;
        }
    }
}
