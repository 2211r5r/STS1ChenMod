package chenmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import chenmod.ChenMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 简化版多发之力：仅让下一张攻击牌打出指定次数，打出后立即移除
public class MultiHitPower extends BasePower {
    // Power唯一ID
    public static final String POWER_ID = ChenMod.makeID(MultiHitPower.class.getSimpleName());
    // 本地化文本
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    private static final boolean TURN_BASED = false;    // 不为回合制效果，回合结束后不移除

    public MultiHitPower(AbstractCreature owner, final int amount) {

        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
        // 仅对“非一次性攻击牌”生效（对齐官方逻辑）
        if (!card.purgeOnUse && card.type == AbstractCard.CardType.ATTACK) {
            this.flash(); // Power图标闪烁（视觉反馈）

            // 1. 获取原牌的目标怪物（保证复制牌命中正确目标）
            AbstractMonster targetMonster = null;
            if (action.target instanceof AbstractMonster) {
                targetMonster = (AbstractMonster) action.target;
            }

            for (int i = 1; i < this.amount; i++) {

                AbstractCard copyCard = card.makeSameInstanceOf(); // 复制原牌（保留原牌所有属性）
                AbstractDungeon.player.limbo.addToBottom(copyCard);

                // 对齐官方的卡牌位置设置（避免复制牌位置错乱）
                copyCard.current_x = card.current_x;
                copyCard.current_y = card.current_y;
                copyCard.target_x = Settings.WIDTH / 2.0f - 300.0f * Settings.scale;
                copyCard.target_y = Settings.HEIGHT / 2.0f;
                copyCard.targetAngle = 0.0F; // 补充角度，避免卡牌旋转错乱
                copyCard.lighten(false); // 保持卡牌亮度一致
                copyCard.drawScale = 0.12F; // 初始缩放（和官方一致）
                copyCard.targetDrawScale = 0.75F; // 目标缩放

                // 计算复制牌对目标的伤害（保证伤害和原牌一致）
                if (targetMonster != null) {
                    copyCard.calculateCardDamage(targetMonster);
                }

                copyCard.purgeOnUse = true; // 复制牌使用后立即移除（避免残留）
                // 关键：用CardQueueItem封装参数，解决此前的语法报错
                AbstractDungeon.actionManager.addCardQueueItem(
                        new CardQueueItem(copyCard, targetMonster, card.energyOnUse, true, true),
                        true
                );
            }

            // 3. 核心：打出这张牌后，立即移除Power（无论次数多少）
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

}