package chenmod.actions;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class LegacyAction extends AbstractGameAction {
    private static final UIStrings uiStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getUIString(ChenMod.makeID("LegacyAction"));
    public static final String[] TEXT = uiStrings.TEXT;

    private boolean cardSelected = false;

    public LegacyAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;

        // 第一次调用：打开选卡界面
        if (!cardSelected) {
            ArrayList<AbstractCard> selectable = new ArrayList<>();
            for (AbstractCard c : p.masterDeck.group) {
                if (c.rarity == AbstractCard.CardRarity.BASIC
                        || c.rarity == AbstractCard.CardRarity.COMMON
                        || c.rarity == AbstractCard.CardRarity.UNCOMMON) {
                    selectable.add(c);
                }
            }

            if (selectable.isEmpty()) {
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
                this.isDone = true;
                return;
            }

            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : selectable) {
                tmp.addToTop(c);
            }

            AbstractDungeon.gridSelectScreen.open(
                    tmp,
                    1,
                    TEXT[0], // 提示文本
                    false, false, false, true
            );

            cardSelected = true;
            tickDuration();
            return;
        }

        // 第二次调用：处理玩家选择
        if (cardSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

            // 删除原牌
            p.masterDeck.removeCard(chosen);

            // 确定目标稀有度
            AbstractCard.CardRarity newRarity;
            if (chosen.rarity == AbstractCard.CardRarity.UNCOMMON) {
                newRarity = AbstractCard.CardRarity.RARE;
            } else {
                newRarity = AbstractCard.CardRarity.UNCOMMON;
            }

            // 随机生成新牌
            AbstractCard newCard = AbstractDungeon.getCard(newRarity).makeCopy();
            if(chosen.upgraded){
                newCard.upgrade();
            }

            // 添加到牌组并播放获得特效
            AbstractDungeon.effectList.add(
                    new ShowCardAndObtainEffect(newCard, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f)
            );

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            this.isDone = true;
        }

        tickDuration();
    }
}
