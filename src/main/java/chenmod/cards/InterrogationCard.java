package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.InterrogationPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class InterrogationCard extends BaseCard {

    public static final String ID = makeID(InterrogationCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 2;

    private static final int MAGIC = 6;
    private static final int UPG_MAGIC = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public InterrogationCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setMagic(99, 1);
        } else {
            setMagic(MAGIC, UPG_MAGIC);
        }
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if(!m.hasPower(ArtifactPower.POWER_ID)){
            this.addToBot(new ApplyPowerAction(m, m,new InterrogationPower(m,this.magicNumber),this.magicNumber));
        }
        this.addToBot(new ApplyPowerAction(m, m, new StrengthPower(m, -this.magicNumber), -this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
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
        return new InterrogationCard();
    }
}