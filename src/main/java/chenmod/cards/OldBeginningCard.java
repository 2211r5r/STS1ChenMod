package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class OldBeginningCard extends BaseCard {

    public static final String ID = makeID(OldBeginningCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 0;

    private static final int BLOCK = 3;
    private static final int UPG_BLOCK = 3;

    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public OldBeginningCard() {
        super(ID, info);

        setMagic(MAGIC, UPG_MAGIC);

        tags.add(CustomTags.DEFEND);

    }

    @Override
    public void applyPowers() {
        this.baseBlock = BLOCK + AbstractDungeon.player.hand.size();
        if (this.upgraded) {
            this.baseBlock += UPG_BLOCK;
        }
        super.applyPowers();
        if (!this.upgraded) {
            this.rawDescription = cardStrings.DESCRIPTION;
        }
        else {
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
        }
        this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = upgraded ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, block));
        this.addToBot(new PressEndTurnButtonAction());
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            upgradeMagicNumber(UPG_MAGIC);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new OldBeginningCard();
    }
}