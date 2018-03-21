new Vue({
    el: '#app',
    name: 'appIndex',
    data: {
        url: '',
        resultUrl: ''
    },
    // 监听
    watch: {},
    // 计算属性
    computed: {},
    // 方法
    methods: {
        generate: function () {
            var _this = this;
            var url = document.getElementById("url").value;
            axios.post("/message/register?url=" + url)
                .then(function (result) {
                    var data = result.data;
                    var hostname = location.hostname;
                    var port = location.port;
                    if (port) {
                        _this.resultUrl = "http://" + hostname + ":" + port + "/message/gitlab?token=" + data.token;
                    } else {
                        _this.resultUrl = "http://" + hostname + "/message/gitlab?token=" + data.token;
                    }
                }).catch(function (reason) {
                _this.resultUrl = "出错了！！！";
            })
        }
    },
    created: function () {
    }
});
