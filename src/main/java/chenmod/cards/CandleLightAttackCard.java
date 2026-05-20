package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CandleLightAttackCard extends BaseCard {

    public static final String ID = makeID(CandleLightAttackCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 3;

    private static final int MAGIC = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public CandleLightAttackCard() {
        super(ID, info);

        setDamage(DAMAGE);
        setMagic(MAGIC);

        tags.add(CustomTags.MULTIPLE_ATTACKS);
        tags.add(CustomTags.CHIXIAO);
        tags.add(CardTags.STRIKE);

        this.exhaust = true;
    }

    public CandleLightAttackCard(boolean isUpgraded) {
        this();
        if(isUpgraded){
            this.upgrade();
        }
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.attackCardEffect);
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool3));

        if (p instanceof ChenCharacter) {
            // 第二步：安全强转（100%不会报错）
            ChenCharacter player = (ChenCharacter) p;
            player.useAttackAnimation();
        }

        for(int i = 0; i < this.magicNumber; ++i){
            addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL),
                    i % 2 ==0 ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL: AbstractGameAction.AttackEffect.SLASH_VERTICAL // 横批
            ));
        }

        this.addToBot(new GainEnergyAction(1));
    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.NORMAL)
                    ));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            this.exhaust = false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CandleLightAttackCard();
    }
}