package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class RuptureCard extends BaseCard {

    public static final String ID = makeID(RuptureCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 2;

    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public RuptureCard() {
        super(ID, info);

        setMagic(MAGIC, UPG_MAGIC);

        this.exhaust = true;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        int recoverHpAmt = p.maxHealth - p.currentHealth;

        this.addToBot(new HealAction(p,p,recoverHpAmt));
        this.addToBot(new ApplyPowerAction(p,p,new StrengthPower(p,this.magicNumber), this.magicNumber));

        if(m == null || m.isDeadOrEscaped() || m.halfDead) {
            ChenMod.logger.info("怪物死了，没法给他回血了。");
        }else{
            this.addToBot(new HealAction(m,m,recoverHpAmt));
            this.addToBot(new ApplyPowerAction(m,m,new StrengthPower(m,1), 1));
        }

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
        return new RuptureCard();
    }
}