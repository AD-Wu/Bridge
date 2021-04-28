let EventHelper = {
    addHandler: function (element, type, handler) {
        if (element.addEventListener) {
            element.addEventListener(type, handler, false);
        } else if (element.attachEvent) {
            element.attachEvent("on" + type, handler);
        } else {
            element["on" + type] = handler;
        }
    },
    removeHandler: function (element, type, handler) {
        if (element.removeEventListener) {
            element.removeEventListener(element, type, handler);
        } else if (element.detachEvent) {
            element.detachEvent("on" + type, handler);
        } else {
            element["on" + type] = null;
        }
    },
    getEvent: function (event) {
        return event ? event : window.event;
    },
    getTarget: function (event) {
        return event.target || event.srcElement;
    },
    getButton: function (event) {
        if (document.implementation.hasFeature("MouseEvents", "2.0")) {
            return event.button;
        } else {
            switch (event.button) {
                // 没有按下按钮
                case 0:
                // 按下了主鼠标按钮
                case 1:
                // 同时按下了主、次鼠标按钮
                case 3:
                // 同时按下了主、中间鼠标按钮
                case 5:
                // 同时按下了三个鼠标按钮
                case 7:
                    return 0;// 主
                // 按下了次鼠标按钮
                case 2:
                // 同时按下了次、中间鼠标按钮
                case 6:
                    return 2// 次
                // 按下了中间鼠标按钮
                case 4:
                    return 1;// 中间
            }
        }
    },
    getCharCode: function (event) {
        if (typeof event.charCode == "number") {
            return event.charCode;
        } else {
            return event.keyCode;
        }
    },
    getWheelDelta: function (event) {
        if (event.wheelDelta) {
            return (client.engine.opera && client.engine.opera < 9.5 ? -event.wheelDelta : event.wheelDelta);
        } else {
            // Firefox
            return -event.detail * 40;
        }
    },
    // 阻止事件传播
    preventDefault: function (event) {
        if (event.preventDefault) {
            event.preventDefault();
        } else {
            event.returnValue = false;
        }
    },
    stopPropagation: function (event) {
        if (event.stopPropagation) {
            event.stopPropagation();
        } else {
            event.cancelBubble = true;
        }
    },
    // 获取剪贴板数据
    getClipboardText: function (event) {
        //  IE 浏览器的clipboard属window对象所有
        let clipboard = (event.clipboardData || window.clipboardData);
        return clipboard.getData("text");
    },
    // 设置剪贴板数据，成功返回true，否则返回false
    setClipboardText: function (event, value) {
        if (event.clipboardData) {
            // 非IE浏览器接受MIME类型数据
            return event.clipboardData.setData("text/plain", value);
        } else if (window.clipboardData) {
            return window.clipboardData.setData("text", value);
        }
    }

}