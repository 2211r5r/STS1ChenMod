package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.FocusPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FocusCard extends BaseCard {

    public static final String ID = makeID(FocusCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public FocusCard() {
        super(ID, info);

        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool3));
        this.addToBot(new ApplyPowerAction(p, p, new FocusPower(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FocusCard();
    }
}