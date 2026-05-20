package chenmod.cards;

import chenmod.actions.DamageAttackIntentEnemiesAction;
import chenmod.actions.NoFastWaitAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class PatrolCard extends BaseCard {

    public static final String ID = makeID(PatrolCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ALL_ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 3;
    private static final int BLOCK = 5;
    private static final int UPG_BLOCK = 3;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public PatrolCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
            setBlock(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
            setBlock(BLOCK, UPG_BLOCK);
        }

        this.isMultiDamage = true;

        tags.add(CustomTags.DEFEND);

    }

    @Override
    public void triggerOnGlowCheck() {
        boolean hasAttackingMonster = false;

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped() && mo.getIntentBaseDmg() >= 0) {
                hasAttackingMonster = true;
                break;
            }
        }

        if (hasAttackingMonster) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        this.addToBot(new GainBlockAction(p, p, this.block));

        this.addToBot(new SFXAction("ATTACK_HEAVY"));
        this.addToBot(new DamageAttackIntentEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.BLUNT_LIGHT));

    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPG_DAMAGE);
            upgradeBlock(UPG_BLOCK);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PatrolCard();
    }
}