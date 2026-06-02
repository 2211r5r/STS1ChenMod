package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class SlashCard extends BaseCard {
    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(SlashCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            AbstractCard.CardType.ATTACK, // 卡牌类型（攻击）
            AbstractCard.CardRarity.COMMON, // 稀有度（普通）
            AbstractCard.CardTarget.ALL_ENEMY, // 目标（全部敌人）
            1 // 基础费用
    );

    // 攻击伤害
    private static final int DAMAGE = 7;
    // 升级后伤害
    private static final int UPG_DAMAGE = 3;

    public SlashCard() {
        super(ID, info); // 调用父类构造方法

        // 设置基础伤害，升级后增加2点伤害
        if(ChenModConfig.DEBUG_MODE){
            setDamage(99, 1);
        }else{
            setDamage(DAMAGE, UPG_DAMAGE);
        }
        this.isMultiDamage = true;

        //
        tags.add(CustomTags.CHIXIAO);
    }

    // 卡牌触发效果（核心逻辑）
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));
        CardCrawlGame.sound.play(Sounds.slashEffect);

        if (p instanceof ChenCharacter) {
            ChenCharacter player = (ChenCharacter) p;
            player.useSkill2AttackAnimation();
        }

        this.addToBot(new SFXAction("ATTACK_HEAVY"));
        this.addToBot(new VFXAction(p, new CleaveEffect(), 0.1f));
        this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));

//        // 遍历游戏中所有存活的敌人（关键：实现全体攻击）
//        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
//            if (!monster.isDeadOrEscaped()) {
//                // 对每个存活敌人造成伤害
//                addToBot(new DamageAction(monster, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
//            }
//        }

    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.THORNS)
                    ));
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeDamage(UPG_DAMAGE); // 应用伤害升级
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SlashCard();
    }
}