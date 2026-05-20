package chenmod.relics;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SummerRelic extends BaseRelic{
    private static final String NAME = "SummerRelic";
    public static final String ID = ChenMod.makeID(NAME);
    private static final RelicTier RARITY = RelicTier.BOSS;
    private static final LandingSound SOUND = LandingSound.CLINK;

    private static final int COST = 648;

    public SummerRelic() {
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
        return this.DESCRIPTIONS[0];
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

        final EnergyManager energy = AbstractDungeon.player.energy;
        ++energy.energyMaster;

        int loseGoldValue = Math.min(COST, AbstractDungeon.player.gold);
        CardCrawlGame.sound.play("GOLD_JINGLE");
        AbstractDungeon.player.loseGold(loseGoldValue);
    }

    @Override
    public void onUnequip() {
        final EnergyManager energy = AbstractDungeon.player.energy;
        --energy.energyMaster;
    }

    // 遗物复制方法（必填）
    @Override
    public AbstractRelic makeCopy() {
        return new SummerRelic();
    }
}
