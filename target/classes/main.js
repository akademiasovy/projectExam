function refreshTestList() {
    var localStorage = window.localStorage;
    var token = localStorage.getItem("token");
    if (token == null || token == undefined) {
        console.error("Couldn't get token!");
        return;
    }

    $.ajax({
        type: "POST",
        url: "./exams",
        data: "token="+token,
        success: function(result) {
            var examList = document.getElementById("examList");
            examList.innerHTML = "";

            var json = JSON.parse(result);
            for (i = 0; i < json.exams.length; i++) {
                var html = '<a id="'+json.exams[i].id+'" href="javascript:void(0)">'+json.exams[i].name+'</a>';
                examList.innerHTML = examList.innerHTML+html;
            }
        },
        dataType: "text"
    });
}