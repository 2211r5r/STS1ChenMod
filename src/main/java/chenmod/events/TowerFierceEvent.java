package chenmod.events;

import chenmod.ChenMod;
import chenmod.cards.TowerFierceCard;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static chenmod.ChenMod.makeID;

public class TowerFierceEvent extends AbstractImageEvent {
    // 事件唯一ID（通过modID拼接，确保全局唯一）
    public static final String ID = makeID(TowerFierceEvent.class.getSimpleName());
    // 加载本地化文本
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String NAME = eventStrings.NAME;

    private final AbstractCard previewCard;

    // 事件阶段控制变量（0：初始剧情，1：选择阶段，2：结果阶段）
    private int screenNum = 0;

    public TowerFierceEvent() {
        // 父类构造：事件名称、初始剧情文本、事件背景图片路径
        super(NAME, DESCRIPTIONS[0], "chenmod/images/events/TowerFierceEvent.png");

        this.previewCard = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();

        // 初始阶段添加第一个按钮（"继续"，用于进入选择阶段）
        this.imageEventText.setDialogOption("继续");

    }

    @Override
    public void onEnterRoom() {
        super.onEnterRoom();
        ChenMod.logger.info("进入了房间，播放自定义BGM");

        CardCrawlGame.music.playTempBGM(Sounds.towerFierceEventBGM);

        AbstractDungeon.eventList.removeIf(eventName -> eventName.equals(ID));

        ChenMod.logger.info("已经将TowerFierceEvent移除出事件List");

    }


    @Override
    protected void buttonEffect(int buttonPressed) {
        // 自定义标签，用于跳出多层switch
        Label_TowerFierceEvent: {
            switch (this.screenNum) {
                // 阶段0：初始剧情（点击"继续"进入选择阶段）
                case 0: {
                    // 更新事件剧情文本为选择阶段的提示
                    this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                    // 清空原有按钮，添加三个核心选项
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[0], new TowerFierceCard()); // [直面TA] 获得 杀戮之塔
                    this.imageEventText.setDialogOption(OPTIONS[1], this.previewCard); // [耸肩无视] 获得 耸肩无视
                    this.imageEventText.setDialogOption(OPTIONS[2]); // [什么也不做] 离开
                    // 切换到选择阶段
                    this.screenNum = 1;
                    break;
                }

                // 阶段1：选择阶段（处理三个选项的逻辑）
                case 1: {
                    switch (buttonPressed) {
                        // 选项0：直面TA → 获得杀戮之塔卡牌
                        case 0: {
                            // 记录事件日志（用于游戏统计/成就）
                            AbstractEvent.logMetricObtainCard("TowerFierceEvent", "[直面TA]", new TowerFierceCard());
                            // 创建卡牌实例
                            final AbstractCard c = new TowerFierceCard();
                            // 添加"获得卡牌"的视觉特效（卡牌从屏幕左侧飞入手牌）
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH * 0.3f, Settings.HEIGHT / 2.0f));
                            // 更新剧情文本为获得卡牌的反馈
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            // 清空所有按钮，添加"离开"按钮
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption("离开");
                            // 切换到结果阶段
                            this.screenNum = 2;
                            break Label_TowerFierceEvent;
                        }

                        // 选项1：耸肩无视 → 无奖励，仅剧情反馈
                        case 1: {
                            // 记录事件日志
                            AbstractEvent.logMetric("TowerFierceEvent", "[耸肩无视]");

                            this.screenNum = 1;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            this.imageEventText.clearRemainingOptions();

                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.previewCard, Settings.WIDTH * 0.3f, Settings.HEIGHT / 2.0f));

                            // 清空所有按钮，添加"离开"按钮
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption("离开");
                            // 切换到结果阶段
                            this.screenNum = 2;
                            break Label_TowerFierceEvent;
                        }

                        // 选项2：什么也不做 → 直接离开
                        case 2: {
                            // 记录事件日志
                            AbstractEvent.logMetric("TowerFierceEvent", "[什么也不做]");
                            // 更新剧情文本为离开的反馈
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            // 清空所有按钮，添加"离开"按钮
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption("离开");
                            // 切换到结果阶段
                            this.screenNum = 2;
                            break Label_TowerFierceEvent;
                        }

                        // 无效按钮点击（防御性处理）
                        default: {

                            CardCrawlGame.music.fadeOutTempBGM();
                            break Label_TowerFierceEvent;
                        }
                    }
                }

                // 阶段2：结果阶段（点击"离开"返回地图）
                case 2: {

                    ChenMod.logger.info("退出了房间，恢复原始BGM");

                    // 2. 仅当播放过自定义BGM时，才执行恢复逻辑
                    CardCrawlGame.music.fadeOutTempBGM();
                    this.openMap();
                    break;
                }

                // 异常阶段（防止卡死，直接返回地图）
                default: {

                    CardCrawlGame.music.fadeOutTempBGM();
                    this.openMap();
                    break;
                }
            }
        }
    }

}