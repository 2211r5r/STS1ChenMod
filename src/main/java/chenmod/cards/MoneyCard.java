package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MoneyCard extends BaseCard{
    public static final String ID = makeID(MoneyCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.ENEMY, // 目标
            1 // 基础费用
    );

    public MoneyCard() {
        super(ID, info); // 调用父类构造方法

        this.exhaust = true;

        this.baseDamage = 0;
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.baseDamage = (int) Math.floor(AbstractDungeon.player.gold / 10.0f);
        this.calculateCardDamage(m);
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        this.rawDescription = this.cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        this.baseDamage = (int) Math.floor(AbstractDungeon.player.gold / 10.0f);
        super.applyPowers();
        this.rawDescription = this.cardStrings.DESCRIPTION;
        this.rawDescription += this.cardStrings.UPGRADE_DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(final AbstractMonster mo) {
        super.calculateCardDamage(mo);
        this.rawDescription = this.cardStrings.DESCRIPTION;
        this.rawDescription += this.cardStrings.UPGRADE_DESCRIPTION;
        this.initializeDescription();
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeBaseCost(0);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new MoneyCard();
    }
}