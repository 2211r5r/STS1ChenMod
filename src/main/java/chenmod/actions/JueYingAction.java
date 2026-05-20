package chenmod.actions;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.effects.JueYingEffect;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class JueYingAction extends AbstractGameAction {

    private static final float HIT_INTERVAL = 0.2F;

    private static final float FIRST_WAIT_INTERVAL = 0.8F;


    // 核心状态机
    private enum HitState {
        IDLE,
        WAITING,
        READY_TO_HIT,
        EXECUTING_HIT,
        CHECK_TARGET,
        FIND_NEW_TARGET,
        RESET_HIT,
        CHECK_HIT_COUNT,
        FINISHING,
        FINISHED
    }

    // 核心变量
    private final DamageInfo info;
    private final boolean isUpgraded;
    private int currentHitCount;
    private HitState hitState;
    private float elapsedTime;
    private AbstractMonster tempNewTarget; // 临时存储新目标（解耦状态）

    private boolean isFirstWaiting; // 标记是否是首次进入WAITING状态

    private boolean hasPlayedAnimation =false;

    private final boolean killedMonsterCanContinue;

    private boolean hasKilledMonster;

    private final int totalHitTimes;

    public JueYingAction(AbstractCreature target, DamageInfo info, int totalHitTimes , boolean killedMonsterCanContinue, boolean isUpgraded) {
        this.target = target;
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.isUpgraded = isUpgraded;

        // 初始化状态
        this.currentHitCount = 0;
        this.hitState = HitState.IDLE; // 初始为闲置状态
        this.elapsedTime = 0.0F;
        this.tempNewTarget = null;
        this.duration = AbstractGameAction.DEFAULT_DURATION;

        this.totalHitTimes = totalHitTimes;

        this.killedMonsterCanContinue = killedMonsterCanContinue;

        this.hasKilledMonster = false;

        this.isFirstWaiting = true;
    }

    public JueYingAction(AbstractCreature target, DamageInfo info ,boolean isUpgraded) {
        this.target = target;
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.isUpgraded = isUpgraded;

        // 初始化状态
        this.currentHitCount = 0;
        this.hitState = HitState.IDLE; // 初始为闲置状态
        this.elapsedTime = 0.0F;
        this.tempNewTarget = null;
        this.duration = AbstractGameAction.DEFAULT_DURATION;

        this.totalHitTimes = 10;

        this.killedMonsterCanContinue = true;

        this.hasKilledMonster = false;

        this.isFirstWaiting = true;
    }

    @Override
    public void update() {

        switch (hitState) {
            case IDLE:
                ChenMod.logger.info("已经进入绝影Action的IDLE状态，卡牌Action准备开始");

                CardCrawlGame.sound.play(Sounds.jueYingEffect_1);

                // 闲置状态仅执行一次，直接切换到等待
//                ChenMod.logger.info("准备切换到WAITING");
                hitState = HitState.WAITING;
                break;

            case WAITING:
                if (!hasPlayedAnimation && player != null) {
                    // 播放绝影启动动画（非循环，无后续动画）

                    // 第一步：判断 p 是不是 ChenCharacter 类型
                    if (player instanceof ChenCharacter) {
                        // 第二步：安全强转（100%不会报错）
                        ChenCharacter p = (ChenCharacter) player;
                        p.useSkill3BeginAnimation();
                    }

                    this.hasPlayedAnimation = true;
//                    ChenMod.logger.info("绝影动画，启动！");
                }
                // 累计等待时间
                elapsedTime += Gdx.graphics.getDeltaTime();

                // ========== 核心修改：区分首次/非首次等待时间 ==========
                float currentInterval = isFirstWaiting ? FIRST_WAIT_INTERVAL : HIT_INTERVAL;
                // 间隔时间到，切换到准备斩击
                if (elapsedTime >= currentInterval) {
                    elapsedTime = 0.0F;
                    isFirstWaiting = false;
//                    ChenMod.logger.info("间隔时间到！！！！！！！！！！！准备进入READY_TO_HIT");
                    hitState = HitState.READY_TO_HIT;
                }
                break;

            case READY_TO_HIT:
//                ChenMod.logger.info("已经进入READY_TO_HIT");

                // 准备状态仅执行一次，切换到执行斩击
//                ChenMod.logger.info("卡牌Action执行前摇结束，准备进入EXE_HIT");
                hitState = HitState.EXECUTING_HIT;
                break;

            case EXECUTING_HIT:

                // 1. 动态刀光轨迹（划过目标）
                AbstractDungeon.effectList.add(new JueYingEffect(
                        this.target.hb.cX,  // 目标中心X坐标
                        this.target.hb.cY   // 目标中心Y坐标
                ));

                CardCrawlGame.sound.play(Sounds.jueYingEffect_2);

                // 2. 刀光命中冲击特效（目标中心爆闪）
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.combat.AdditiveSlashImpactEffect(
                        this.target.hb.cX,                // 冲击中心X（目标中心）
                        this.target.hb.cY,                // 冲击中心Y（目标中心）
                        Color.RED.cpy().mul(1.2f, 1.0f, 1.0f, 1.0f) // 冲击颜色（加深红色）
                ));

                this.target.damage(this.info);
                currentHitCount++;
//                ChenMod.logger.info("绝影第" + currentHitCount + "次斩击，目标：" + ((AbstractMonster) this.target).name + "，造成伤害：" + this.info.output);

                // 斩击执行完毕，切换到检查目标
//                ChenMod.logger.info("本次斩击完毕，准备进入CHECK_TARGET");
                hitState = HitState.CHECK_TARGET;
                break;

            case CHECK_TARGET:
//                ChenMod.logger.info("已切换到CHECK_TARGET");
                // 检查目标是否死亡

                // 如果这个敌人正在死亡结算，或者这个敌人当前生命值已经小于等于0，或者这个敌人进入了假死状态（小黑的复活中）
                boolean targetKilled = this.target.isDying ||
                        this.target.currentHealth <= 0 ||this.target.halfDead;

//                ChenMod.logger.info("我们的["+(AbstractMonster) this.target+"]现在"+ (targetKilled? "已经死亡(进入假死)!" : "还没死呢."));

                // 状态切换
                if (targetKilled) {
                    // 播放击杀音效
                    CardCrawlGame.sound.play(Sounds.jueYingEffect_3);

                    this.hasKilledMonster = true;

                    if(this.killedMonsterCanContinue){
                        // 如果目标被杀 且 斩击次数没有用完 且 击杀后可以寻找新目标 ，则寻找新目标
                        hitState = HitState.FIND_NEW_TARGET;
                    }else{
                        hitState = HitState.FINISHING;
                    }

                } else {
                    // 其他情况下, 去检查已经斩击的次数,看看动作有没有结束
//                    ChenMod.logger.info("目标仍然存活，即将切换到CHECK_HIT_COUNT");
                    hitState = HitState.CHECK_HIT_COUNT;
                }
                break;

            case FIND_NEW_TARGET:

                // 寻找新目标（仅执行一次）
                tempNewTarget = findNewTarget((AbstractMonster) this.target);

                // 状态切换
                if (tempNewTarget != null) {
                    if (isUpgraded) {
                        hitState = HitState.RESET_HIT; // 升级后重置次数
                    } else {
                        hitState = HitState.CHECK_HIT_COUNT; // 未升级，检查次数
                    }
                } else {
                    // 没有新目标，直接结束

                    hitState = HitState.FINISHING;
                }
                break;

            case RESET_HIT:

                currentHitCount = 0;
                this.target = tempNewTarget;
                tempNewTarget = null;

                hitState = HitState.WAITING;
                break;

            case CHECK_HIT_COUNT:

                // 检查是否还有剩余斩击次数（仅执行一次）
                if (currentHitCount < totalHitTimes) {
//                    ChenMod.logger.info("有剩余次数！！！");
                    // 有剩余次数：如果找到新目标则切换，否则继续打原目标
                    if (tempNewTarget != null) {

                        this.target = tempNewTarget;
//                        ChenMod.logger.info("已经选择了新的目标："+ ((AbstractMonster) this.target).name);
                        tempNewTarget = null;
                    }
                    // 关键修复：如果当前目标已经死亡或逃跑，且没有新目标，直接结束
                    if (this.target == null || ((AbstractMonster) this.target).isDeadOrEscaped()) {
//                        ChenMod.logger.info("即将切换到到FINISHING，如果当前目标已经死亡或逃跑，且没有新目标，直接结束");
                        hitState = HitState.FINISHING;
                    } else {
//                        ChenMod.logger.info("即将切换到到WAITING");
                        hitState = HitState.WAITING;
                    }
                } else {
                    // 次数用完，收尾
//                    ChenMod.logger.info("即将切换到到FINISHING，没有剩余次数");
                    hitState = HitState.FINISHING;
                }
                break;

            case FINISHING:
                // 收尾阶段
//                ChenMod.logger.info("已经进入FINISHING");

                if(this.hasKilledMonster){
                    CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.jueYingVoicePool));
//                    ChenMod.logger.info("是谁给你们的自信前来挑战我??");
                }

                // 第一步：判断 p 是不是 ChenCharacter 类型
                if (player instanceof ChenCharacter) {
                    // 第二步：安全强转（100%不会报错）
                    ChenCharacter p = (ChenCharacter) player;
                    p.useSkill3EndAnimation();
                }

//                ChenMod.logger.info("准备切换到FINISHED");
                hitState = HitState.FINISHED;
                break;

            case FINISHED:
                // 最终状态：标记Action完成
                this.isDone = true;
                ChenMod.logger.info("本次Action完美结束！！！！！");
                break;
        }
    }

    // 寻找新目标（复用原有逻辑）
    private AbstractMonster findNewTarget(AbstractMonster excludedMonster) {

        if (AbstractDungeon.getCurrRoom() == null
                || AbstractDungeon.getCurrRoom().monsters == null
                || AbstractDungeon.getCurrRoom().monsters.monsters == null) {
            return null;
        }

        List<AbstractMonster> validMonsters = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            // 如果m不是刚刚选中的目标，且m没有死亡或逃跑，且m不处于假死状态（小黑复活中）
            if (m != excludedMonster && !m.isDeadOrEscaped() && !m.halfDead && !m.isDying) {
                validMonsters.add(m);
            }
        }
        if (validMonsters.isEmpty()) {
            return null;
        }

        return validMonsters.get(AbstractDungeon.cardRandomRng.random(validMonsters.size() - 1));
    }
}