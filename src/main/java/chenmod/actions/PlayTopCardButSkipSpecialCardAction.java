package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PlayTopCardButSkipSpecialCardAction extends AbstractGameAction {
    private final boolean exhaustCards;
    private final AbstractCard skipCard; // 要跳过的牌（按 originalName 判断）

    public PlayTopCardButSkipSpecialCardAction(AbstractCreature target, boolean exhausts, AbstractCard skipCard) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.WAIT;
        this.source = AbstractDungeon.player;
        this.target = target;
        this.exhaustCards = exhausts;
        this.skipCard = skipCard;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {

            // 1. 整个牌堆（抽牌 + 弃牌）都没牌 → 直接结束
            if (AbstractDungeon.player.drawPile.size() + AbstractDungeon.player.discardPile.size() == 0) {
                this.isDone = true;
                return;
            }

            // 2. 在当前 drawPile 中，从顶往下找第一张“不是 skipCard”的牌
            AbstractCard cardToPlay = null;
            for (int i = AbstractDungeon.player.drawPile.size() - 1; i >= 0; i--) {
                AbstractCard c = AbstractDungeon.player.drawPile.group.get(i);
                if (!c.originalName.equals(skipCard.originalName)) {
                    cardToPlay = c;
                    break;
                }
            }

            // 3. 当前 drawPile 里没有可打的牌
            if (cardToPlay == null) {
                // 如果弃牌堆有牌 → 洗牌后再试一次
                if (!AbstractDungeon.player.discardPile.isEmpty()) {
                    this.addToTop(new PlayTopCardButSkipSpecialCardAction(this.target, this.exhaustCards, this.skipCard));
                    this.addToTop(new EmptyDeckShuffleAction());
                }
                // 如果弃牌堆也空了 → 整个牌库都只有 skipCard，直接结束
                this.isDone = true;
                return;
            }

            // 4. 从 drawPile 移除这张要打出的牌
            AbstractDungeon.player.drawPile.group.remove(cardToPlay);
            AbstractDungeon.getCurrRoom().souls.remove(cardToPlay);

            // 5. 设置是否 exhaust
            cardToPlay.exhaustOnUseOnce = this.exhaustCards;

            // 6. 放入 limbo
            AbstractDungeon.player.limbo.group.add(cardToPlay);
            cardToPlay.current_y = -200.0F * Settings.scale;
            cardToPlay.target_x = (float)Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
            cardToPlay.target_y = (float)Settings.HEIGHT / 2.0F;
            cardToPlay.targetAngle = 0.0F;
            cardToPlay.lighten(false);
            cardToPlay.drawScale = 0.12F;
            cardToPlay.targetDrawScale = 0.75F;
            cardToPlay.applyPowers();

            // 7. 打出卡牌
            this.addToTop(new NewQueueCardAction(cardToPlay, this.target, false, true));
            this.addToTop(new UnlimboAction(cardToPlay));

            // 8. 动画等待
            if (!Settings.FAST_MODE) {
                this.addToTop(new WaitAction(Settings.ACTION_DUR_MED));
            } else {
                this.addToTop(new WaitAction(Settings.ACTION_DUR_FASTER));
            }

            this.isDone = true;
        }
    }
}
