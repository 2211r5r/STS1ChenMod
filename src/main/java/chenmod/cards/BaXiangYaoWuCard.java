package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.LiberationPower;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class BaXiangYaoWuCard extends BaseCard {
    public static final String ID = makeID(BaXiangYaoWuCard.class.getSimpleName());
    // 卡牌字符串（用于提示文本）
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.ALL_ENEMY, // 目标
            2 // 基础费用
    );

    // 未升级：偷取最多3点力量；升级：偷取全部力量
    private static final int BASE_STEAL_STRENGTH = 3;
    private static final int UPG_STEAL_STRENGTH = 996;

    public BaXiangYaoWuCard() {
        super(ID, info); // 调用父类构造方法
        setMagic(BASE_STEAL_STRENGTH, UPG_STEAL_STRENGTH);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        for (AbstractMonster targetMonster : AbstractDungeon.getCurrRoom().monsters.monsters){
            int enemyStrength = 0;
            if (targetMonster.hasPower(StrengthPower.POWER_ID)) {
                StrengthPower enemyStrengthPower = (StrengthPower) targetMonster.getPower(StrengthPower.POWER_ID);
                enemyStrength = Math.max(enemyStrengthPower.amount, 0); // 仅取正数
            }
            // 有正数力量，可以偷取
            if (enemyStrength > 0) {

                int stealAmount = 0;
                if (upgraded) {
                    stealAmount = enemyStrength; // 升级后偷取全部
                } else {
                    stealAmount = Math.min(enemyStrength, this.magicNumber); // 未升级最多偷3点
                }

                // 偷取逻辑
                if (stealAmount > 0) {
                    // 移除敌人的对应力量
                    this.addToBot(new ReducePowerAction(targetMonster, p, StrengthPower.POWER_ID, stealAmount));
                    // 给玩家添加同等力量
                    this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, stealAmount), stealAmount, AbstractGameAction.AttackEffect.NONE));
                    // 可选：偷取成功音效
                    CardCrawlGame.sound.play("POWER_STRENGTH", 0.1f);
                }

            }else{
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 1.8f, cardStrings.EXTENDED_DESCRIPTION[0], true));
            }

        }
    }

    // 卡牌升级逻辑
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPG_STEAL_STRENGTH);

            // 升级后更新描述
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BaXiangYaoWuCard();
    }
}