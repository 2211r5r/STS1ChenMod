package chenmod.actions;

import chenmod.cards.StorageCard;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class LeiFengAction extends AbstractGameAction
{
    private int counter;
    private final boolean isUpgraded;

    public LeiFengAction(final AbstractCreature source, final boolean isUpgraded) {
        this.source = source;
        this.duration = Settings.ACTION_DUR_FAST;
        this.counter = 0;
        this.isUpgraded = isUpgraded;
    }

    public LeiFengAction(final AbstractCreature source) {
        this.source = source;
        this.duration = Settings.ACTION_DUR_FAST;
        this.counter = 0;
        this.isUpgraded = false;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (final AbstractCard c : AbstractDungeon.player.hand.group) {
                if (!c.hasTag(CustomTags.CHIXIAO)) {
                    this.addToTop(new DiscardSpecificCardAction(c));
                    counter++;
                }
            }

            if(counter > 0){
                this.addToBot(new MakeTempCardInHandAction(new StorageCard(isUpgraded),counter));
            }

            this.isDone = true;
        }
    }
}