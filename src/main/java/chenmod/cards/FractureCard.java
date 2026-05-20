package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.FracturePower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FractureCard extends BaseCard {

    public static final String ID = makeID(FractureCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 3;

    private static final int MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public FractureCard() {
        super(ID, info);
        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new FracturePower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FractureCard();
    }
}