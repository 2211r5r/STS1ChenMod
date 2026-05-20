package chenmod.actions;

import chenmod.powers.HaiSiPower;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class HaiSiAction extends AbstractGameAction {

    private final boolean freeToPlayOnce;

    private final AbstractPlayer p ;

    private final int energyOnUse;


    public HaiSiAction(final AbstractPlayer p, final int amount, final boolean freeToPlayOnce, final int energyOnUse) {
        this.setValues(target, source, amount);

        this.duration = 0.1f;
        this.p = p;

        this.energyOnUse = energyOnUse;
        this.freeToPlayOnce = freeToPlayOnce;
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
        if (effect > 0) {

            CardCrawlGame.sound.playV(Sounds.haiSiPowerEffect, 1.2f);

            int maxHpBeforeThisPower = this.p.maxHealth;

            int increasedMaxHp = this.amount * effect;

            this.p.increaseMaxHp(increasedMaxHp, false);

            this.addToBot(new ApplyPowerAction(p,
                    p,
                    new HaiSiPower(p, 0, maxHpBeforeThisPower)
            ));

            if (!this.freeToPlayOnce) {
                this.p.energy.use(EnergyPanel.totalCount);
            }
        }
        this.isDone = true;
    }

}
