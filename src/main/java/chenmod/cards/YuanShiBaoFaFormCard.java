package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.CandleLightFormPower;
import chenmod.powers.YuanShiBaoFaPower;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YuanShiBaoFaFormCard extends BaseCard{
    public static final String ID = makeID(YuanShiBaoFaFormCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.RARE, // 稀有度
            CardTarget.SELF, // 目标
            2 // 基础费用
    );

    private static final int BASE_MAGIC = 2;

    public YuanShiBaoFaFormCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC);

        this.isEthereal = true;

        this.cardsToPreview =  new YuanShiBaoFaCard(upgraded);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));

        this.addToBot(new ApplyPowerAction(p, p,
                new YuanShiBaoFaPower(p, this.magicNumber, this.upgraded), this.magicNumber));

        YuanShiBaoFaPower power = p.hasPower(YuanShiBaoFaPower.POWER_ID)
                ? (YuanShiBaoFaPower) p.getPower(YuanShiBaoFaPower.POWER_ID) : null;

        if (power != null && this.upgraded) {
            power.isUpgraded = true;
            power.updateDescription();
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            this.isEthereal = false; //将[虚无] 属性取消掉
            upgradeName();

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;

            // 关键：升级预览卡牌
            if (this.cardsToPreview != null && !this.cardsToPreview.upgraded) {
                this.cardsToPreview.upgrade();
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        YuanShiBaoFaFormCard copy = new YuanShiBaoFaFormCard();
        if (this.upgraded) {
            copy.upgrade();
        }
        return copy;
    }
}
