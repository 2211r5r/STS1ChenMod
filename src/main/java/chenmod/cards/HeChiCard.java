package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HeChiCard extends BaseCard{
    public static final String ID = makeID(HeChiCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            AbstractCard.CardType.SKILL, // 卡牌类型（技能）
            AbstractCard.CardRarity.UNCOMMON, // 稀有度（普通）
            AbstractCard.CardTarget.SELF, // 目标自己
            0 // 基础费用
    );

    // 攻击伤害
    private static final int BASE_DRAW_CARD = 1;
    // 升级后伤害
    private static final int UPG_DRAW_CARD = 1;


    public HeChiCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_DRAW_CARD, UPG_DRAW_CARD);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        this.addToBot(new GainEnergyAction(1));
        this.addToBot(new DrawCardAction(p, this.magicNumber));

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeMagicNumber(UPG_DRAW_CARD);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeChiCard();
    }
}
