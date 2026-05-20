package chenmod.powers;

import chenmod.ChenMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

import java.util.ArrayList;


public class StunPower extends BasePower{
    // ========== 静态常量（本地化/纹理） ==========
    public static final String POWER_ID = ChenMod.makeID(StunPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;

    private static final AbstractPower.PowerType TYPE = PowerType.DEBUFF;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final boolean TURN_BASED = true; //是回合制效果（回合结束后移除）

    // ========== 备份怪物原始状态的字段 ==========
    private byte originalNextMove;          // 原始行动ID
    private AbstractMonster.Intent originalIntent; // 原始意图
    private int originalBaseDmg;            // 原始基础伤害
    private ArrayList<Byte> originalMoveHistory; // 原始行动历史
    private boolean originalCannotEscape;   // 原始是否无法逃脱
    private String originalMoveName;        // 原始行动名称
    private int originalIntentDmg;
    private boolean isMultiAttack;
    private int originalAttackMultiplier;

    // ========== 构造方法 ==========
    public StunPower(AbstractCreature owner) {
        // 调用BasePower构造：ID、类型（DEBUFF）、非回合制、所有者、来源、层数（1层）
        super(
                POWER_ID,
                TYPE,
                TURN_BASED,
                owner,
                AbstractDungeon.player,
                1,
                true,
                true
        );
        this.amount = 1; // 固定1层，仅眩晕1回合
        this.updateDescription();
    }

    // ========== 核心方法：更新描述 ==========
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }


    // ========== 关键时机1：施加Power时备份原始状态 ==========
    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        if (owner instanceof AbstractMonster && !owner.isDeadOrEscaped()) {
            AbstractMonster monster = (AbstractMonster) owner;

            // 1. 备份基础字段
            this.originalNextMove = monster.nextMove;
            this.originalIntent = monster.intent;
            this.originalBaseDmg = monster.getIntentBaseDmg();
            this.originalIntentDmg = monster.getIntentDmg(); // 公开方法：获取最终显示的伤害
            this.originalMoveHistory = new ArrayList<>(monster.moveHistory);
            this.originalCannotEscape = monster.cannotEscape;
            this.originalMoveName = monster.moveName;

            // 2. 核心：反向推断多段攻击属性（仅攻击意图）
            this.isMultiAttack = false;
            this.originalAttackMultiplier = 0;
            if (monster.intent.name().contains("ATTACK")) {
                // 从AbstractMonster源码可知：intentMultiAmt>0 且 isMultiDmg=true 才是多段
                // 虽然intentMultiAmt是私有，但我们可以通过反射读取（无侵入，仅读取）
                try {
                    // 反射读取私有字段：intentMultiAmt（段数）、isMultiDmg（是否多段）
                    java.lang.reflect.Field intentMultiAmtField = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                    intentMultiAmtField.setAccessible(true);
                    this.originalAttackMultiplier = (int) intentMultiAmtField.get(monster);

                    java.lang.reflect.Field isMultiDmgField = AbstractMonster.class.getDeclaredField("isMultiDmg");
                    isMultiDmgField.setAccessible(true);
                    this.isMultiAttack = (boolean) isMultiDmgField.get(monster);

                    ChenMod.logger.info("怪物[" + monster.name + "]多段攻击推断结果：" + isMultiAttack + "，段数：" + originalAttackMultiplier);
                } catch (Exception e) {
                    // 反射失败时的降级方案：通过伤害逻辑推断
                    this.isMultiAttack = monster.getIntentDmg() > 0 && monster.getIntentBaseDmg() > 0
                            && monster.getIntentDmg() % monster.getIntentBaseDmg() == 0
                            && monster.getIntentDmg() / monster.getIntentBaseDmg() > 1;
                    if (this.isMultiAttack) {
                        this.originalAttackMultiplier = monster.getIntentDmg() / monster.getIntentBaseDmg();
                    }
                    ChenMod.logger.warn("反射读取失败，降级推断多段攻击：" + isMultiAttack + "，段数：" + originalAttackMultiplier);
                }

            }

            // 3. 临时设置STUN意图
            monster.setMove(originalNextMove, AbstractMonster.Intent.STUN);
            monster.createIntent();
        }
    }

    // ========== 关键时机2：怪物回合开始时触发眩晕 ==========
    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();
        if (owner instanceof AbstractMonster && !owner.isDeadOrEscaped()) {
            AbstractMonster monster = (AbstractMonster) owner;

            // 2. 强制跳过行动：设为ESCAPE（99），解除无法逃脱限制
            monster.cannotEscape = false;
            monster.nextMove = 99; // 对应AbstractMonster.ESCAPE常量

            this.flashWithoutSound();

            ChenMod.logger.info("怪物[" + monster.name + "]回合开始，触发眩晕，跳过行动");
        }
    }

    // ========== 关键时机3：怪物回合结束时恢复状态+移除Power ==========
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        if (!isPlayer && owner instanceof AbstractMonster && !owner.isDeadOrEscaped()) {
            AbstractMonster monster = (AbstractMonster) owner;

            // 1. 恢复基础状态（原有）
            monster.moveHistory.clear();
            monster.moveHistory.addAll(originalMoveHistory);
            monster.nextMove = originalNextMove;
            monster.cannotEscape = originalCannotEscape;

            // 2. 核心修复：使用完整的setMove重载，恢复多段攻击信息
            // 这样会自动重建monster.move对象，createIntent()会重新计算显示
            if (originalIntent.name().contains("ATTACK")) {
                // 对于攻击意图，我们需要区分是单次还是多段攻击
                // 这里我们假设你已经知道原始攻击是单次还是多段，并在备份时记录了originalIsMultiDamage和originalMultiplier
                // 如果原始是多段攻击
                if (this.isMultiAttack && this.originalAttackMultiplier > 0) {
                    // 使用5参数的setMove重载：名称、行动ID、意图、基础伤害、段数
                    // 这个重载内部会创建一个isMultiDamage=true的EnemyMoveInfo
                    monster.setMove(
                            this.originalMoveName,
                            this.originalNextMove,
                            this.originalIntent,
                            this.originalBaseDmg,
                            this.originalAttackMultiplier,
                            true
                    );
                } else {
                    // 原始是单次攻击，使用4参数的setMove重载
                    monster.setMove(
                            this.originalMoveName,
                            this.originalNextMove,
                            this.originalIntent,
                            this.originalBaseDmg
                    );
                }
            } else {
                // 非攻击意图，使用3参数的setMove重载
                monster.setMove(this.originalMoveName, this.originalNextMove, this.originalIntent);
            }

            // 3. 调用createIntent()，让游戏根据新的monster.move对象重新计算显示
            monster.createIntent();

            AbstractDungeon.effectList.add(
                    new TextAboveCreatureEffect(
                            monster.hb.cX,
                            monster.hb.cY + 50.0F,
                            DESCRIPTIONS[1],
                            Color.GREEN

                    )
            );

            // 5. 移除眩晕Power
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));

            ChenMod.logger.info("怪物[" + monster.name + "]回合结束，完整恢复原始状态（含多段攻击），移除眩晕Power");
        }
    }

}
