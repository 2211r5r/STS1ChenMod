package chenmod.cards;

import chenmod.ChenMod;
import chenmod.actions.PlayTopCardButSkipSpecialCardAction;
import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class TheBirthOfTragedyCard extends BaseCard {

    public static final String ID = makeID(TheBirthOfTragedyCard.class.getSimpleName());

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final int COST = 4;

    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public TheBirthOfTragedyCard() {
        super(ID, info);
        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public boolean hasEnoughEnergy() {
        // 永远返回 true，绕过默认能量不足的限制
        return true;
    }

    @Override
    public void applyPowers() {
        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        int hpLoss = currentCost - availableEnergy;

        super.applyPowers();
        this.rawDescription = this.cardStrings.DESCRIPTION;
        this.rawDescription += String.format(this.cardStrings.EXTENDED_DESCRIPTION[0], hpLoss);
        this.initializeDescription();
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
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));
        int currentCost = this.costForTurn;
        int availableEnergy = EnergyPanel.totalCount;

        if (availableEnergy < currentCost) {
            int hpLoss = currentCost - availableEnergy;

            ChenMod.logger.info("当前费用："+currentCost+", 当前能量:"+availableEnergy);

            this.addToBot(new LoseHPAction(p, p, hpLoss));      // 血量补足

            for (int i = 0; i < hpLoss * this.magicNumber; i++) {
                this.addToBot(new PlayTopCardButSkipSpecialCardAction(AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng), false, this));
            }

        }
//        else {
//            AbstractDungeon.player.energy.use(currentCost);     // 正常消耗能量
//        }

    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = upgraded
                ?cardStrings.UPGRADE_DESCRIPTION
                :cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPG_MAGIC);
            upgradeBaseCost(3);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TheBirthOfTragedyCard();
    }
}