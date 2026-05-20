package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ZhuangShiAction extends AbstractGameAction {


    public ZhuangShiAction(final AbstractPlayer p, final int amount) {
        this.source = AbstractDungeon.player;
    }

    @Override
    public void update() {
        
        this.isDone = true;
    }
}
