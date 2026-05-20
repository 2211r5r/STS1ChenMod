package chenmod.actions;

import chenmod.ChenMod;
import chenmod.powers.LastMarchPower;
import chenmod.util.Sounds;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class LastMarchAction extends AbstractGameAction {

    private int marchBlock = 0;
    private int marchDamage = 0;
    private float finalOffsetX = 0.0f;
    private float finalOffsetY = 0.0f;

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }


    // ---与行军动画是否结束有关的变量---
    private boolean animationFinished = false;
    private boolean listenerAdded = false;

    private AnimationState.AnimationStateListener animationListener;

    public LastMarchAction(AbstractMonster monster, final float finalOffsetX, final float finalOffsetY, final int marchDamage, final int marchBlock){
        this.target = monster;
        this.finalOffsetX = finalOffsetX;
        this.finalOffsetY = finalOffsetY;
        this.marchBlock = marchBlock;
        this.marchDamage = marchDamage;

        this.isDone = false;
    }

    @Override
    public void update() {

        if (!listenerAdded) {
            listenerAdded = true;
            ChenMod.logger.info("【"+this.target.name+"】的行军: 将listenerAdded设置为"+ listenerAdded);


            if (state38 != null) {
                animationListener = new AnimationState.AnimationStateListener() {
                    @Override
                    public void complete(AnimationState.TrackEntry entry) {
                        animationFinished = true;
                        ChenMod.logger.info("行军动画 结束。");
                    }   // 动画完成时，执行此函数内部的逻辑
                    @Override public void start(AnimationState.TrackEntry entry) {}    // 动画开始时,执行此内部的逻辑
                    @Override public void interrupt(AnimationState.TrackEntry entry) {}
                    @Override public void end(AnimationState.TrackEntry entry) {}
                    @Override public void dispose(AnimationState.TrackEntry entry) {}
                    @Override public void event(AnimationState.TrackEntry entry, Event event) {}
                };

                state38.addAnimation(0,"Move_Begin", false, 0.0f);
                state38.addAnimation(0,"Move_Loop", false, 0.0f);
                ChenMod.logger.info("行军: 调用Move动画");
                state38.addListener(animationListener);
                ChenMod.logger.info("行军: 添加动画的监听");
            }
        }

        // 等待动画结束
        if (animationFinished) {
            ChenMod.logger.info("行军动画已经结束，准备调用停止移动并攻击+防守的的动画。");
            if (animationListener != null) {
                state38.removeListener(animationListener);

                state38.addAnimation(0, "Move_End", true, 0.0f);
                state38.addAnimation(0,"Attack", false, 0.0f);
                state38.addAnimation(0, "Idle", true, 0.0f);

                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(
                        this.target,
                        this.target,
                        marchBlock)
                );

                AbstractDungeon.actionManager.addToBottom(new DamageAction(
                        AbstractDungeon.player,
                        new DamageInfo(this.target, marchDamage),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY)
                );


                // 释放引用，避免内存泄漏
                animationListener = null;
            }
            this.isDone = true;
            ChenMod.logger.info("行军Action结束。");
        }

    }
}
