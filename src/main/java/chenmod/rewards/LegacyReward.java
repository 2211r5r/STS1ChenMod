package chenmod.rewards;

import basemod.abstracts.CustomReward;
import chenmod.ChenMod;
import chenmod.patches.CustomRewardsPatch;
import chenmod.powers.LegacyPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class LegacyReward extends CustomReward {
    private static final Texture ICON = new Texture(Gdx.files.internal("chenmod/images/powers/large/LegacyPower.png"));
    private static final RewardType TYPE = CustomRewardsPatch.LEGACY_REWARD;

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ChenMod.makeID(LegacyPower.class.getSimpleName()));
    private static final String TEXT = powerStrings.DESCRIPTIONS[1];

    private boolean waitingSelection = false;

    public LegacyReward() {
        super(ICON, TEXT, TYPE);
    }

    @Override
    public boolean claimReward() {

        ArrayList<AbstractCard> selectable = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.BASIC
                    || c.rarity == AbstractCard.CardRarity.COMMON
                    || c.rarity == AbstractCard.CardRarity.UNCOMMON) {
                selectable.add(c);
            }
        }

        if (selectable.isEmpty()) {
            return true; // 没有可选牌，奖励直接结束并移除
        }

        CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : selectable) {
            tmp.addToTop(c);
        }

        // 隐藏奖励界面 UI，避免遮挡
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.previousScreen = AbstractDungeon.screen;

        AbstractDungeon.gridSelectScreen.open(
                tmp,
                1,
                TEXT,
                false, false, false, true
        );

        // 如果已经在等待选卡，检查是否完成
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.player.masterDeck.removeCard(chosen);

            AbstractCard.CardRarity newRarity =
                    (chosen.rarity == AbstractCard.CardRarity.UNCOMMON)
                            ? AbstractCard.CardRarity.RARE
                            : AbstractCard.CardRarity.UNCOMMON;

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
            waitingSelection = false;

            // 奖励完成后恢复 UI
            AbstractDungeon.dynamicBanner.appear();
            AbstractDungeon.overlayMenu.cancelButton.show("Cancel");

            return true; // 奖励完成，移除
        }


        return false; // 还在等待玩家选择
    }
}
