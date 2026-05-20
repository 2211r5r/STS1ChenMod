package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.GlazeWallPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;

public class DengLinYiCard extends BaseCard {

    public static final String ID = makeID(DengLinYiCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 3;

    private static final int BLOCK = 9;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public DengLinYiCard() {
        super(ID, info);

        setBlock(BLOCK);

        this.exhaust = true;
//        this.isEthereal = true;
        tags.add(CustomTags.DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, block));

        this.addToBot(new ApplyPowerAction(p,p,new GlazeWallPower(p)));

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
        return new DengLinYiCard();
    }
}