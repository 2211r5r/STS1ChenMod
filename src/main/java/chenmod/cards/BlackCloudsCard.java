package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.BlackCloudsPower;
import chenmod.powers.LieZhenPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BlackCloudsCard extends BaseCard {

    public static final String ID = makeID(BlackCloudsCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 2;

    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public BlackCloudsCard() {
        super(ID, info);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));
        this.addToBot(new ApplyPowerAction(
                p, p,
                new BlackCloudsPower(p, this.magicNumber),this.magicNumber
        ));
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
        return new BlackCloudsCard();
    }
}