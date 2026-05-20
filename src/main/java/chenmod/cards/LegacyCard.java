package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.LegacyPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LegacyCard extends BaseCard {

    public static final String ID = makeID(LegacyCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public LegacyCard() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if(!p.hasPower(LegacyPower.POWER_ID)){
            this.addToBot(new ApplyPowerAction(p, p, new LegacyPower(p)));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeBaseCost(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LegacyCard();
    }
}