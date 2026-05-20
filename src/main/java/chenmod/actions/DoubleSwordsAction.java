package chenmod.actions;

import chenmod.effects.DoubleSwordsEffect;
import chenmod.util.Sounds;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DoubleSwordsAction extends AbstractGameAction {

    private DamageInfo info;

    public DoubleSwordsAction(final DamageInfo info) {
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = AttackEffect.NONE;
    }

    @Override
    public void update() {
        this.target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        if (this.target != null) {
            AbstractDungeon.effectList.add(
                    new DoubleSwordsEffect(this.target.hb.cX, this.target.hb.cY)
            );
            CardCrawlGame.sound.play(Sounds.jueYingEffect_2);
            // 2. 刀光命中冲击特效（目标中心爆闪）
            AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.combat.AdditiveSlashImpactEffect(
                    this.target.hb.cX,                // 冲击中心X（目标中心）
                    this.target.hb.cY,                // 冲击中心Y（目标中心）
                    Color.BLACK.cpy().mul(1.2f, 1.0f, 1.0f, 1.0f) // 冲击颜色（加深红色）
            ));
            this.addToTop(new DamageAction(this.target, this.info, AttackEffect.NONE));
        }
        this.isDone = true;
    }
}
