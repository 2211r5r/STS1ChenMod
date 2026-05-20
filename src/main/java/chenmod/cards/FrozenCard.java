package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Objects;

public class FrozenCard extends BaseCard{
    public static final String ID = makeID(FrozenCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, // 卡牌颜色
            CardType.STATUS, // 卡牌类型
            CardRarity.SPECIAL, // 稀有度
            CardTarget.NONE, // 目标
            1 // 基础费用
    );

    public FrozenCard() {
        super(ID, info); // 调用父类构造方法
        this.exhaust = true;    // 消耗
//         this.isEthereal = true; // 虚无
        this.selfRetain = true; // 保留（回合结束不弃牌）
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() >= 2
                && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2).type == CardType.STATUS)
        {
            this.addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public void applyPowers() {

        super.applyPowers();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            monster.applyPowers();
        }

    }


    @Override
    public void triggerOnExhaust() {

        boolean hasFrozenCard = AbstractDungeon.player.hand.group
                .stream()
                .anyMatch(c -> Objects.equals(c.cardID, FrozenCard.ID));

        if(!hasFrozenCard){
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                monster.applyPowers();
//                ChenMod.logger.info("怪物【"+monster.name+"】意图已经刷新--玩家手牌中没有霜冻，伤害降低");
            }
        }

    }

    @Override
    public void onMoveToDiscard() {

        boolean hasFrozenCard = AbstractDungeon.player.hand.group
                .stream()
                .anyMatch(c -> Objects.equals(c.cardID, FrozenCard.ID));

        if(!hasFrozenCard){
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                monster.applyPowers();
//                ChenMod.logger.info("怪物【"+monster.name+"】意图已经刷新--玩家手牌中没有霜冻，伤害降低");
            }
        }

    }

    @Override
    public void triggerOnGlowCheck() {
        if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty() && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1).type == CardType.STATUS) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
        else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FrozenCard();
    }
}