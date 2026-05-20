package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;

public class RushingCard extends BaseCard {

    public static final String ID = makeID(RushingCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 0;

    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public RushingCard() {
        super(ID, info);

        setMagic(MAGIC, UPG_MAGIC);
        tags.add(CustomTags.DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber),this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new LoseDexterityPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPG_MAGIC);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new RushingCard();
    }
}