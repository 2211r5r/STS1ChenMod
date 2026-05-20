package chenmod.relics;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ChiXiaoPlusRelic extends BaseRelic{
    private static final String NAME = "ChiXiaoPlusRelic";
    public static final String ID = ChenMod.makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public ChiXiaoPlusRelic() {
        super(ID, NAME, ChenCharacter.Meta.CARD_COLOR, RARITY, SOUND);
    }

    @Override
    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return this.setDescription(AbstractDungeon.player.chosenClass);
        }
        return this.setDescription(null);
    }

    private String setDescription(final AbstractPlayer.PlayerClass c) {

        if(c == null){
            return this.DESCRIPTIONS[0];
        }

        if(ChenModConfig.ORANGEAL_RELIC){
            return this.DESCRIPTIONS[1];
        }else {
            return this.DESCRIPTIONS[2];
        }
    }

    @Override
    public void updateDescription(final AbstractPlayer.PlayerClass c) {
        this.description = this.setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEquip() {

        if(ChenModConfig.ORANGEAL_RELIC){
            this.counter = 0;
        }else{
            this.counter = -1;
        }

    }

    @Override
    public void atTurnStart() {

        if(!ChenModConfig.ORANGEAL_RELIC){
            return;
        }

        if (this.counter == -1) {
            this.counter += 2;
        }
        else {
            ++this.counter;
        }
        if (this.counter == 3) {
            this.counter = 0;
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player,
                  AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 1), 1, true, AbstractGameAction.AttackEffect.NONE));

        }
    }


    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player.hasRelic(ChiXiaoRelic.ID);
    }


    // 遗物复制方法（必填）
    @Override
    public AbstractRelic makeCopy() {
        return new ChiXiaoPlusRelic();
    }
}
