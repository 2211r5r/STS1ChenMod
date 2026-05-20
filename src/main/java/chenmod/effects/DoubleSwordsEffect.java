package chenmod.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class DoubleSwordsEffect extends AbstractGameEffect {

    private static final float LENGTH_MULTIPLIER = 60.0f; // 刀光长度系数（越大越长）

    private static final float WIDTH_MULTIPLIER = 0.05f; // 刀光宽度系数（控制粗细）
    private static final float BASE_TARGET_SCALE = 2.0f; // 刀光基础缩方
    private static final float ANGLE_RANGE = 10.0f;      // 角度随机范围
    private static final Color MAIN_COLOR = Color.BLACK;
    private static final Color SECOND_COLOR = Color.DARK_GRAY;

    // 自己维护所有字段，彻底避免访问父类私有成员
    private float x;
    private float y;
    private float sX;
    private float sY;
    private float tX;
    private float tY;
    private float scaleX;
    private float scaleY;
    private float targetScale;
    private Color color2;
    private final float lengthMultiplier;

    private final float widthMultiplier;

    // 构造方法：极简调用（仅传目标坐标）
    public DoubleSwordsEffect(float targetCX, float targetCY) {
        this(targetCX, targetCY, LENGTH_MULTIPLIER, WIDTH_MULTIPLIER);
    }

    // 构造方法：自定义长度系数（备用）
    public DoubleSwordsEffect(float targetCX, float targetCY, float lengthMultiplier, float widthMultiplier) {
        float dX = 200.0f * Settings.scale;
        float dY = 0.0f;
        float angle = MathUtils.random(-ANGLE_RANGE, ANGLE_RANGE);

        this.x = targetCX - 64.0F - dX / 2.0F * Settings.scale;
        this.y = targetCY - 64.0F - dY / 2.0F * Settings.scale;
        this.sX = this.x;
        this.sY = this.y;
        this.tX = this.x + dX / 2.0F * Settings.scale;
        this.tY = this.y + dY / 2.0F * Settings.scale;
        this.color = MAIN_COLOR.cpy();
        this.color2 = SECOND_COLOR.cpy();
        this.color.a = 0.0F;
        this.startingDuration = 0.2F; // 缩短动画时长，适配快速连击
        this.duration = this.startingDuration;
        this.targetScale = BASE_TARGET_SCALE;
        this.scaleX = 0.01F;
        this.scaleY = 0.01F;
        this.rotation = angle;
        this.lengthMultiplier = lengthMultiplier;
        this.widthMultiplier = widthMultiplier;
    }

    // 重写update方法：自己维护所有动画逻辑
    @Override
    public void update() {
        if (this.duration > this.startingDuration / 2.0F) {
            this.color.a = Interpolation.exp10In.apply(0.8F, 0.0F, (this.duration - this.startingDuration / 2.0F) / (this.startingDuration / 2.0F));
            this.scaleX = Interpolation.exp10In.apply(this.targetScale, 0.1F, (this.duration - this.startingDuration / 2.0F) / (this.startingDuration / 2.0F));
            this.scaleY = this.scaleX;
            this.x = Interpolation.fade.apply(this.tX, this.sX, (this.duration - this.startingDuration / 2.0F) / (this.startingDuration / 2.0F));
            this.y = Interpolation.fade.apply(this.tY, this.sY, (this.duration - this.startingDuration / 2.0F) / (this.startingDuration / 2.0F));
        } else {
            this.scaleX = Interpolation.pow2In.apply(0.5F, this.targetScale, this.duration / (this.startingDuration / 2.0F));
            this.color.a = Interpolation.pow5In.apply(0.0F, 0.8F, this.duration / (this.startingDuration / 2.0F));
            this.scaleY = this.scaleX;
        }

        this.duration -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    // 重写render方法：自己维护所有渲染逻辑
    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color2);
        sb.setBlendFunction(770, 1);

        // 核心：使用自己维护的scaleX/scaleY，乘以绝影专属长度系数
        sb.draw(ImageMaster.ANIMATED_SLASH_VFX, this.x, this.y, 64.0F, 64.0F, 128.0F, 128.0F,
                this.scaleX * 0.4F * MathUtils.random(0.95F, 1.05F) * Settings.scale * this.lengthMultiplier,
                this.scaleY * 0.7F * MathUtils.random(0.95F, 1.05F) * Settings.scale * this.widthMultiplier,
                this.rotation, 0, 0, 128, 128, false, false);

        sb.setColor(this.color);
        sb.draw(ImageMaster.ANIMATED_SLASH_VFX, this.x, this.y, 64.0F, 64.0F, 128.0F, 128.0F,
                this.scaleX * 0.7F * MathUtils.random(0.95F, 1.05F) * Settings.scale * this.lengthMultiplier,
                this.scaleY * MathUtils.random(0.95F, 1.05F) * Settings.scale * this.widthMultiplier,
                this.rotation, 0, 0, 128, 128, false, false);

        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
        // 无需释放资源
    }
}