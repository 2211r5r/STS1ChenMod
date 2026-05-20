package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.powers.LastMarchPower;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.Map;

public class AbstractSpine38Monster extends AbstractMonster {

    private String atlas38Path1 = null;
    private String json38Path1 = null;
    private String atlas38Path2 = null;
    private String json38Path2 = null;

    private static final float SCALE = 1.30F; // 根据需要调整大小，通常是 1.0F 到 1.5F


    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }

    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表


    private boolean isSpine38Dying = false;

    public AbstractSpine38Monster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    private void loadSpine() {
        atlas38 = new TextureAtlas(Gdx.files.internal(atlas38Path1));
        SkeletonJson json = new SkeletonJson(atlas38);
        json.setScale(Settings.renderScale / SCALE);
        SkeletonData data = json.readSkeletonData(Gdx.files.internal(json38Path1));
        skeleton38 = new Skeleton(data);
        skeleton38.setColor(Color.WHITE);

        skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));

        stateData38 = new AnimationStateData(data);
        state38 = new AnimationState(stateData38);
        stateData38.setDefaultMix(0.2f);

        // 记得设置初始动画.
    }

    @Override
    public void update() {
        super.update();
        if (state38 != null) {

            float baseSpeed = 2.0f; // 你原有的全局基础速度
            float animSpeed = baseSpeed; // 默认使用全局速度

            if (state38.getCurrent(0) != null && state38.getCurrent(0).getAnimation() != null) {
                String currentAnimName = state38.getCurrent(0).getAnimation().getName();
                // 如果配置了该动画的速度，则使用配置值；否则用全局速度
                animSpeed = animSpeedMap.getOrDefault(currentAnimName, baseSpeed);
            }

            state38.update(Gdx.graphics.getDeltaTime() * animSpeed);
            state38.apply(skeleton38);
            skeleton38.updateWorldTransform();
            skeleton38.setPosition(this.drawX + this.animX, this.drawY + this.animY);
            skeleton38.setColor(this.tint.color);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        // 1. 渲染 Spine 动画
        if (!this.isDead || (state38 != null && !state38.getCurrent(0).isComplete())) {
            sb.end();
            psb.begin();
            sr.draw(psb, skeleton38);
            psb.end();
            sb.begin();
        }

        // 2. 渲染碰撞箱
        this.hb.render(sb);
        this.intentHb.render(sb);
        this.healthHb.render(sb);

        // 3. 渲染血条和名字
        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderName", SpriteBatch.class).invoke(this, sb);
        }

        // 4. 渲染意图 (带完整条件判定)
        if (!this.isDying && !this.isEscaping &&
                AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.player.isDead &&
                !AbstractDungeon.player.hasRelic("Runic Dome") &&
                this.intent != Intent.NONE &&
                !Settings.hideCombatElements) {

            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxBehind", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntent", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxAfter", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderDamageRange", SpriteBatch.class).invoke(this, sb);
        }
    }

    public void changeSpine() {
        // 1. 先 dispose 旧资源（非常重要，否则内存泄漏+贴图错乱）
        if (atlas38 != null) {
            atlas38.dispose();
            atlas38 = null;
        }

        // 2. 加载新贴图集
        atlas38 = new TextureAtlas(Gdx.files.internal(atlas38Path2));

        // 3. 加载新骨骼数据
        SkeletonJson json = new SkeletonJson(atlas38);
        json.setScale(Settings.renderScale / SCALE);
        SkeletonData newSkeletonData = json.readSkeletonData(Gdx.files.internal(json38Path2));

        // 4. 重建骨骼和动画状态
        skeleton38 = new Skeleton(newSkeletonData);
        skeleton38.setColor(Color.WHITE);
        skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));

        stateData38 = new AnimationStateData(newSkeletonData);
        stateData38.setDefaultMix(0.2f);
        state38 = new AnimationState(stateData38);

        // 记得设定初始动画
    }


    @Override
    public void takeTurn() {

    }

    @Override
    protected void getMove(int i) {

    }

    @Override
    public void die() {

        // 播放 Spine 死亡动画
        if (state38 != null) {
            this.isSpine38Dying = true;
            AnimationState.AnimationStateListener dieAnimationListener = new AnimationState.AnimationStateListener() {
                @Override
                public void complete(AnimationState.TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        superDie(); // 动画结束后再真正死亡
                        state38.removeListener(this);
                    }
                }

                @Override public void start(AnimationState.TrackEntry entry) {}
                @Override public void interrupt(AnimationState.TrackEntry entry) {}
                @Override public void end(AnimationState.TrackEntry entry) {}
                @Override public void dispose(AnimationState.TrackEntry entry) {}
                @Override public void event(AnimationState.TrackEntry entry, Event event) {}
            };

            state38.setAnimation(0, "Die", false);
            state38.addListener(dieAnimationListener);
        }else{
            superDie();
        }

    }

    private void superDie(){
        super.die();
        // 爱国者死后，向全场盾卫添加 “最后的行军 ”
    }
}
