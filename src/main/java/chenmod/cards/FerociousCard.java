package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.FerociousPower;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FerociousCard extends BaseCard {
    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(FerociousCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR,
            CardType.POWER, // 卡牌类型（攻击）
            CardRarity.UNCOMMON, // 稀有度（初始牌）
            CardTarget.SELF, // 目标（单个敌人）
            1 // 基础费用
    );

    // 攻击伤害
    private static final int BASE_MAGIC = 1;
    // 升级后伤害
    private static final int UPG_MAGIC = 1;

    public FerociousCard() {
        super(ID, info); // 调用父类构造方法
        setMagic(BASE_MAGIC, UPG_MAGIC);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p,p,new FerociousPower(p,this.magicNumber), this.magicNumber));
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPG_MAGIC);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FerociousCard();
    }
}
