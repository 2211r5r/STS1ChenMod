package chenmod.cards;

import basemod.abstracts.CustomCard;
import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class StorageCard extends BaseCard {

    public static final String ID = makeID(StorageCard.class.getSimpleName());

    private static final CardType TYPE = CardType.STATUS;
    private static final CardRarity RARITY = CardRarity.SPECIAL;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 1;

    private static final int MAGIC = 1;

    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, TYPE, RARITY, TARGET, COST
    );

    public StorageCard() {
        super(ID, info);

        setMagic(MAGIC);

        this.exhaust = true;

        tags.add(CustomTags.CHIXIAO);

    }

    public StorageCard(boolean isUpgraded) {
        this();

        if(isUpgraded){
            this.upgrade();
        }

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DrawCardAction(p,1));
    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.NORMAL)
                    ));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();

            upgradeBaseCost(0);

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new StorageCard();
    }
}