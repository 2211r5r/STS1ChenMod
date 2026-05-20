package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.DistanceCache;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TouristCard extends BaseCard {

    public static final String ID = makeID(TouristCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 1;

    private static  final int BASE_DAMAGE = 8;

    private static  final int UPG_DAMAGE = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public TouristCard() {
        super(ID, info);

        setDamage(BASE_DAMAGE,UPG_DAMAGE);

        this.rawDescription = cardStrings.DESCRIPTION;

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

        float times = DistanceCache.getTimesFromMax(mo);

        this.baseDamage = (int)Math.ceil(realBase * times);

        super.calculateCardDamage(mo);

        this.baseDamage = realBase;

        this.isDamageModified = true;   // 我修改伤害了，记得更新

        // 更新描述（必须）
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool2));

        this.addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT
        ));

        if(!m.isDeadOrEscaped() && m.getIntentBaseDmg() >= 0){
            this.addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT
            ));
        }
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
        return new TouristCard();
    }
}