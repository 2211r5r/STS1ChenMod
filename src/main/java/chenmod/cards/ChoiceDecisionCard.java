package chenmod.cards;

import chenmod.actions.ChoiceDecisionAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChoiceDecisionCard extends BaseCard {

    public static final String ID = makeID(ChoiceDecisionCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public ChoiceDecisionCard() {
        super(ID, info);

        this.exhaust = true;

        this.isEthereal = true; // 虚无
        this.selfRetain = false; // 保留（回合结束不弃牌）

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ChoiceDecisionAction());
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            this.isEthereal = false; // 虚无
            this.selfRetain = true; // 保留（回合结束不弃牌）

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ChoiceDecisionCard();
    }
}