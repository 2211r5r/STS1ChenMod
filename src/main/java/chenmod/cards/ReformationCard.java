package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReformationCard extends BaseCard{

    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(ReformationCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色（铁clad）
            CardType.SKILL, // 卡牌类型（攻击）
            CardRarity.COMMON, // 稀有度（初始牌）
            CardTarget.SELF, // 目标（单个敌人）
            0 // 基础费用
    );
    public ReformationCard() {
        super(ID, info); // 调用父类构造方法

        this.exhaust=true;
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        int handCount = AbstractDungeon.player.hand.size() - 1;
        if(handCount > 0){
            this.addToTop(new PutOnDeckAction(AbstractDungeon.player, AbstractDungeon.player, 99, true));
            this.addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
            this.addToBot(new DrawCardAction(AbstractDungeon.player, handCount));
        }

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeBaseCost(0);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            this.exhaust=false;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ReformationCard();
    }
}
