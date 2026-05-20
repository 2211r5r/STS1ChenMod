package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.NewBeginningPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class NewBeginningCard extends BaseCard {

    public static final String ID = makeID(NewBeginningCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public NewBeginningCard() {
        super(ID, info);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new NewBeginningPower(p)));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new NewBeginningCard();
    }
}