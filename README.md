# FMFileTool
file app manager project
RecyclerView长按快速滚动时候，为什么焦点位置会乱，原来是，在item的FocusChange时候，如果去设置界面其它控件，如TextView的显示文本时，就会焦点错乱


RecyclerView为什么焦点位置没有居于屏幕中央位置，原因是，剧中的实现方法支持recycle25+版本，26，27这方式就不行了
