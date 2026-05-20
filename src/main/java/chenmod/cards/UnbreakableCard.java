package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
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

public class UnbreakableCard extends BaseCard {
    public static final String ID = makeID(UnbreakableCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER,                // 卡牌类型
            CardRarity.RARE,               // 稀有度
            CardTarget.SELF,               // 目标
            3                              // 基础费用
    );

    private static final int BASE_SKILL_COUNT = 5;
    private static final int UPG_SKILL_COUNT = 1;

    public UnbreakableCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_SKILL_COUNT, UPG_SKILL_COUNT);
        this.isInnate = false;

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer player, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));

        this.addToBot(new ApplyPowerAction(player, player, new BarricadePower(player)));

        // 只拿【前 magicNumber 张】技能牌，并且不破坏牌堆
        CardGroup skillCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : player.drawPile.group) {
            if (c.type == AbstractCard.CardType.SKILL && c.hasTag(CustomTags.DEFEND)) {
                skillCards.addToBottom(c);
                if (skillCards.size() >= this.magicNumber) {
                    break;
                }
            }
        }

        int count = skillCards.size();
        for (int i = 0; i < count; i++) {
            AbstractCard card = skillCards.group.get(i);

            if (player.drawPile.contains(card)) {
                player.drawPile.removeCard(card); // 从抽牌堆移除
            }

            // 随机目标
            AbstractMonster monster = null;
            if (!AbstractDungeon.getCurrRoom().monsters.monsters.isEmpty()) {
                monster = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
            }

            card.exhaustOnUseOnce = false;
            // 放入limbo准备打出
            AbstractDungeon.player.limbo.group.add(card);
            // 卡牌动画位置设置
            card.current_y = -200.0F * Settings.scale;
            card.target_x = (float) Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
            card.target_y = (float) Settings.HEIGHT / 2.0F;
            card.targetAngle = 0.0F;
            card.lighten(false);
            card.drawScale = 0.12F;
            card.targetDrawScale = 0.75F;
            card.applyPowers();

            // 加入打出队列（改用addToBot保证顺序）
            if (monster != null) {
                this.addToBot(new NewQueueCardAction(card, monster, false, true));
            } else {
                this.addToBot(new NewQueueCardAction(card, player, false, true));
            }
            this.addToBot(new UnlimboAction(card));

            // 动画间隔
            if (!Settings.FAST_MODE) {
                this.addToBot(new WaitAction(Settings.ACTION_DUR_MED));
            } else {
                this.addToBot(new WaitAction(Settings.ACTION_DUR_FASTER));
            }
        }
    }

    // 卡牌升级逻辑
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeMagicNumber(UPG_SKILL_COUNT);
            this.isInnate = true; // 升级后变为固有

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        UnbreakableCard copy = new UnbreakableCard();
        if (this.upgraded) {
            copy.upgrade();
        }
        return copy;
    }
}