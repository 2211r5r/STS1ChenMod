package chenmod.actions;

import chenmod.character.ChenCharacter;
import chenmod.effects.JueYingEffect;
import chenmod.util.Sounds;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TranquilityAction extends AbstractGameAction {

    private final DamageInfo info;

    private static final AbstractPlayer p = AbstractDungeon.player;

    private boolean isAttackIntent;

    private boolean firstAnimation;

    public TranquilityAction(final AbstractCreature target, final DamageInfo info) {
        this.setValues(target, this.info = info);
        this.actionType = ActionType.DAMAGE;
        this.duration = 1.6f;
        this.isAttackIntent = false;
        this.firstAnimation = true;
    }

    @Override
    public void update() {

        if(this.firstAnimation){
            if (p instanceof ChenCharacter) {
                // 第二步：安全强转（100%不会报错）
                ChenCharacter player = (ChenCharacter) p;
                player.useSkill2AttackAnimation();
            }
            this.firstAnimation = false;
        }

        if(this.target == null || this.target.isDeadOrEscaped()){
            return;
        }

        if(this.duration >=0.5f && !this.isAttackIntent){

            int intentBaseDamage =  ((AbstractMonster)this.target).getIntentBaseDmg();
            if(intentBaseDamage > 0){
                // 1. 动态刀光轨迹（划过目标）
                AbstractDungeon.effectList.add(new JueYingEffect(
                        this.target.hb.cX,  // 目标中心X坐标
                        this.target.hb.cY   // 目标中心Y坐标
                ));

                // 2. 刀光命中冲击特效（目标中心爆闪）
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.combat.AdditiveSlashImpactEffect(
                        this.target.hb.cX,                // 冲击中心X（目标中心）
                        this.target.hb.cY,                // 冲击中心Y（目标中心）
                        Color.RED.cpy().mul(1.2f, 1.0f, 1.0f, 1.0f) // 冲击颜色（加深红色）
                ));

                this.target.damage(this.info);
            }

            this.isAttackIntent = true;
        }

        if (this.duration >=1.5f) {

            // 1. 动态刀光轨迹（划过目标）
            AbstractDungeon.effectList.add(new JueYingEffect(
                    this.target.hb.cX,  // 目标中心X坐标
                    this.target.hb.cY   // 目标中心Y坐标
            ));

            CardCrawlGame.sound.play(Sounds.jueYingEffect_3);

            // 2. 刀光命中冲击特效（目标中心爆闪）
            AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.combat.AdditiveSlashImpactEffect(
                    this.target.hb.cX,                // 冲击中心X（目标中心）
                    this.target.hb.cY,                // 冲击中心Y（目标中心）
                    Color.RED.cpy().mul(1.2f, 1.0f, 1.0f, 1.0f) // 冲击颜色（加深红色）
            ));

            this.target.damage(this.info);

            this.isDone = true;

        }
        this.tickDuration();
    }
}
