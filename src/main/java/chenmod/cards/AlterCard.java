package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AlterCard extends BaseCard {

    public static final String ID = makeID(AlterCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int BLOCK = 6;
    private static final int UPG_BLOCK = 2;
    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public AlterCard() {
        super(ID, info);

        setBlock(BLOCK, UPG_BLOCK);
        setMagic(MAGIC, UPG_MAGIC);

        tags.add(CustomTags.DEFEND);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, block));
        this.addToBot(new DrawCardAction(magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            upgradeBlock(UPG_BLOCK);
            upgradeMagicNumber(UPG_MAGIC);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new AlterCard();
    }
}