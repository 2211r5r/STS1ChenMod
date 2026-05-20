package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QuittingTimeCard extends BaseCard {

    public static final String ID = makeID(QuittingTimeCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 0;

    private static final int MAGIC = 11;
    private static final int UPG_MAGIC = 4;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public QuittingTimeCard() {
        super(ID, info);
        setMagic(MAGIC, UPG_MAGIC);
        setExhaust(true);
        tags.add(CardTags.HEALING);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if(AbstractDungeon.player.gold >= 5){
            return super.canUse(p, m);
        }else {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play("GOLD_JINGLE");
        int loseGoldValue = Math.min(5, AbstractDungeon.player.gold);
        AbstractDungeon.player.loseGold(loseGoldValue);
        this.addToBot(new HealAction(p, p, this.magicNumber));
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
        return new QuittingTimeCard();
    }
}