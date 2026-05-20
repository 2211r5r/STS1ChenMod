package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ZeroCostAllCardsThisTurnAction extends AbstractGameAction {

    public ZeroCostAllCardsThisTurnAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        for (AbstractCard c : p.hand.group) {
            c.costForTurn = 0;
            c.isCostModifiedForTurn = true;
        }

        this.isDone = true;
    }
}
