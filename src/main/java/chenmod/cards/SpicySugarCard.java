package chenmod.cards;

import chenmod.ChenMod;
import chenmod.actions.ZeroCostAllCardsThisTurnAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.ExpertiseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class SpicySugarCard extends BaseCard {

    public static final String ID = makeID(SpicySugarCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int MAGIC = 1;

    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, TYPE, RARITY, TARGET, COST
    );

    public SpicySugarCard() {
        super(ID, info);

        setMagic(MAGIC);

        this.setDisplayRarity(CardRarity.RARE);

        this.isInnate = true;   // 固有
        this.exhaust = true;
        this.selfRetain = true; // 保留
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        if(!upgraded){
            this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, this.magicNumber, false), this.magicNumber));
        }

        this.addToBot(new ExpertiseAction(p, 10));

        this.addToBot(new ZeroCostAllCardsThisTurnAction());

        // 删牌
        CardCrawlGame.metricData.addPurgedItem(this.getMetricID());
        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(this, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));

        AbstractCard target = null;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(this.uuid)) {
                target = c;
                break;
            }
        }

        if (target != null) {
            AbstractDungeon.player.masterDeck.removeCard(target);
        }

    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SpicySugarCard();
    }
}