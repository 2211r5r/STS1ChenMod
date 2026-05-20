package chenmod.actions;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;

import java.util.Iterator;

public class DiscardPileToTopOfDeckAndUpgradeAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private AbstractPlayer p;

    public DiscardPileToTopOfDeckAndUpgradeAction(AbstractCreature source) {
        this.p = AbstractDungeon.player;
        this.setValues((AbstractCreature)null, source, this.amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    public void update() {
        if (AbstractDungeon.getCurrRoom().isBattleEnding()) {
            this.isDone = true;
        } else {
            if (this.duration == Settings.ACTION_DUR_FASTER) {
                if (this.p.discardPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                if (this.p.discardPile.size() == 1) {
                    AbstractCard tmp = this.p.discardPile.getTopCard();
                    this.p.discardPile.removeCard(tmp);
                    if(tmp.canUpgrade()){
                        tmp.upgrade();
                        AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        AbstractDungeon.effectsQueue.add(new ShowCardAndAddToDrawPileEffect(
                                tmp.makeStatEquivalentCopy(), // 展示的卡牌副本
                                Settings.WIDTH / 2.0f,        // 起始位置 X
                                Settings.HEIGHT / 2.0f,       // 起始位置 Y
                                false,                        // 是否随机插入
                                false,                        // 是否显示复制
                                false                         // 是否洗牌
                        ));
                    }else{
                        this.p.discardPile.moveToDeck(tmp, false);
                    }
                }

                if (this.p.discardPile.group.size() > this.amount) {
                    AbstractDungeon.gridSelectScreen.open(this.p.discardPile, 1, TEXT[0], false, false, false, false);
                    this.tickDuration();
                    return;
                }
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                Iterator var3 = AbstractDungeon.gridSelectScreen.selectedCards.iterator();

                while(var3.hasNext()) {
                    AbstractCard c = (AbstractCard)var3.next();
                    this.p.discardPile.removeCard(c);
                    if(c.canUpgrade()){
                        c.upgrade();
                        AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                        // 请补充 特效
                        AbstractDungeon.effectsQueue.add(new ShowCardAndAddToDrawPileEffect(
                                c.makeStatEquivalentCopy(), // 展示的卡牌副本
                                Settings.WIDTH / 2.0f,        // 起始位置 X
                                Settings.HEIGHT / 2.0f,       // 起始位置 Y
                                false,                        // 是否随机插入
                                false,                        // 是否显示复制
                                false                         // 是否洗牌
                        ));
                    }else{
                        this.p.hand.moveToDeck(c, false);
                    }
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.tickDuration();
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(ChenMod.makeID("DiscardPileToTopOfDeckAndUpgradeAction"));
        TEXT = uiStrings.TEXT;
    }
}
