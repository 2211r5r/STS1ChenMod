package chenmod.powers;

import chenmod.ChenMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

public class HaiSiPower extends BasePower{

    public static final String POWER_ID = ChenMod.makeID(HaiSiPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    private int maxHpBeforeThisPower = 1;

    public HaiSiPower(AbstractCreature owner, int amount, int maxHpBeforeThisPower) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);

        this.maxHpBeforeThisPower = maxHpBeforeThisPower;

        this.updateDescription();
        ChenMod.logger.info("获得此Power时的生命值为："+this.maxHpBeforeThisPower+"点");
    }
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 1. 伤害被格挡 → 不触发
        if (damageAmount <= 0) {
            return damageAmount;
        }

        // 2. 排除不应触发的伤害类型
        if (info.type == DamageInfo.DamageType.HP_LOSS ||
                info.type == DamageInfo.DamageType.THORNS) {
            return damageAmount;
        }

        // 3. 排除自伤
        if (info.owner == this.owner) {
            return damageAmount;
        }

        // 4. 正常触发效果
        if (this.owner.maxHealth > 1) {
            this.flash();

            // 扣最大生命值
            this.owner.decreaseMaxHealth(damageAmount);

            // 显示效果
            AbstractDungeon.effectList.add(
                    new TextAboveCreatureEffect(
                            this.owner.hb.cX,
                            this.owner.hb.cY + 50.0F,
                            String.format(DESCRIPTIONS[1], damageAmount),
                            Color.RED
                    )
            );

            // 阻止当前生命值减少
            return 0;
        }

        return damageAmount;
    }
    @Override
    public void onVictory(){
        int gap = this.owner.maxHealth - this.maxHpBeforeThisPower;
        if(gap > 0){
            this.owner.decreaseMaxHealth(gap);
            this.owner.increaseMaxHp(Math.min(3,gap),true);
        }
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.maxHpBeforeThisPower);
    }

}
