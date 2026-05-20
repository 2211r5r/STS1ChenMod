package chenmod.cards;

import chenmod.actions.SeeYouTomorrowAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SeeYouTomorrowCard extends BaseCard {

    public static final String ID = makeID(SeeYouTomorrowCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public SeeYouTomorrowCard() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SeeYouTomorrowAction(p, p, 1, this.upgraded));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SeeYouTomorrowCard();
    }
}