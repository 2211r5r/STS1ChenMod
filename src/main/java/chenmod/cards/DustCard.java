package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.DustPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Objects;

public class DustCard extends BaseCard {

    public static final String ID = makeID(DustCard.class.getSimpleName());

    private static final CardType TYPE = CardType.STATUS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.NONE;
    private static final int COST = 0;

    private static final int MAGIC = 2;

    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, TYPE, RARITY, TARGET, COST
    );

    public DustCard() {
        super(ID, info);
        setMagic(MAGIC);
        this.exhaust = false; // 默认不消耗
    }

    @Override
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, this.magicNumber, AbstractGameAction.AttackEffect.NONE));
        }else{
            this.exhaust = true;
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    @Override
    public void triggerOnExhaust() {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DustPower(AbstractDungeon.player, 1), 1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgraded = true;
        }
    }

    @Override
    public boolean canUpgrade(){
        return false;
    }

    @Override
    public AbstractCard makeCopy() {
        return new DustCard();
    }
}