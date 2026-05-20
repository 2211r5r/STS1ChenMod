package chenmod.cards;

import chenmod.actions.MakeTempUpgradedCardInHandAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CommandCard extends BaseCard {

    public static final String ID = makeID(CommandCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 0;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public CommandCard() {
        super(ID, info);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int count = AbstractDungeon.player.hand.size();

        for (int i = 0; i < count; ++i) {
            if (Settings.FAST_MODE) {
                this.addToTop(new ExhaustAction(1, true, true, false, Settings.ACTION_DUR_XFAST));
            }
            else {
                this.addToTop(new ExhaustAction(1, true, true));
            }
        }

        for (int j = 0; j < count; ++j) {
            if(upgraded){
                this.addToBot(new MakeTempUpgradedCardInHandAction(AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy(), false));
            }else{
                this.addToBot(new MakeTempCardInHandAction(AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy(), false));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            this.upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CommandCard();
    }
}