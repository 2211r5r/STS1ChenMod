package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DefendCard extends BaseCard{
    public static final String ID = makeID(DefendCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            AbstractCard.CardType.SKILL, // 卡牌类型（技能）
            AbstractCard.CardRarity.BASIC, // 稀有度（初始牌）
            AbstractCard.CardTarget.SELF, // 目标自己
            1 // 基础费用
    );

    // 攻击伤害
    private static final int BLOCK = 5;
    // 升级后伤害
    private static final int UPG_BLOCK = 3;

    public DefendCard() {
        super(ID, info); // 调用父类构造方法

        if(ChenModConfig.DEBUG_MODE){
            setBlock(99, 1);
        }else{
            setBlock(BLOCK, UPG_BLOCK);
        }

        tags.add(CardTags.STARTER_DEFEND);
        tags.add(CustomTags.DEFEND);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeBlock(UPG_BLOCK);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DefendCard();
    }
}
