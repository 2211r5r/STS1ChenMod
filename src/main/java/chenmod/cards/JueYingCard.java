package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.actions.JueYingAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

import java.util.ArrayList;
import java.util.List;



public class JueYingCard extends BaseCard{
    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(JueYingCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR,
            CardType.ATTACK, // 卡牌类型（攻击）
            CardRarity.SPECIAL, // 稀有度（特殊，不加入商店，战斗奖励）
            CardTarget.ENEMY, // 目标（单个敌人）
            3 // 基础费用
    );

    // 攻击伤害
    private static final int DAMAGE = 1;

    private static final int ATTACK_TIMES = 10;

    private static final int INTANGIBLE_AMOUNT = 1;

    public static final List<String> attackVoicePool = new ArrayList<>();

    static {
        attackVoicePool.add(Sounds.attackVoice_3);
        attackVoicePool.add(Sounds.attackVoice_4);
    }

    public JueYingCard() {
        super(ID, info); // 调用父类构造方法

        setDamage(DAMAGE);
        setMagic(ATTACK_TIMES);

        this.upgraded = false;

        this.exhaust = true;

        this.setDisplayRarity(CardRarity.RARE);

        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.MULTIPLE_ATTACKS);
    }

    public JueYingCard(boolean isUpgraded) {
        super(ID, info); // 调用父类构造方法

        setDamage(DAMAGE);
        setMagic(ATTACK_TIMES);

        this.upgraded = isUpgraded;

        this.exhaust = true;

        // 终于找到了，调用一下父类CustomCard，使得卡虽然是SPECIAL，但是视觉上是金色RARE
        this.setDisplayRarity(CardRarity.RARE);

        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.MULTIPLE_ATTACKS);

        if (isUpgraded) {
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        // 空值保护：避免玩家/敌人为空导致闪退
        if (p == null || m == null || m.isDeadOrEscaped()) {
            return;
        }

        this.addToBot(new ApplyPowerAction(
                p,
                p,
                new IntangiblePlayerPower(p, INTANGIBLE_AMOUNT),
                INTANGIBLE_AMOUNT
        ));

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(attackVoicePool));

        this.addToBot(new JueYingAction(
                m,
                new DamageInfo(p, Math.max(this.damage, 1), this.damageTypeForTurn),
                this.magicNumber,
                true,
                this.upgraded)
        );
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

            // 更新描述
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new JueYingCard(this.upgraded);
    }
}
