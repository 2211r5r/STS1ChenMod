package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RecastAction extends AbstractGameAction
{
    private int counter;

    public RecastAction(final AbstractCreature source) {
        this.source = source;
        this.duration = Settings.ACTION_DUR_FAST;
        this.counter = 0;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (final AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type == AbstractCard.CardType.ATTACK) {
                    this.addToTop(new DiscardSpecificCardAction(c));
                    counter++;
                }
            }

            if(counter > 0){
                this.addToBot(new DrawCardAction(counter));
                this.addToBot(new GainBlockAction(source, counter));
            }

            this.isDone = true;
        }
    }
}