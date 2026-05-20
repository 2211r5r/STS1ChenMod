package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.powers.FrozenPower;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.ClashEffect;

public class RoaringFlareCard extends BaseCard{
    public static final String ID = makeID(RoaringFlareCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.RARE, // 稀有度
            CardTarget.SELF, // 目标
            4 // 基础费用
    );

    public RoaringFlareCard() {
        super(ID, info); // 调用父类构造方法
        this.magicNumber = 0;
        this.exhaust = true;
    }

    @Override
    public boolean hasEnoughEnergy() {
        // 永远返回 true，绕过默认能量不足的限制
//        ChenMod.logger.info("【怒号光明】绕过默认能量不足的限制");
        return true;
    }

    @Override
    public void applyPowers() {
        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        int hpLoss = currentCost - availableEnergy;

        super.applyPowers();
        this.rawDescription = String.format(this.cardStrings.EXTENDED_DESCRIPTION[0], hpLoss, hpLoss);
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        // 保留原有的缠绕检查
        if (AbstractDungeon.player.hasPower("Entangled") && this.type == CardType.ATTACK) {
            this.cantUseMessage = TEXT[10];
            return false;
        }

        // 检查 FrozenPower 的限制
        for (AbstractPower pow : p.powers) {
            if (!pow.canPlayCard(this)) {
                this.cantUseMessage = pow.description; // 或者自定义提示
                return false;
            }
        }

        // 跳过能量检查，允许使用
        return this.cardPlayable(m);
    }

    @Override
    public void use(final AbstractPlayer p, final AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));
        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        if (availableEnergy < currentCost) {
            int hpLoss = currentCost - availableEnergy;

            ChenMod.logger.info("当前费用："+currentCost+", 当前能量:"+availableEnergy);

//            AbstractDungeon.player.energy.use(availableEnergy); // 用光能量
            this.addToBot(new LoseHPAction(p, p, hpLoss));      // 血量补足
            this.addToBot(new DrawCardAction(p, hpLoss));
            this.addToBot(new GainEnergyAction(hpLoss));
        }
//        else {
//            AbstractDungeon.player.energy.use(currentCost);     // 正常消耗能量
//        }

    }
    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeBaseCost(5);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new RoaringFlareCard();
    }
}
