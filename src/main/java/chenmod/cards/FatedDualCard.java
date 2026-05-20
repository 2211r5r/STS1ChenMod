package chenmod.cards;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
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

import java.util.Objects;

public class FatedDualCard extends BaseCard{
    public static final String ID = makeID(FatedDualCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.ENEMY, // 目标
            0 // 基础费用
    );

    private static final int BASE_DAMAGE = 12;
    private static final int BASE_DAMAGE_PER_HP = 3;
    private static final int UPG_DAMAGE_PER_HP = 2;

    private int loseHp = 0;

    public FatedDualCard() {
        super(ID, info); // 调用父类构造方法

        setDamage(BASE_DAMAGE);

        setMagic(BASE_DAMAGE_PER_HP, UPG_DAMAGE_PER_HP);

    }

    @Override
    public void applyPowers() {

        int extraCost = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!Objects.equals(c.cardID, this.cardID) && c.type == CardType.ATTACK) {
                extraCost++;
            }
        }
        this.setCostForTurn(this.cost + extraCost);

        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        this.loseHp = Math.max(currentCost - availableEnergy, 0);

        this.baseDamage = BASE_DAMAGE;

        this.baseDamage += this.loseHp * this.magicNumber;

        super.applyPowers();
        // 动态费用计算：每张其他攻击牌增加1费用

        if(this.loseHp > 0){
            int exDamage = this.loseHp * this.magicNumber;
            this.rawDescription = String.format(cardStrings.EXTENDED_DESCRIPTION[0] + cardStrings.EXTENDED_DESCRIPTION[1], this.loseHp, exDamage);
        }else{
            this.rawDescription = String.format(cardStrings.EXTENDED_DESCRIPTION[0]);
        }
        initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public boolean hasEnoughEnergy() {
        // 永远返回 true，绕过默认能量不足的限制
//        ChenMod.logger.info("【宿命对决】绕过默认能量不足的限制");
        return true;
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

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));

        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        this.loseHp = Math.max(currentCost - availableEnergy, 0);

        ChenMod.logger.info("当前费用："+currentCost+", 当前能量:"+availableEnergy);

        if (availableEnergy < currentCost) {
            this.addToBot(new LoseHPAction(p, p, this.loseHp));      // 血量补足
        }

        if (p instanceof ChenCharacter) {
            ChenCharacter player = (ChenCharacter) p;
            player.useAttackAnimation();
        }

        // 攻击效果
        if (m != null) {
            this.addToBot(new VFXAction(new ClashEffect(m.hb.cX, m.hb.cY), 0.1f));
            this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
        }

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgraded = true;
            upgradeMagicNumber(UPG_DAMAGE_PER_HP);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FatedDualCard();
    }
}
