package chenmod.cards;

import chenmod.actions.ShockAllEnemiesAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.DistanceCache;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShockCard extends BaseCard{
    public static final String ID = makeID(ShockCard.class.getSimpleName());

    private static final int BASE_DAMAGE = 6;

    private static final int UPG_DAMAGE = 4;

    private static final float TIMES_LIMIT = 1.50f;
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.COMMON, // 稀有度
            CardTarget.ALL_ENEMY, // 目标
            1 // 基础费用
    );

    public ShockCard() {
        super(ID, info); // 调用父类构造方法

        setDamage(BASE_DAMAGE, UPG_DAMAGE);
        setMagic(0);

    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        // 关键：生成与 Action 一致的全体伤害矩阵
        int[] matrix = DamageInfo.createDamageMatrix(this.damage, true);

        int maxDamage = 0;

        for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
            AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

            if (!m.isDeadOrEscaped()) {
                float times = upgraded
                        ? DistanceCache.getTimesFromMax(m)
                        : Math.min(TIMES_LIMIT, DistanceCache.getTimesFromMax(m));

                int dmg = (int)Math.ceil(matrix[i] * times);
                if (dmg > maxDamage) maxDamage = dmg;
            }
        }
        this.rawDescription =cardStrings.UPGRADE_DESCRIPTION;
        this.rawDescription += " NL (最高伤害为"+maxDamage+"点)";
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);

        // 关键：生成与 Action 一致的全体伤害矩阵
        int[] matrix = DamageInfo.createDamageMatrix(this.damage, true);

        int maxDamage = 0;

        for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
            AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

            if (!m.isDeadOrEscaped()) {
                float times = upgraded
                        ? DistanceCache.getTimesFromMax(m)
                        : Math.min(TIMES_LIMIT, DistanceCache.getTimesFromMax(m));

                int dmg = (int)Math.ceil(matrix[i] * times);
                if (dmg > maxDamage) maxDamage = dmg;
            }
        }
        this.rawDescription =cardStrings.UPGRADE_DESCRIPTION;
        this.rawDescription += " NL (最高伤害为"+maxDamage+"点)";
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
    public void triggerOnExhaust() {
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool2));
        CardCrawlGame.sound.play(Sounds.shockAttackEffect);
        this.addToBot(new ShockAllEnemiesAction(
                p,
                this.damage,                 // 当前卡牌的伤害（会被 Action 自动复制成矩阵）
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                this.upgraded                // 是否升级（决定倍率是否封顶）
        ));
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeDamage(UPG_DAMAGE);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ShockCard();
    }
}
