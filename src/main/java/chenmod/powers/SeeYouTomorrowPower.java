package chenmod.powers;

import chenmod.ChenMod;
import chenmod.actions.MakeTempUpgradedCardInHandAction;
import chenmod.util.GeneralUtils;
import chenmod.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SeeYouTomorrowPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(SeeYouTomorrowPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    private final AbstractCard card;

    private final boolean isUpgraded;

    public SeeYouTomorrowPower(AbstractCreature owner, int cardAmt, AbstractCard copyMe, boolean upgraded) {

        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, cardAmt));
        this.isUpgraded = upgraded;
        (this.card = copyMe.makeStatEquivalentCopy()).resetAttributes();

        // 逻辑ID：拼接uuid，避免叠加
        this.ID = ChenMod.makeID(POWER_ID + card.uuid.toString());

        updateDescription();

    }

    @Override
    public void updateDescription() {

        if(this.card == null) {
            this.description = DESCRIPTIONS[3];
            return;
        }

        if(this.isUpgraded){
            this.description = String.format(DESCRIPTIONS[0] + this.card.name + DESCRIPTIONS[1] + DESCRIPTIONS[2], this.amount);
        }else{
            this.description = String.format(DESCRIPTIONS[0] + this.card.name + DESCRIPTIONS[1], this.amount);
        }
    }

    @Override
    public void atStartOfTurn() {

        if(this.card == null) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            return;
        }

        if(isUpgraded){
            this.addToBot(new MakeTempUpgradedCardInHandAction(this.card, this.amount));
        }else {
            this.addToBot(new MakeTempCardInHandAction(this.card, this.amount));
        }
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
}