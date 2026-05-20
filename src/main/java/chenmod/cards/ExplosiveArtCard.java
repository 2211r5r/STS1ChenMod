package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.ExplosiveArtPower;
import chenmod.powers.GlazeWallPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ExplosiveArtCard extends BaseCard {

    public static final String ID = makeID(ExplosiveArtCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    private static final int BASE_MAGIC = 50;

    public ExplosiveArtCard() {
        super(ID, info);
        this.isInnate = false;

        setMagic(BASE_MAGIC);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool2));
        this.addToBot(new ApplyPowerAction(p,p,new ExplosiveArtPower(p, this.magicNumber), this.magicNumber));

        this.addToBot(new ApplyPowerAction(p,p,new GlazeWallPower(p)));

    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            this.isInnate = true;

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ExplosiveArtCard();
    }
}