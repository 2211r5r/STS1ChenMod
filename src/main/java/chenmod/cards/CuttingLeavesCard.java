package chenmod.cards;

import chenmod.ChenMod;
import chenmod.actions.AddCostForTurnAction;
import chenmod.actions.AddHitTimesAction;
import chenmod.actions.DoubleSwordsAction;
import chenmod.actions.PlayTopCardButSkipSpecialCardAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
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

public class CuttingLeavesCard extends BaseCard {

    public static final String ID = makeID(CuttingLeavesCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 1;

    private static final int MAGIC = 2;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public CuttingLeavesCard() {
        super(ID, info);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(MAGIC);

        tags.add(CustomTags.MULTIPLE_ATTACKS);
        tags.add(CustomTags.CHIXIAO);

        this.returnToHand = true;

    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
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
    public boolean hasEnoughEnergy() {
        // 永远返回 true，绕过默认能量不足的限制
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));
        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        if (availableEnergy < currentCost) {
            int hpLoss = currentCost - availableEnergy;
            ChenMod.logger.info("当前费用："+currentCost+", 当前能量:"+availableEnergy);
            this.addToBot(new LoseHPAction(p, p, hpLoss));      // 血量补足
        }

        if (p instanceof ChenCharacter) {
            // 第二步：安全强转（100%不会报错）
            ChenCharacter player = (ChenCharacter) p;
            player.useAttackAnimation();
        }

        for(int i = 0; i < this.magicNumber; ++i){
            addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL),
                    i % 2 ==0 ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL: AbstractGameAction.AttackEffect.SLASH_VERTICAL // 横批
            ));
        }

        addToBot(new AddCostForTurnAction(this));

    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.THORNS))
            );
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName();
            upgradeDamage(UPG_DAMAGE);
            upgradeBaseCost(0);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CuttingLeavesCard();
    }
}