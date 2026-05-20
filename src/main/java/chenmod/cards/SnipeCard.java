package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.DistanceCache;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SnipeCard extends BaseCard{
    public static final String ID = makeID(SnipeCard.class.getSimpleName());

    private static  final int MAGIC = 150;

    private static  final int BASE_DAMAGE = 15;

    private static  final int UPG_DAMAGE = 10;
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.ENEMY, // 目标
            1 // 基础费用
    );

    public SnipeCard() {
        super(ID, info); // 调用父类构造方法

        setDamage(BASE_DAMAGE,UPG_DAMAGE);

        this.rawDescription = cardStrings.DESCRIPTION;

        this.exhaust = true;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {

        int realBase = this.baseDamage;

        float times = DistanceCache.getTimesFromMin(mo);

        this.baseDamage = (int)Math.ceil(realBase * times);

        super.calculateCardDamage(mo);

        this.baseDamage = realBase;

        this.isDamageModified = true;   // 我修改伤害了，记得更新

        // 更新描述（必须）
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        this.addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT
        ));
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）

            upgradeDamage(UPG_DAMAGE);

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SnipeCard();
    }
}
