package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.powers.LiberationPower;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;

public class Defend_ChiXiaoCard extends BaseCard{
    public static final String ID = makeID(Defend_ChiXiaoCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.COMMON, // 稀有度
            CardTarget.SELF, // 目标
            1 // 基础费用
    );

    private static final int BASE_BLOCK = 4;

    private static final int BASE_DEXTERITY = 1;

    private static final int UPG_BLOCK = 3;


    public Defend_ChiXiaoCard() {
        super(ID, info); // 调用父类构造方法

        setBlock(BASE_BLOCK, UPG_BLOCK);

        setMagic(BASE_DEXTERITY);

        this.exhaust = true;

        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.DEFEND);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void triggerOnExhaust() {
        addToBot(new ApplyPowerAction(
                AbstractDungeon.player, AbstractDungeon.player,
                new DexterityPower(AbstractDungeon.player, this.magicNumber),
                this.magicNumber
        ));
    }

    // 抽到卡牌时，增加格挡
    @Override
    public void triggerWhenDrawn() {
        this.resetAttributes();
        this.applyPowers();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.block));
    }

    // 复制卡牌是也是
    public void triggerWhenCopied() {
        this.resetAttributes();
        this.applyPowers();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.block));

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.NORMAL)
                    ));
        }

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgraded = true;

            upgradeBlock(UPG_BLOCK);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Defend_ChiXiaoCard();
    }
}
