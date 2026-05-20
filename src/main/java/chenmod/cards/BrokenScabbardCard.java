package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.BrokenScabbardPower;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BrokenScabbardCard extends BaseCard{
    public static final String ID = makeID(BrokenScabbardCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.RARE, // 稀有度
            CardTarget.SELF, // 目标
            2 // 基础费用
    );

    private static final int BASE_MAGIC = 3;
    private static final int UPG_MAGIC = 2;

    public BrokenScabbardCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC, UPG_MAGIC);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));
        addToBot(new ApplyPowerAction(
                p, p,
                new BrokenScabbardPower(p, this.magicNumber),
                this.magicNumber
        ));
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeMagicNumber(UPG_MAGIC);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BrokenScabbardCard();
    }
}