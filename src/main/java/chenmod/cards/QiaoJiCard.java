package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.powers.StunPower;
import chenmod.util.CardStats;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class QiaoJiCard extends BaseCard{
    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(QiaoJiCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色（铁clad）
            AbstractCard.CardType.ATTACK, // 卡牌类型（攻击）
            AbstractCard.CardRarity.UNCOMMON, // 稀有度（稀有）
            AbstractCard.CardTarget.ENEMY, // 目标（单个敌人）
            2 // 基础费用
    );

    // 攻击伤害
    private static final int DAMAGE = 5;
    // 升级后伤害
    private static final int UPG_DAMAGE = 3;

    public QiaoJiCard() {
        super(ID, info); // 调用父类构造方法
        // 设置基础伤害，升级后增加伤害
        setDamage(DAMAGE, UPG_DAMAGE);

        this.exhaust = true;

        tags.add(CustomTags.CHIXIAO);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.qiaoJiEffect);
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));


        if (p instanceof ChenCharacter) {
            ChenCharacter player = (ChenCharacter) p;
            player.useSkillAttackAnimation();
        }

        addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));

        if(!m.isDeadOrEscaped() && !m.hasPower(StunPower.POWER_ID)){
            addToBot(new ApplyPowerAction(
                    m, p,
                    new StunPower(m)
            ));
        }

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
            upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new QiaoJiCard();
    }
}
