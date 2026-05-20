package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.LoseBarricadePower;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class UnstoppableCard extends BaseCard {
    public static final String ID = makeID(UnstoppableCard.class.getSimpleName());

    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER,                // 卡牌类型
            CardRarity.RARE,               // 稀有度
            CardTarget.SELF,               // 目标
            3                              // 基础费用
    );

    // 基础力量加成系数：未升级2倍，升级后3倍
    private static final int MAGIC = 5;

    public UnstoppableCard() {
        super(ID, info); // 调用父类构造方法
        this.isInnate = false;

        // 设置magicNumber为力量加成系数（2/3）
        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));

        if(!player.hasPower(BarricadePower.POWER_ID)){
            this.addToBot(new ApplyPowerAction(player, player, new BarricadePower(player)));
        }

        if(!player.hasPower(LoseBarricadePower.POWER_ID)){
            this.addToBot(new ApplyPowerAction(player, player, new LoseBarricadePower(player)));
        }



        // 1. 收集抽牌堆中所有带DEFEND标签的技能牌（排除自身）
        CardGroup skillCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : player.drawPile.group) {
            if (c.type == CardType.SKILL && c.hasTag(CustomTags.DEFEND) && !c.cardID.equals(this.cardID)) {
                skillCards.addToBottom(c);
            }
        }
        int skillCount = upgraded
                ? skillCards.size()
                : Math.min(this.magicNumber, skillCards.size());

        // 3. 分情况处理技能牌
        if (skillCount > 0) {

            // 2. 应用力量加成
            this.addToBot(new ApplyPowerAction(
                    player, player,
                    new StrengthPower(player, skillCount),
                    skillCount
            ));

            for (int i = 0; i < skillCount; i++) {
                AbstractCard card = skillCards.group.get(i);

                // 先从抽牌堆移除卡牌（保证视觉上“取出”）
                if (player.drawPile.contains(card)) {
                    player.drawPile.removeCard(card);
                }

                // 通用：设置卡牌动画属性（升级/未升级都用这套）
                AbstractMonster targetMonster = null;
                if (!AbstractDungeon.getCurrRoom().monsters.monsters.isEmpty()) {
                    targetMonster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                }

                // 标记打出后消耗（触发枯木树枝）
                card.exhaustOnUseOnce = true;

                // 放入limbo准备播放动画
                AbstractDungeon.player.limbo.group.add(card);
                // 卡牌动画位置设置（和升级后完全一致）
                card.current_y = -200.0F * Settings.scale;
                card.target_x = (float) Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
                card.target_y = (float) Settings.HEIGHT / 2.0F;
                card.targetAngle = 0.0F;
                card.lighten(false);
                card.drawScale = 0.12F;
                card.targetDrawScale = 0.75F;
                card.applyPowers(); // 仅计算属性，不执行效果

//                if (this.upgraded) {
                    // ===== 升级后：正常执行use() + 播放动画 + 消耗 =====
                if (targetMonster != null) {
                    this.addToBot(new NewQueueCardAction(card, targetMonster, false, true));
                } else {
                    this.addToBot(new NewQueueCardAction(card, player, false, true));
                }
                this.addToBot(new UnlimboAction(card));
//                } else {
//                    // ===== 未升级：仅播放动画 + 消耗，跳过use() =====
//                    // 步骤1：播放卡牌打出的视觉特效（和正常打出一致）
//                    this.addToBot(new AbstractGameAction() {
//                        @Override
//                        public void update() {
//
//                            AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect(card));
//                            this.isDone = true;
//                        }
//                    });
//
//                    // 步骤2：手动将卡牌移到消耗区（跳过use()，但触发消耗回调）
//                    this.addToBot(new AbstractGameAction() {
//                        @Override
//                        public void update() {
//                            // 核心：只消耗，不执行card.use()
//                            if (AbstractDungeon.player.limbo.contains(card)) {
//                                AbstractDungeon.player.limbo.moveToExhaustPile(card);
//                                CardCrawlGame.dungeon.checkForPactAchievement();
//                            }
//                            this.isDone = true;
//                        }
//                    });
//
//                    // 步骤3：移出limbo，清理临时区域
//                    this.addToBot(new UnlimboAction(card));
//                }

                // 通用：动画间隔（保证顺序）
                if (!Settings.FAST_MODE) {
                    this.addToBot(new WaitAction(Settings.ACTION_DUR_MED));
                } else {
                    this.addToBot(new WaitAction(Settings.ACTION_DUR_FASTER));
                }
            }
        }
    }

    // 卡牌升级逻辑
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        UnstoppableCard copy = new UnstoppableCard();
        if (this.upgraded) {
            copy.upgrade();
        }
        return copy;
    }
}