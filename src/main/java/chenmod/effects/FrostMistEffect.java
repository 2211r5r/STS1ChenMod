package chenmod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FrostMistEffect extends AbstractGameEffect {
    private TextureRegion img;
    private float x, y;

    private float effectScale;

    public FrostMistEffect(float x, float y, float effectScale) {
        this.x = x;
        this.y = y;
        this.effectScale = effectScale;

        img = ImageMaster.EXHAUST_L;

        this.color = new Color(0.75f, 0.90f, 1f, 1f);

        this.duration = 0.5f;
        this.startingDuration = 0.5f;
    }

    public FrostMistEffect(float x, float y) {
        this(x, y, 1f); // 默认大小
    }

    @Override
    public void update() {
        float t = 1f - (duration / startingDuration);

        // --- 冰暴 scale 曲线 ---
        // 0.0 → 0.15：快速爆开
        // 0.15 → 0.4：稍微回缩（冲击波感）
        // 0.4 → 1.0：再次扩散
        if (t < 0.15f) {
            scale = MathUtils.lerp(0.6f, 2.4f, t / 0.15f);
        } else if (t < 0.4f) {
            scale = MathUtils.lerp(2.4f, 1.6f, (t - 0.15f) / 0.25f);
        } else {
            scale = MathUtils.lerp(1.6f, 3.0f, (t - 0.4f) / 0.6f);
        }

        // --- 冰暴亮度脉冲 ---
        // 前 20%：亮度上升（冰晶闪光）
        if (t < 0.2f) {
            color.a = MathUtils.lerp(1f, 1.3f, t / 0.2f);
        } else {
            // 后面逐渐消失
            color.a = MathUtils.lerp(1.3f, 0f, (t - 0.2f) / 0.8f);
        }

        duration -= Gdx.graphics.getDeltaTime();
        if (duration <= 0f) {
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);

        float w = img.getRegionWidth();
        float h = img.getRegionHeight();

        sb.draw(
                img,
                x - w / 2f,
                y - h / 2f,
                w / 2f,
                h / 2f,
                w,
                h,
                scale * Settings.scale * effectScale,
                scale * Settings.scale * effectScale,
                0f
        );
    }

    @Override
    public void dispose() {}
}
