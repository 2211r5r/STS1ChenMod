package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.NextTurnEnergyPower;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PaperWorkCard extends BaseCard{
    public static final String ID = makeID(PaperWorkCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            1 // 基础费用
    );

    public PaperWorkCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(3,1);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DrawCardAction(p, this.magicNumber));
    }

    @Override
    public void triggerOnExhaust() {
        AbstractPlayer p = AbstractDungeon.player;
        if(this.upgraded){
            this.addToBot(new ApplyPowerAction(p, p, new NextTurnEnergyPower(p, 3), 3));
        }else{
            this.addToBot(new ApplyPowerAction(p, p, new NextTurnEnergyPower(p, 2), 2));
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeMagicNumber(1);
            upgraded = true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PaperWorkCard();
    }
}
