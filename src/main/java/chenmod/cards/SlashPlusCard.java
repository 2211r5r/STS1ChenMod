package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.BreakBlockPower_player;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class SlashPlusCard extends BaseCard {

    public static final String ID = makeID(SlashPlusCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 7;
    private static final int UPG_DAMAGE = 3;

    private static final int MAGIC = 75;
    private static final int UPG_MAGIC = 25;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public SlashPlusCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
        }

        setMagic(MAGIC, UPG_MAGIC);

        this.isMultiDamage = true;

        tags.add(CustomTags.CHIXIAO);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        this.addToBot(new ApplyPowerAction(p,p,new BreakBlockPower_player(p, this.magicNumber), this.magicNumber));

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));
        CardCrawlGame.sound.play(Sounds.slashEffect);

        if (p instanceof ChenCharacter) {
            ChenCharacter player = (ChenCharacter) p;
            player.useSkill2AttackAnimation();
        }

        this.addToBot(new SFXAction("ATTACK_HEAVY"));
        this.addToBot(new VFXAction(p, new CleaveEffect(), 0.1f));
        this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));

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
            upgradeDamage(UPG_DAMAGE); // 应用伤害升级
            upgradeMagicNumber(UPG_MAGIC);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SlashPlusCard();
    }
}