package chenmod.relics;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;


public class ChiXiaoRelic extends BaseRelic{
    private static final String NAME = "ChiXiaoRelic"; //The name will be used for determining the image file as well as the ID.
    public static final String ID = ChenMod.makeID(NAME); //This adds the mod's prefix to the relic ID, resulting in modID:MyRelic
    private static final RelicTier RARITY = RelicTier.STARTER; //The relic's rarity.
    private static final LandingSound SOUND = LandingSound.MAGICAL; //The sound played when the relic is clicked.

    private static final int MAGIC_NUMBER = 1; //For convenience of changing it later and clearly knowing what the number means instead of just having a 10 sitting around in the code.

    private static final int LOSE_HP = 1;

    private boolean isFirst = true;

    public ChiXiaoRelic() {
        super(ID, NAME, ChenCharacter.Meta.CARD_COLOR, RARITY, SOUND);
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {

        if(ChenModConfig.ORANGEAL_RELIC)
            return;

        if(targetCard.type == AbstractCard.CardType.SKILL){

            if(AbstractDungeon.player.currentHealth > 1){
                AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, LOSE_HP, DamageInfo.DamageType.HP_LOSS));
            }

            AbstractRelic chiXiaoPlus = AbstractDungeon.player.getRelic(ChiXiaoPlusRelic.ID);

            if (chiXiaoPlus!=null && isFirst){

                this.isFirst=false;

                chiXiaoPlus.flash();
                addToBot(new ApplyPowerAction(AbstractDungeon.player,
                  AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, MAGIC_NUMBER), MAGIC_NUMBER, true, AbstractGameAction.AttackEffect.NONE));

            }else{

                this.flash();
                addToBot(new ApplyPowerAction(AbstractDungeon.player,
                        AbstractDungeon.player, new VigorPower(AbstractDungeon.player, MAGIC_NUMBER), MAGIC_NUMBER, true, AbstractGameAction.AttackEffect.NONE));

            }

            AbstractDungeon.player.addBlock(1);

        }

    }

    @Override
    public void onPlayerEndTurn() {
        this.isFirst = true;
    }

    @Override
    public void atTurnStart() {

        if(!ChenModConfig.ORANGEAL_RELIC)
            return;

        this.flash();
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player,
                        AbstractDungeon.player, new VigorPower(AbstractDungeon.player, 1), 1, true, AbstractGameAction.AttackEffect.NONE));
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));  // 这是特效......
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

    // 遗物复制方法（必填）
    @Override
    public AbstractRelic makeCopy() {
        return new ChiXiaoRelic();
    }

}
