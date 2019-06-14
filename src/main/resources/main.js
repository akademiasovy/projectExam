$(document).ready(function() {
    refreshTestList();
    setName();
});

function selectExam(id) {
}

function setName() {
    var localStorage = window.localStorage;
    var firstname = localStorage.getItem("firstname");
    var lastname = localStorage.getItem("lastname");

    if (firstname != undefined && firstname != null && lastname != undefined && lastname != null) {
        var welcomeP = document.getElementById("welcomeP");
        welcomeP.innerHTML = "Welcome "+firstname+" "+lastname+"!";
    }

}

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
                var html = '<a id="'+json.exams[i].id+'" onclick=selectExam('+json.exams[i].id+') href="javascript:void(0)">'+json.exams[i].name+'</a>';
                examList.innerHTML = examList.innerHTML+html;
            }
        },
        dataType: "text"
    });
}