package chenmod.actions;

import chenmod.ChenMod;
import chenmod.monsters.ShieldGuard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShieldGuardMarchAction extends AbstractGameAction {

    private final ShieldGuard monster;
    private final float startX, startY;
    private float targetX;
    private final float targetY;
    private final int marchDamage;
    private final int marchBlock;

    private float moveDuration = 1.0f;
    private float moveTimer = 0.0f;

    private static final float MIN_DISTANCE_TO_PLAYER = 150.0f;
    private static final float MIN_DISTANCE_TO_MONSTER = 100.0f;

    private boolean movementSkipped = false;

    public ShieldGuardMarchAction(ShieldGuard monster, float offsetX, float offsetY, int marchDamage, int marchBlock) {
        this.monster = monster;

        this.startX = monster.drawX;
        this.startY = monster.drawY;

        this.targetX = startX + offsetX;
        this.targetY = startY + offsetY;

        this.marchDamage = marchDamage;
        this.marchBlock = marchBlock;

        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {

        // 盾卫与玩家的最小距离
        float minDist = MIN_DISTANCE_TO_PLAYER;

        // 当前距离
        float currentDist = Math.abs(monster.hb.cX - AbstractDungeon.player.hb.cX);

        // 如果移动后会小于最小距离 → 修正 targetX
        float predictedDist = Math.abs(targetX - AbstractDungeon.player.hb.cX);

        if (predictedDist < minDist) {
            // 计算新的 targetX，使得距离刚好等于 minDist
            if (monster.drawX > AbstractDungeon.player.hb.cX) {
                // 盾卫在玩家右侧（正常情况）
                targetX = AbstractDungeon.player.hb.cX + minDist;
            } else {
                // 盾卫在玩家左侧（极少见，但逻辑完整）
                targetX = AbstractDungeon.player.hb.cX - minDist;
            }

            ChenMod.logger.info("【盾卫行军】目标坐标过近 → 修正 targetX = " + targetX);
        }

        AbstractMonster front = findFrontMonster(monster);
        if (front != null) {
            float distToFront = Math.abs(monster.hb.cX - front.hb.cX);
            float predictedDistFront = Math.abs(targetX - front.hb.cX);

            if (predictedDistFront < MIN_DISTANCE_TO_MONSTER) {
                // 修正 targetX，使得距离刚好等于最小距离
                if (monster.drawX > front.drawX) {
                    targetX = front.drawX + MIN_DISTANCE_TO_MONSTER;
                } else {
                    targetX = front.drawX - MIN_DISTANCE_TO_MONSTER;
                }

                ChenMod.logger.info("【盾卫行军】前方小怪过近 → 修正 targetX = " + targetX);
            }
        }

        // ③ 若跳过移动 → 直接攻击+防御
        if (movementSkipped) {
            playAttackAnimation();
            doAttackAndBlock();
            this.isDone = true;
            return;
        }

        // ④ 播放行军动画（不需要监听器）
        if (moveTimer == 0f && monster.state38 != null) {
            monster.state38.setAnimation(0, "Move_Begin", false);
            monster.state38.addAnimation(0, "Move_Loop", true, 0f);
        }

        // ⑤ 行军移动（插值）
        moveTimer += Gdx.graphics.getDeltaTime();
        float progress = Math.min(moveTimer / moveDuration, 1.0f);

        monster.drawX = MathUtils.lerp(startX, targetX, progress);
        monster.drawY = MathUtils.lerp(startY, targetY, progress);

        // ⑥ 到达目标 → 结束行军动画，进入攻击
        if (progress >= 1.0f) {
            playAttackAnimation();
            doAttackAndBlock();
            this.isDone = true;
        }
    }

    /** 播放攻击动画（强制打断行军动画） */
    private void playAttackAnimation() {
        if (monster.state38 != null) {
            monster.state38.setAnimation(0, "Attack", false);
            monster.state38.addAnimation(0, "Idle", true, 0f);
        }
    }

    /** 执行攻击 + 防御 */
    private void doAttackAndBlock() {
        if(marchBlock>0){
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(monster, monster, marchBlock));
        }

        if (this.marchDamage > 0){
            AbstractDungeon.actionManager.addToBottom(new DamageAction(
                    AbstractDungeon.player,
                    new DamageInfo(monster, marchDamage),
                    AttackEffect.BLUNT_LIGHT
            ));
        }
    }

    /** 找到站在 monster 前面的最近小怪 */
    private AbstractMonster findFrontMonster(AbstractMonster self) {
        AbstractMonster result = null;
        float minDist = Float.MAX_VALUE;

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m == self || m.isDeadOrEscaped()) continue;

            if (m.drawX < self.drawX) {
                float dist = Math.abs(self.hb.cX - m.hb.cX);
                if (dist < minDist) {
                    minDist = dist;
                    result = m;
                }
            }
        }
        return result;
    }
}

