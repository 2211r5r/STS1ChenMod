package chenmod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class GoldenInvincibleAuraEffect extends AbstractGameEffect {

    private TextureRegion ringImg;

    private float centerX, centerY;

    private float rotation = 0f;       // 光圈旋转角度
    private float pulse = 0f;          // 光晕脉冲
    private boolean ending = false;    // 是否正在结束

    public GoldenInvincibleAuraEffect(float x, float y) {
        this.centerX = x;
        this.centerY = y;

        ringImg = ImageMaster.WHITE_RING;

        // 金色光圈
        this.color = new Color(1f, 0.92f, 0.55f, 1f);

        // 持续型特效，不自动结束
        this.duration = Float.MAX_VALUE;
    }

    // 外部调用：结束特效
    public void end() {
        ending = true;
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();

        // 旋转
        rotation += dt * 40f; // 可调

        // 光晕脉冲（呼吸感）
        pulse += dt * 2f;
        float pulseValue = 0.9f + (float)Math.sin(pulse) * 0.1f;

        color.a = pulseValue;

        // 结束时淡出
        if (ending) {
            duration = 0.5f;
            duration -= dt;
            if (duration <= 0f) {
                isDone = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);

        float w = ringImg.getRegionWidth();
        float h = ringImg.getRegionHeight();

        float scale = 3.0f * Settings.scale; // 光圈大小，可调

        sb.draw(
                ringImg,
                centerX - w / 2f,
                centerY - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                scale,
                scale,
                rotation
        );
    }

    @Override
    public void dispose() {}
}
