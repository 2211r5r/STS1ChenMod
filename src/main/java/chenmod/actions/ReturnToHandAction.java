package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ReturnToHandAction extends AbstractGameAction {

    private final AbstractPlayer p;
    private final AbstractCard c;
    public ReturnToHandAction(AbstractCard c) {
        this.p = AbstractDungeon.player;
        this.c = c;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.CARD_MANIPULATION;
    }
    @Override
    public void update() {
        // 如果手牌未满
        if (p.hand.size() < 10) {
            p.hand.addToTop(c);
            AbstractDungeon.player.hand.refreshHandLayout();
        }
        this.isDone = true;

    }
}
