package chenmod.powers;

import chenmod.ChenMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;

public class NewBeginningPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(NewBeginningPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    private boolean hasUseCard;

    public NewBeginningPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0]);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void onInitialApplication() {
        this.hasUseCard = false;
    }

    @Override
    public void atStartOfTurn() {
        this.hasUseCard = false;
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.hasUseCard = true;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !this.hasUseCard) {
            this.addToBot(new VFXAction(new WhirlwindEffect(new Color(1.0f, 0.9f, 0.4f, 1.0f), true)));

            final int count = AbstractDungeon.player.hand.size();
            for (int i = 0; i < count; ++i) {
                if (Settings.FAST_MODE) {
                    this.addToBot(new ExhaustAction(1, true, true, false, Settings.ACTION_DUR_XFAST));
                }
                else {
                    this.addToBot(new ExhaustAction(1, true, true));
                }
            }

            this.addToBot(new SkipEnemiesTurnAction());
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }
}