package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.CandleLightFormPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CandleLightFormCard extends BaseCard {

    public static final String ID = makeID(CandleLightFormCard.class.getSimpleName());

    private static final CardType TYPE = CardType.POWER;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 3;

    private static final int MAGIC = 1;
//    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public CandleLightFormCard() {
        super(ID, info);
        setMagic(MAGIC);
        this.cardsToPreview =  new CandleLightAttackCard(false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool3));

        this.addToBot(new ApplyPowerAction(p, p,
                new CandleLightFormPower(p, this.magicNumber, false), this.magicNumber));

//        CandleLightFormPower power = p.hasPower(CandleLightFormPower.POWER_ID)
//                ? (CandleLightFormPower) p.getPower(CandleLightFormPower.POWER_ID) : null;
//
//        if (power != null && this.upgraded) {
//            power.isUpgraded = true;
//            power.updateDescription();
//        }

        if(this.upgraded){
            this.addToBot(new GainEnergyAction(1));
        }

    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CandleLightFormCard();
    }
}