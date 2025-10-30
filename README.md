# 飞机大战游戏 (Aircraft War)

一个使用Java Swing开发的经典飞机大战游戏，集成了多种设计模式和丰富的游戏机制。

> 现在可以直接使用 `git push` 命令推送代码！

## 项目简介

这是一个功能完整的2D飞机大战游戏，玩家操控英雄飞机与各种敌机战斗，通过获取道具、击败Boss来获取高分。游戏支持难度选择、音效管理、排行榜记录等功能。

## 核心功能

### 🎮 游戏机制
- **多种敌机类型**：普通敌机、精英敌机、超级精英敌机、Boss
- **三种难度模式**：Easy、Medium、Difficult（使用模板方法模式实现）
- **道具系统**：血量补给、炸弹补给、火力增强、超级子弹
- **射击策略**：直线射击、散射、环形射击（策略模式）
- **观察者模式**：炸弹道具清除屏幕敌人

### 🎵 音效系统
- 背景音乐（普通/Boss阶段切换）
- 各种音效：射击、爆炸、道具获取、游戏结束
- 音效开关控制

### 📊 分数与排行榜
- 实时分数计算
- 历史记录保存（txt文件）
- 排行榜对话框展示
- 成绩导出功能

## 设计模式应用

### 1. 单例模式 (Singleton)
- `HeroAircraft`：英雄飞机全局唯一实例
- `SoundManager`：音效管理器单例
- `ImageManager`：图片资源管理器

### 2. 工厂模式 (Factory)
- `EnemyFactory` 接口及多个实现：
  - `MobEnemyFactory`（普通敌机）
  - `EliteEnemyFactory`（精英敌机）
  - `SuperEliteEnemyFactory`（超级精英）
  - `BossFactory`（Boss）
- `SupplyFactory` 接口及多个实现：
  - `BloodSupplyFactory`（血量补给）
  - `BombSupplyFactory`（炸弹补给）
  - `BulletSupplyFactory`（火力补给）
  - `SuperBulletSupplyFactory`（超级子弹）

### 3. 策略模式 (Strategy)
射击策略接口 `strategy`：
- `Direct`：直线射击
- `Scatter`：散射
- `Circle`：环形射击

### 4. 模板方法模式 (Template Method)
- `ModeSelect` 抽象类定义游戏难度配置流程
- 子类 `Easy`、`Medium`、`Difficulte` 实现不同难度参数

### 5. 观察者模式 (Observer)
- `ObserverAircraft` 接口定义观察者
- `BombActive` 作为主题，管理观察者列表
- 炸弹道具触发时通知所有敌机

### 6. 数据访问对象模式 (DAO)
- `ScoreDao` 接口
- `ScoreDaolmpl` 实现类
- 封装分数数据访问逻辑

## 项目结构

```
src/
├── edu/hitsz/
│   ├── aircraft/          # 飞机类
│   │   ├── AbstractAircraft.java      # 飞机抽象基类
│   │   ├── HeroAircraft.java          # 英雄飞机（单例）
│   │   ├── MobEnemy.java              # 普通敌机
│   │   ├── EliteEnemy.java            # 精英敌机
│   │   ├── SuperEliteEnemy.java       # 超级精英敌机
│   │   ├── Boss.java                  # Boss
│   │   ├── *Factory.java              # 各种工厂类
│   │   ├── BombActive.java            # 炸弹主题
│   │   └── ObserverAircraft.java      # 观察者接口
│   ├── application/       # 应用层
│   │   ├── Main.java                  # 程序入口
│   │   ├── Game.java                  # 游戏主逻辑
│   │   ├── HeroController.java        # 英雄控制
│   │   └── ImageManager.java          # 图片管理
│   ├── basic/             # 基础类
│   │   └── AbstractFlyingObject.java  # 抽象飞行对象
│   ├── bullet/            # 子弹类
│   │   ├── BaseBullet.java            # 子弹基类
│   │   ├── HeroBullet.java            # 英雄子弹
│   │   └── EnemyBullet.java           # 敌机子弹
│   ├── ballistic/         # 弹道策略
│   │   ├── strategy.java              # 策略接口
│   │   ├── Direct.java                # 直线策略
│   │   ├── Scatter.java               # 散射策略
│   │   └── Circle.java                # 环形策略
│   ├── mode/              # 难度模式
│   │   ├── ModeSelect.java            # 模板方法抽象类
│   │   ├── Easy.java
│   │   ├── Medium.java
│   │   └── Difficulte.java
│   ├── supply/            # 道具系统
│   │   ├── BaseSupply.java            # 道具基类
│   │   ├── BloodSupply.java           # 血量补给
│   │   ├── BombSupply.java            # 炸弹补给
│   │   ├── BulletSupply.java          # 火力补给
│   │   ├── SuperBulletSupply.java     # 超级子弹
│   │   └── *Factory.java              # 各种工厂类
│   └── score/             # 分数系统
│       ├── Score.java                 # 分数实体
│       ├── ScoreDao.java              # DAO接口
│       ├── ScoreDaolmpl.java          # DAO实现
│       ├── ScoreBoardDialog.java      # 排行榜对话框
│       └── ExportRecords.java         # 导出功能
├── images/                # 图片资源
├── videos/                # 音效资源
├── music/                 # 音乐管理
│   ├── SoundManager.java  # 音效管理器（单例）
│   └── MusicThread.java   # 音乐线程
└── uml/                   # UML类图
    ├── Demo.puml         # PlantUML示例
    ├── Inheritence.puml  # 继承关系图
    └── test.puml
```

## 技术特点

- **纯Java实现**：使用Java Swing开发GUI
- **多线程音乐**：独立的音乐线程管理音效
- **线程池管理**：使用ExecutorService管理音效线程
- **文件持久化**：分数记录保存到txt文件
- **UML文档**：包含PlantUML类图
- **设计模式实践**：综合运用6种经典设计模式

## 运行要求

- Java 8+
- Apache Commons Lang3（用于线程工厂）

## 如何运行

1. 克隆项目到本地
2. 使用IDE（如IntelliJ IDEA）导入项目
3. 配置Maven依赖或直接导入jar包
4. 运行 `src/edu/hitsz/application/Main.java`

## 游戏操作

- **移动**：鼠标控制英雄飞机位置
- **射击**：自动射击
- **道具**：碰撞拾取道具
- **难度**：游戏开始前选择

## 项目特色

✨ **丰富的设计模式应用**：单例、工厂、策略、观察者、模板方法、DAO  
🎨 **完整的功能实现**：多种敌机、道具、音效、排行榜  
🔧 **良好的代码架构**：清晰的包结构、职责分离  
📈 **可扩展性强**：易于添加新的敌机、道具、射击策略

## 作者

xu-wen-yu

## 许可证

MIT License
