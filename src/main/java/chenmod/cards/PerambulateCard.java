package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.actions.ExhaustRandomAttackCardAndBuffAction;
import chenmod.actions.JueYingAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PerambulateCard extends BaseCard {

    public static final String ID = makeID(PerambulateCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 2;

    private static final int DAMAGE = 8;

    private static final int MAGIC = 2;

    public static final List<String> attackVoicePool = new ArrayList<>();

    static {
        attackVoicePool.add(Sounds.attackVoice_3);
        attackVoicePool.add(Sounds.attackVoice_4);
    }

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public PerambulateCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE);
        }

        setMagic(MAGIC);

        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.MULTIPLE_ATTACKS);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        if (p == null || m == null || m.isDeadOrEscaped()) {
            return;
        }

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(attackVoicePool));

        this.addToBot(new JueYingAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                this.magicNumber,
                false,
                this.upgraded)
        );

        this.addToBot(new ExhaustRandomAttackCardAndBuffAction(this));

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
            upgradeName(); // 升级卡牌名称（自动添加+号）

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PerambulateCard();
    }
}