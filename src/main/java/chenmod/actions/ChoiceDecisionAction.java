package chenmod.actions;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ChoiceDecisionAction extends AbstractGameAction {
    private AbstractPlayer p;
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    public ChoiceDecisionAction() {
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.duration != Settings.ACTION_DUR_FAST) {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                for (final AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    if (c.cost > 0) {
                        c.cost = 0;
                        c.costForTurn = 0;
                        c.isCostModified = true;
                    }
                    this.p.hand.addToTop(c);
                }
                AbstractDungeon.player.hand.refreshHandLayout();
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
            this.tickDuration();
            return;
        }
        if (this.p.hand.isEmpty()) {
            this.isDone = true;
            return;
        }
        if (this.p.hand.size() == 1) {
            final AbstractCard c2 = this.p.hand.getTopCard();
            if (c2.cost > 0) {
                c2.cost = 0;
                c2.costForTurn = 0;
                c2.isCostModified = true;
            }
            this.p.hand.addToTop(c2);
            AbstractDungeon.player.hand.refreshHandLayout();
            this.isDone = true;
            return;
        }
        AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
        this.tickDuration();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(ChenMod.makeID("ChoiceDecisionAction"));
        TEXT = uiStrings.TEXT;
    }
}
