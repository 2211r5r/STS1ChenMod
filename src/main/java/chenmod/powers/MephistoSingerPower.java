package chenmod.powers;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.monsters.Mephisto;
import chenmod.monsters.MephistoSinger;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.Objects;

public class MephistoSingerPower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(MephistoSingerPower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    public MephistoSingerPower(AbstractCreature owner) {
        super(POWER_ID, TYPE, TURN_BASED, owner, -1);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if(this.amount > 0){
            this.description = String.format(DESCRIPTIONS[0], this.amount);
        }else{
            this.description = DESCRIPTIONS[1];
        }
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void update(int slot){
        super.update(slot);
        if (this.owner instanceof MephistoSinger) {
            // 第二步：安全强转（100%不会报错）
            MephistoSinger m = (MephistoSinger) this.owner;
            if(m.currentHpPhase < m.hpPhase.size()){
                this.amount = (int) (m.maxHealth * m.hpPhase.get(m.currentHpPhase));
            }else{
                this.amount = -1;
            }
            updateDescription();
        }else{
            this.amount = -1;
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}