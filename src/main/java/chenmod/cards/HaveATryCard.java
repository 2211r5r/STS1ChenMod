package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.PowerCostUpPower;
import chenmod.powers.SkillCostUpPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HaveATryCard extends BaseCard {

    public static final String ID = makeID(HaveATryCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public HaveATryCard() {
        super(ID, info);

        this.magicNumber = 0;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        int attackCount = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                attackCount++;
            }
        }

        this.baseMagicNumber = attackCount;
        this.magicNumber = baseMagicNumber;

        this.rawDescription = upgraded ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;
        if(this.magicNumber>0){
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        this.initializeDescription();

    }

    @Override
    public void onMoveToDiscard() {
        this.magicNumber = 0;
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        this.addToBot(new GainEnergyAction(this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new SkillCostUpPower(p)));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeBaseCost(1);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HaveATryCard();
    }
}