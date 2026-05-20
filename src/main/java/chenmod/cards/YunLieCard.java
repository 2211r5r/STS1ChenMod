package chenmod.cards;

import chenmod.actions.AddHitTimesAction;
import chenmod.actions.DoubleSwordsAction;
import chenmod.actions.YunLieAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YunLieCard extends BaseCard {

    public static final String ID = makeID(YunLieCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final int COST = 2;

    private static final int DAMAGE = 9;
    private static final int UPG_DAMAGE = 2;
    private static final int MAGIC = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public YunLieCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
        }

        setMagic(MAGIC);
        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.MULTIPLE_ATTACKS);

        this.selfRetain = true; // 保留

    }

    @Override
    public void onRetained() {
        CardCrawlGame.sound.play(Sounds.attackVoice_9);
        this.addToBot(new AddHitTimesAction(this, 1));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new YunLieAction(this, this.magicNumber));
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
            upgradeDamage(UPG_DAMAGE);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new YunLieCard();
    }
}