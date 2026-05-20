package chenmod.effects;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class DelayedShowCardAndObtainEffect extends ShowCardAndObtainEffect {

    public DelayedShowCardAndObtainEffect(AbstractCard card, float x, float y, float holdDuration) {
        super(card, x, y);
        this.duration = holdDuration;
    }

    @Override
    public void update() {
        super.update(); // 正常执行飞入牌组的逻辑
    }
}

