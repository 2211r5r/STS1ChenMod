package chenmod.powers;

import chenmod.ChenMod;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BreakBlockPower_monster extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(BreakBlockPower_monster.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false; //不为回合制效果（回合结束后不移除）

    public BreakBlockPower_monster(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    public BreakBlockPower_monster(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, 75);
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {

        if (type == DamageInfo.DamageType.NORMAL) {
            return damage + (int)Math.max(0, AbstractDungeon.player.currentBlock * this.amount * 0.01f);
        }

        return damage;
    }

    @Override
    public void update(int slot){
        super.update(slot);

        if(this.owner instanceof AbstractMonster){
            ((AbstractMonster)this.owner).applyPowers();
        }
    }

}
