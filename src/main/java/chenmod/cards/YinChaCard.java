package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class YinChaCard extends BaseCard{
    public static final String ID = makeID(YinChaCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.COMMON, // 稀有度
            CardTarget.SELF, // 目标
            -1 // 基础费用
    );

    // 核心数值：每1点能量回复3点生命，升级后+1
    private static final int HEAL_PER_ENERGY = 3;
    private static final int UPG_HEAL_PER_ENERGY = 1;

    public YinChaCard() {
        super(ID, info); // 调用父类构造方法

        // 魔法数值：存储每费回复量（用于本地化占位符!M!）
        setMagic(HEAL_PER_ENERGY, UPG_HEAL_PER_ENERGY);

        this.exhaust = true;

        tags.add(CardTags.HEALING);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        // 仿照WhirlwindAction，直接在use中内嵌核心逻辑（不单独建Action类）
        this.addToBot(new AbstractGameAction() {
            // 声明需要的参数（和WhirlwindAction一致）
            private final int healPerEnergy = YinChaCard.this.magicNumber; // 每费回复量
            private final boolean freeToPlayOnce = YinChaCard.this.freeToPlayOnce; // 免费标记
            private final int energyOnUse = YinChaCard.this.energyOnUse; // 手动选择的能量值

            // 初始化Action配置（对齐官方）
            {
                this.duration = Settings.ACTION_DUR_XFAST;
                this.actionType = ActionType.SPECIAL;
            }

            // 核心逻辑：读取消耗能量X → 回复3X生命 → 消耗能量
            @Override
            public void update() {
                // 步骤1：确定实际消耗的能量值X（和旋风斩逻辑完全一致）
                int effect = EnergyPanel.totalCount;
                if (this.energyOnUse != -1) {
                    effect = this.energyOnUse;
                }

                // 兼容Chemical X遗物（可选，和旋风斩对齐）
                if (p.hasRelic("Chemical X")) {
                    effect += 2;
                    p.getRelic("Chemical X").flash();
                }

                // 步骤2：执行回复和能量消耗
                if (effect > 0) {
                    int totalHeal = this.healPerEnergy * effect; // 总回复=3×X（升级后4×X）

                    // 视觉/音效反馈（提升体验，可选）
                    addToBot(new SFXAction("HEAL_SOUND"));
                    // 执行生命回复（官方标准HealAction）
                    addToBot(new HealAction(
                            p,
                            p,
                            totalHeal
                    ));

                    // 消耗能量（非免费使用时）
                    if (!this.freeToPlayOnce) {
                        p.energy.use(effect);
                    }

                }

                if(upgraded){
                    this.addToBot(new GainEnergyAction(effect));
                }else{
                    this.addToBot(new GainEnergyAction(Math.max(effect - 1, 0)));
                }

                // 标记Action执行完成（必须）
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 自动添加+号
            upgradeMagicNumber(UPG_HEAL_PER_ENERGY);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription(); // 更新卡牌描述
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new YinChaCard();
    }
}