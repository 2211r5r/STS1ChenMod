package chenmod.cards;

import chenmod.actions.TranquilityAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TranquilityCard extends BaseCard {

    public static final String ID = makeID(TranquilityCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 2;

    private static final int DAMAGE = 12;
    private static final int UPG_DAMAGE = 4;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public TranquilityCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
        }

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new TranquilityAction(m, new DamageInfo(m, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeDamage(UPG_DAMAGE);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TranquilityCard();
    }
}