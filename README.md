# HCountDownTimer
仿日历翻页倒计时控件
#### 思路
1、首先，需要保证一个倒计时线程控制所有FlipView的动画。

2、其次，考虑有两种方案：比较倒计时器每秒计算下当前需变化FlipView,然后手动实现翻转。
第二种方案是控制秒数的FlipView，把所有的View进行关联。然后每秒变化后只手动调用秒数的组件。
相对来说实现起来更复杂一点，需要控制所有View之间的关联性。本项目以前者实现为基础。

3、后期考虑整合FlipLayout,实现内置倒计时器。
> 时间问题：代码主要实现倒计时和FlipLayoutView,未做到最优封装
