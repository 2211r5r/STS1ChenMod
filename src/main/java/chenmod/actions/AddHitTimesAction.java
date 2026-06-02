package chenmod.actions;

import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AddHitTimesAction extends AbstractGameAction {

    private final AbstractPlayer p;
    private final int improveMagicNumber;
    private final AbstractCard c;

    public AddHitTimesAction(AbstractCard card, final int improveMagicNumber) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.improveMagicNumber = improveMagicNumber;
        this.c = card;
    }

    public AddHitTimesAction(AbstractCard card) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.improveMagicNumber = 1;
        this.c = card;
    }

    @Override
    public void update() {
        if (c.type == AbstractCard.CardType.ATTACK && c.hasTag(CustomTags.MULTIPLE_ATTACKS)) {
            // 修改基础值
            c.baseMagicNumber += this.improveMagicNumber;
            c.magicNumber = c.baseMagicNumber;

            // 标记为已修改（否则 UI 不会显示变化）
            c.isMagicNumberModified = true;

            c.superFlash();
            c.applyPowers();
            c.initializeDescription();
        }

        this.isDone = true;
        this.tickDuration();
    }

}