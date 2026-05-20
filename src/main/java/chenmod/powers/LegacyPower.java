package chenmod.powers;

import chenmod.ChenMod;
import chenmod.actions.LegacyAction;
import chenmod.effects.DelayedShowCardAndObtainEffect;
import chenmod.rewards.LegacyReward;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class LegacyPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(LegacyPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    private AbstractCard chosenCard = null;

    public LegacyPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
        updateDescription();
    }

    @Override
    public void updateDescription() {

        if(chosenCard == null) {
            this.description = DESCRIPTIONS[0];
            return;
        }

        String exText = chosenCard.rarity == AbstractCard.CardRarity.UNCOMMON ? DESCRIPTIONS[3] : DESCRIPTIONS[4];

        this.description = String.format(DESCRIPTIONS[1] + chosenCard.name + DESCRIPTIONS[2] + exText);
    }

    @Override
    public void onInitialApplication(){
        // 获得 Power 时立即打开选卡界面
        ArrayList<AbstractCard> selectable = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.BASIC
                    || c.rarity == AbstractCard.CardRarity.COMMON
                    || c.rarity == AbstractCard.CardRarity.UNCOMMON) {
                selectable.add(c);
            }
        }

        if (!selectable.isEmpty()) {
            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : selectable) {
                tmp.addToTop(c);
            }
            AbstractDungeon.gridSelectScreen.open(tmp, 1, powerStrings.DESCRIPTIONS[0], false, true, false, false);
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if(chosenCard == null) {
            this.onInitialApplication();
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        // 保存玩家选择的牌
        if (chosenCard == null && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            chosenCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            updateDescription();
        }
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onVictory() {
        if (chosenCard != null) {
            // 删除原牌
            AbstractDungeon.player.masterDeck.removeCard(chosenCard);

            // 确定目标稀有度
            AbstractCard.CardRarity newRarity =
                    (chosenCard.rarity == AbstractCard.CardRarity.UNCOMMON)
                            ? AbstractCard.CardRarity.RARE
                            : AbstractCard.CardRarity.UNCOMMON;


            // 随机生成新牌
            AbstractCard newCard = AbstractDungeon.getCard(newRarity).makeCopy();
            if(chosenCard.upgraded){
                newCard.upgrade();
            }

            // 添加到牌组并播放获得特效
            AbstractDungeon.topLevelEffects.add(
                    new DelayedShowCardAndObtainEffect(newCard, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, 1.5F)
            );

            chosenCard = null; // 清空引用，避免重复执行
            updateDescription();
        }
    }

}