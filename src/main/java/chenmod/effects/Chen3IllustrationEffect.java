package chenmod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class Chen3IllustrationEffect extends AbstractGameEffect {

    private float x;
    private float y;
    private float startX;
    private float targetX;
    private float targetY;

    // 时间线
    private final float flyDuration = 0.3f;   // 飞入
    private final float holdDuration = 0.5f;  // 停留
    private final float fadeDuration = 0.5f;  // 淡出

    private float timer = 0f;

    private Runnable afterEffect;

    private boolean isFirst = true;

    private static final Texture IMG;

    static {
        IMG = ImageMaster.loadImage("chenmod/images/character/chen3.png");
        IMG.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    // 缓存宽高（避免每帧 getWidth/getHeight）
    private static final float IMG_W = IMG.getWidth();
    private static final float IMG_H = IMG.getHeight();

    public Chen3IllustrationEffect(Runnable afterEffect) {

        this.afterEffect = afterEffect;
        // 起点：屏幕左侧之外
        startX = Settings.WIDTH / 2f - IMG_W / 2f;
        x = startX;
        targetX = Settings.WIDTH / 4f - IMG_W / 2f;

        targetY = Settings.HEIGHT / 2f - IMG_H / 2f;

        // 保留偏移
        y = targetY;

        this.color = Color.WHITE.cpy();
        this.color.a = 0f; // 起点完全透明
    }

    @Override
    public void update() {
        timer += Gdx.graphics.getDeltaTime();

        // 1. 快速飞入阶段
        if (timer <= flyDuration) {
            float t = timer / flyDuration;
            x = Interpolation.pow2Out.apply(startX, targetX, t);
            color.a = t; // 透明度逐渐增加
            return;
        }

        // 2. 停留阶段
        if (timer <= flyDuration + holdDuration) {
            x = targetX;
            color.a = 1f; // 保持完全显示
            return;
        }

        // 在进入缓速阶段时触发回调
        if (timer > flyDuration + holdDuration && isFirst) {
            isFirst = false;
            if (afterEffect != null) {
                afterEffect.run();
            }
        }

        // 3. 缓速右移 + 渐隐阶段
        float fadeT = (timer - (flyDuration + holdDuration)) / fadeDuration;
        if (fadeT <= 1f) {
            // 缓速右移：从 targetX 向左平移 250 像素
            float offset = Interpolation.pow2In.apply(0f, 250f, fadeT);
            x = targetX - offset;

            // 渐隐：透明度逐渐降低
            color.a = 1f - fadeT;

            return;
        }

        // 4. 结束
        isDone = true;
    }


    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(
                IMG,
                x, y,
                IMG_W / 2f, IMG_H / 2f,   // origin
                IMG_W, IMG_H,             // 🔥 按原图像素绘制
                1f, 1f,                            // 🔥 不缩放
                0f,
                0, 0,
                (int)IMG_W, (int)IMG_H,
                false, false
        );
    }

    @Override
    public void dispose() {}

    public void reset(Runnable afterEffect) {
        this.afterEffect = afterEffect;
        this.timer = 0f;
        this.color = Color.WHITE.cpy();
        this.x = startX;
        this.y = targetY;   // 保持居中
        this.isDone = false;
        this.isFirst = true;
    }

}
