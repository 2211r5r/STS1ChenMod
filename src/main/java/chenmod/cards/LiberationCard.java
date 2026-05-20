package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.powers.LiberationPower;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LiberationCard extends BaseCard{
    public static final String ID = makeID(LiberationCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.RARE, // 稀有度
            CardTarget.SELF, // 目标
            1 // 基础费用
    );

    public LiberationCard() {
        super(ID, info); // 调用父类构造方法

        this.cardsToPreview =  new JueYingCard(upgraded);

        tags.add(CustomTags.CHIXIAO);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));
        // 使用后，将一张赤霄绝影放入抽牌堆
        this.addToBot(new MakeTempCardInDrawPileAction(
                new JueYingCard(upgraded),  //
                1,
                true,
                true));

    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.NORMAL)
                    ));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            // 关键：升级预览卡牌
            if (this.cardsToPreview != null && !this.cardsToPreview.upgraded) {
                this.cardsToPreview.upgrade();
            }

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        LiberationCard copy = new LiberationCard();
        if (this.upgraded) {
            copy.upgrade();
        }
        return copy;
    }
}
