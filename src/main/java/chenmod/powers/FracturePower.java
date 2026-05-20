package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FracturePower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(FracturePower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public FracturePower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        updateDescription();
    }

    public FracturePower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, 1);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        if (card.rarity == AbstractCard.CardRarity.COMMON || card.rarity == AbstractCard.CardRarity.BASIC) {
            this.flash();
            action.exhaustCard = true;

            this.addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
        }
    }
}