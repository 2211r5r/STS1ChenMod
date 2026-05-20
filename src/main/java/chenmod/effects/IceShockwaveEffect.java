package chenmod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class IceShockwaveEffect extends AbstractGameEffect {

    private TextureRegion ringImg;     // 冲击波圆环
    private TextureRegion mistImg;     // 冰雾
    private TextureRegion shardImg;    // 冰晶碎片

    private float x, y;

    private boolean shaken = false;

    // 多层冲击波的 scale
    private float ringScale1, ringScale2, ringScale3;

    // 粒子数据
    private static class Shard {
        float x, y;
        float vX, vY;
        float scale;
        float rotation;
        float rotationSpeed;
    }

    private Array<Shard> shards = new Array<>();

    public IceShockwaveEffect(float x, float y) {
        this.x = x;
        this.y = y;

        ringImg = ImageMaster.EXHAUST_L;
        mistImg = ImageMaster.EXHAUST_L;

        // 魔法爆裂粒子（亮点）
        shardImg = ImageMaster.GLOW_SPARK;

        this.color = new Color(0.75f, 0.90f, 1f, 1f);

        this.duration = 1.0f;
        this.startingDuration = 1.0f;

        generateShards();
    }

    private void generateShards() {
        int count = MathUtils.random(256, 512);

        for (int i = 0; i < count; i++) {
            Shard s = new Shard();

            s.x = x;
            s.y = y;

            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(700f, 1400f);

            s.vX = MathUtils.cosDeg(angle) * speed;
            s.vY = MathUtils.sinDeg(angle) * speed;

            s.scale = MathUtils.random(0.6f, 1.6f);
            s.rotation = MathUtils.random(0f, 360f);
            s.rotationSpeed = MathUtils.random(-480f, 480f);

            shards.add(s);
        }
    }

    @Override
    public void update() {

        if (!shaken) {
            shaken = true;
            CardCrawlGame.screenShake.shake(
                    ScreenShake.ShakeIntensity.HIGH,
                    ScreenShake.ShakeDur.MED,
                    false
            );
        }


        float dt = Gdx.graphics.getDeltaTime();
        float t = 1f - (duration / startingDuration);

        // 多层冲击波 scale
        ringScale1 = MathUtils.lerp(0.9f, 16.5f, t);
        ringScale2 = MathUtils.lerp(1.8f, 11.0f, t * 0.9f);
        ringScale3 = MathUtils.lerp(0.5f, 7.0f, t * 0.8f);

        // 冰雾透明度
        if (t < 0.2f) {
            color.a = MathUtils.lerp(1f, 1.5f, t / 0.2f);
        } else {
            color.a = MathUtils.lerp(1.5f, 0f, (t - 0.2f) / 0.8f);
        }

        // 更新冰晶碎片
        for (Shard s : shards) {
            s.x += s.vX * dt;
            s.y += s.vY * dt;
            s.rotation += s.rotationSpeed * dt;
        }

        duration -= dt;
        if (duration <= 0f) {
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);

        float w = ringImg.getRegionWidth();
        float h = ringImg.getRegionHeight();

        // --- 第一层冲击波（最亮、最大） ---
        sb.draw(
                ringImg,
                x - w / 2f,
                y - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                ringScale1 * Settings.scale,
                ringScale1 * Settings.scale,
                0f
        );

        // --- 第二层冲击波（稍暗） ---
        sb.setColor(0.75f, 0.90f, 1f, color.a * 0.7f);
        sb.draw(
                ringImg,
                x - w / 2f,
                y - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                ringScale2 * Settings.scale,
                ringScale2 * Settings.scale,
                0f
        );

        // --- 第三层冲击波（最柔和） ---
        sb.setColor(0.75f, 0.90f, 1f, color.a * 0.4f);
        sb.draw(
                ringImg,
                x - w / 2f,
                y - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                ringScale3 * Settings.scale,
                ringScale3 * Settings.scale,
                0f
        );

        // --- 冰雾层 ---
        sb.setColor(0.75f, 0.90f, 1f, color.a * 0.6f);
        sb.draw(
                mistImg,
                x - w / 2f,
                y - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                ringScale1 * 0.5f * Settings.scale,
                ringScale1 * 0.5f * Settings.scale,
                0f
        );

        // --- 魔法爆裂碎片 ---
        sb.setColor(1f, 1f, 1f, color.a);
        for (Shard s : shards) {
            float sw = shardImg.getRegionWidth();
            float sh = shardImg.getRegionHeight();

            sb.draw(
                    shardImg,
                    s.x - sw / 2f,
                    s.y - sh / 2f,
                    sw / 2f,
                    sh / 2f,
                    sw,
                    sh,
                    s.scale * Settings.scale,
                    s.scale * Settings.scale,
                    s.rotation
            );
        }
    }

    @Override
    public void dispose() {}
}
