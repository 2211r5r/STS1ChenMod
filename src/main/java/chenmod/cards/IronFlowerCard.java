package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.IronFlowerPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class IronFlowerCard extends BaseCard {

    public static final String ID = makeID(IronFlowerCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int MAGIC = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public IronFlowerCard() {
        super(ID, info);

        setMagic(MAGIC);

        this.isInnate = false;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool3));
        this.addToBot(new ApplyPowerAction(p, p, new IronFlowerPower(p, this.magicNumber), this.magicNumber));
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
        return new IronFlowerCard();
    }
}