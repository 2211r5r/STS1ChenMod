package chenmod.actions;

import chenmod.ChenMod;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class CooperationAction extends AbstractGameAction {

    private final boolean freeToPlayOnce;

    private final AbstractPlayer p ;

    private final int energyOnUse;

    private final boolean isUpgraded;

    public CooperationAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse, boolean isUpgraded){

        this.p = p;

        this.energyOnUse = energyOnUse;

        this.freeToPlayOnce = freeToPlayOnce;

        this.isUpgraded = isUpgraded;

        this.isDone = false;

    }

    @Override
    public void update() {

        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }
        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if (isUpgraded){
            effect ++;
        }

        ChenMod.logger.info("Cooperation Action, this.freeToPlayOnce ="+this.freeToPlayOnce);
        ChenMod.logger.info("Cooperation Action, this.effect ="+effect);

        if (effect > 0) {

            CardCrawlGame.sound.playV(Sounds.cooperationActionVoice, 1.2f);
            this.addToBot(new ReducePowerAction(p,p, LoseDexterityPower.POWER_ID,effect));
            this.addToBot(new ReducePowerAction(p,p, LoseStrengthPower.POWER_ID,effect));

            if (!this.freeToPlayOnce) {
                this.p.energy.use(EnergyPanel.totalCount);
            }
        }

        this.isDone = true;
    }
}
