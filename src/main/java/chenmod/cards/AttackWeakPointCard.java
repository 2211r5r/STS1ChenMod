package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class AttackWeakPointCard extends BaseCard {

    public static final String ID = makeID(AttackWeakPointCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 3;
    private static final int UPG_DAMAGE = 1;

    private static final int MAGIC = 2;

    private static final int BASE_BUFF_COUNTER = 1;
    private static final int UPG_BUFF_COUNTER = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public AttackWeakPointCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
        }

        setMagic(MAGIC);

        setCustomVar("BuffCount", BASE_BUFF_COUNTER,UPG_BUFF_COUNTER);

        tags.add(CardTags.STARTER_STRIKE);
        tags.add(CardTags.STRIKE);
        tags.add(CustomTags.MULTIPLE_ATTACKS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.attackCardEffect);
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));

        if (p instanceof ChenCharacter) {
            // 第二步：安全强转（100%不会报错）
            ChenCharacter player = (ChenCharacter) p;
            player.useAttackAnimation();
        }

        for(int i = 0; i < this.magicNumber; ++i){
            addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL),
                    i % 2 ==0 ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL: AbstractGameAction.AttackEffect.SLASH_VERTICAL // 横批
            ));
        }

        int buffNumber = customVar("BuffCount");

        if (m != null && m.getIntentBaseDmg() >= 0) {
            this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(m, buffNumber,false), buffNumber));
        }else{
            this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new VulnerablePower(m, buffNumber, false), buffNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPG_DAMAGE);
            upgradeCustomVar("BuffCount", UPG_BUFF_COUNTER);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new AttackWeakPointCard();
    }
}