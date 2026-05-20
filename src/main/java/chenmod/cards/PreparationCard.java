package chenmod.cards;

import chenmod.actions.PreparationAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PreparationCard extends BaseCard {

    public static final String ID = makeID(PreparationCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public PreparationCard() {
        super(ID, info);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));
        this.addToBot(new PreparationAction(this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeMagicNumber(UPG_MAGIC);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PreparationCard();
    }
}