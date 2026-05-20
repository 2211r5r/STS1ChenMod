package chenmod.actions;

import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PreparationAction extends AbstractGameAction {

    private final AbstractPlayer p;
    private final int improveMagicNumber;

    public PreparationAction(final int improveMagicNumber) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.improveMagicNumber = improveMagicNumber;
    }

    @Override
    public void update() {
        for (final AbstractCard c : this.p.hand.group) {
            if (c.hasTag(CustomTags.MULTIPLE_ATTACKS)) {
                // 修改基础值
                c.baseMagicNumber += this.improveMagicNumber;
                c.magicNumber = c.baseMagicNumber;

                // 标记为已修改（否则 UI 不会显示变化）
                c.isMagicNumberModified = true;

                c.superFlash();
                c.applyPowers();
                c.initializeDescription();
            }
        }
        this.isDone = true;
        this.tickDuration();
    }

}
