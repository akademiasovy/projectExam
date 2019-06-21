$(document).ready(function() {
    refreshTestList();
    setName();
});

function setName() {
    var localStorage = window.localStorage;
    var firstname = localStorage.getItem("firstname");
    var lastname = localStorage.getItem("lastname");

    if (firstname != undefined && firstname != null && lastname != undefined && lastname != null) {
        var welcomeP = document.getElementById("welcomeP");
        welcomeP.innerHTML = "Welcome "+firstname+" "+lastname+"!";
    }
}

function selectExam(id) {
        var localStorage = window.localStorage;
        var token = localStorage.getItem("token");
        if (token == null || token == undefined) {
            console.error("Couldn't get token!");
            return;
        }

        $.ajax({
            type: "POST",
            url: "./exams/"+id,
            data: "token="+token,
            success: function(result) {
                var json = JSON.parse(result);

                var id = json.id;
                var name = json.name;
                var description = json.description;
                var questions = json.questions;
                var start = json.start;
                var end = json.end;

                console.log(id+" | "+name+" | "+description+" | "+questions+" | "+start+" | "+end);
            },
            dataType: "text"
        });
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