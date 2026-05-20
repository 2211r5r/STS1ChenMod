package chenmod.actions;

import chenmod.ChenMod;
import chenmod.powers.SeeYouTomorrowPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class SeeYouTomorrowAction extends AbstractGameAction
{
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private AbstractPlayer p;
    private static float DURATION;

    private final boolean isUpgraded;

    public SeeYouTomorrowAction(final AbstractCreature target, final AbstractCreature source, final int amount, final boolean isUpgraded) {
        this.setValues(target, source, amount);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = 0.5f;
        this.isUpgraded = isUpgraded;
        this.p = (AbstractPlayer)target;
    }

    @Override
    public void update() {
        if (this.duration != 0.5f) {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                final AbstractCard tmpCard = AbstractDungeon.handCardSelectScreen.selectedCards.getBottomCard();
                this.addToTop(new ApplyPowerAction(this.p, this.p, new SeeYouTomorrowPower(this.p, this.amount, tmpCard, this.isUpgraded)));
                AbstractDungeon.player.hand.moveToExhaustPile(tmpCard);
                AbstractDungeon.handCardSelectScreen.selectedCards.clear();
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
            this.addToTop(new ApplyPowerAction(this.p, this.p, new SeeYouTomorrowPower(this.p, this.amount, this.p.hand.getBottomCard(), this.isUpgraded)));
            AbstractDungeon.player.hand.moveToExhaustPile(this.p.hand.getBottomCard());
            this.isDone = true;
            return;
        }

        if(this.isUpgraded){
            AbstractDungeon.handCardSelectScreen.open(TEXT[1], 1, false, false);
        }else{
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false);
        }
        this.tickDuration();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(ChenMod.makeID("SeeYouTomorrowAction"));
        TEXT = uiStrings.TEXT;
        DURATION = Settings.ACTION_DUR_XFAST;
    }

}
