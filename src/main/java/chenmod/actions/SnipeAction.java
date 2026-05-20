package chenmod.actions;

import chenmod.ChenMod;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.*;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class SnipeAction extends AbstractGameAction {

    private static final float BASE_TIMES = 1.0f; // 基础伤害倍率

    private static final float TIMES_LIMIT = 1.50f;    // 伤害倍率限制

    private float damageTimes = 1.0f;

    private final DamageInfo orangeInfo;  // 传入的伤害信息

    private DamageInfo newInfo;  // 传入的伤害信息
    private final boolean isUpgraded;   // 是否升级? 升级后的倍率限制取消
    private SnipeAction.HitState hitState;

    private enum HitState {
        IDLE,
        WAITING,
        READY_TO_HIT,
        EXECUTING_HIT,
        FINISHING,
        FINISHED
    }

    public SnipeAction(AbstractCreature target, DamageInfo info, boolean isUpgraded) {
        this.target = target;
        this.orangeInfo = info;
        this.newInfo = null;
        this.actionType = ActionType.DAMAGE;
        this.isUpgraded = isUpgraded;

        // 初始化状态
        this.hitState = SnipeAction.HitState.IDLE; // 初始为闲置状态
        this.duration = AbstractGameAction.DEFAULT_DURATION;

        this.isDone=false;

    }


    @Override
    public void update() {
        switch(hitState){
            case IDLE:
                ChenMod.logger.info("已经进入IDLE状态，卡牌Action准备开始");

                CardCrawlGame.sound.play(Sounds.jueYingEffect_1);

                // 闲置状态仅执行一次，直接切换到等待
                ChenMod.logger.info("准备切换到WAITING");
                hitState = HitState.WAITING;

                break;

            case WAITING:

                ChenMod.logger.info("已经进入WATTING状态, 准备进入READY_TO_HIT");

                hitState = HitState.READY_TO_HIT;
                break;

            case READY_TO_HIT:
                // 计算两者坐标之间的距离， 增加伤害倍率
                ChenMod.logger.info("已经进入READY_TO_HIT状态");

                float tempTimes = calculateDamageTimes((AbstractMonster) this.target);

                this.damageTimes = isUpgraded? tempTimes : Math.min(tempTimes, TIMES_LIMIT);

                ChenMod.logger.info("准备进入EXE_HIT");
                hitState = HitState.EXECUTING_HIT;
                break;

            case EXECUTING_HIT:
                // new 一个新的伤害 Info,让目标受到伤害.
                ChenMod.logger.info("已经进入EXE_HIT状态, 准备打击");

                newInfo = new DamageInfo(
                        player,
                        (int)(orangeInfo.base * this.damageTimes),
                        orangeInfo.type
                );
                ChenMod.logger.info("newDamageInfo's base damage is :"+newInfo.base);

                this.target.damage(newInfo);

                ChenMod.logger.info("伤害打击结束, 准备进入FINISHING");
                hitState = HitState.FINISHING;
                break;

            case FINISHING:
                ChenMod.logger.info("已经进入FINISHING状态, 准备进入FINISHED");

                hitState = HitState.FINISHED;

                break;

            case FINISHED:
                this.isDone = true;
                ChenMod.logger.info("本次Action完美结束！！！！！");
                break;

        }
    }

    public float getDamageTimes() {
        return damageTimes;
    }

    public void setDamageTimes(float damageTimes) {
        this.damageTimes = damageTimes;
    }

    public float calculateDamageTimes(AbstractMonster targetMonster){

        Map<String, Double> distances = new HashMap<>();

        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
            double distance = Math.sqrt(Math.pow(monster.hb.cX - player.hb.cX, 2) + Math.pow(monster.hb.cY - player.hb.cY, 2));

            ChenMod.logger.info(monster.id+"的距离为"+distance);

            distances.put(monster.id, distance);

        }

        ChenMod.logger.info("距离集合"+distances);

        double minDistance = Double.MAX_VALUE;

        for (Double distance: distances.values()){

            if (distance==null)
                continue;

            if(distance < minDistance){
                minDistance = distance;
            }
        }
        ChenMod.logger.info("本房间内的最低距离基数为"+minDistance);

        double targetDistance = distances.getOrDefault(targetMonster.id, minDistance);

        ChenMod.logger.info("针对["+targetMonster.id+"]的距离为"+targetDistance);

        double damageTimes = targetDistance / minDistance;

        ChenMod.logger.info("针对["+targetMonster.id+"]的伤害倍率为"+damageTimes);

        return (float) damageTimes;
    }
}
