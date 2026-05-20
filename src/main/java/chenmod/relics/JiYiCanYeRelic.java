package chenmod.relics;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import chenmod.util.Sounds;

public class JiYiCanYeRelic extends BaseRelic{
    private static final String NAME = "JiYiCanYeRelic";
    public static final String ID = ChenMod.makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.CLINK;

    private static final int DAMAGE_PER_ENERGY = 9;

    public JiYiCanYeRelic() {
        super(ID, NAME, ChenCharacter.Meta.CARD_COLOR, RARITY, SOUND);
    }

    @Override
    public void onPlayerEndTurn() {

        int effect = EnergyPanel.totalCount;

        // 兼容Chemical X遗物
        if (AbstractDungeon.player.hasRelic("Chemical X")) {
            effect += 2;
            AbstractDungeon.player.getRelic("Chemical X").flash();
        }

        int damage = effect * DAMAGE_PER_ENERGY;

        ChenMod.logger.info("现在陈的剩余能量还有" + effect + "点，预计造成"+ damage + "点伤害." );

        if(effect > 0){
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(damage, true), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));

        }

        this.stopPulse();
        this.grayscale = false;

    }

    @Override
    public String getUpdatedDescription() {
        // 读取本地化文本的第一个描述（对应RelicStrings.json中的DESCRIPTIONS[0]）
        return DESCRIPTIONS[0];
    }

    // 遗物复制方法（必填）
    @Override
    public AbstractRelic makeCopy() {
        return new JiYiCanYeRelic();
    }
}
