$( document ).ready(function() {
    refreshExamList();
    setPositions();
    window.onresize = function(event) {
        setPositions();
    };
    new ResizeObserver(setPositions).observe(document.getElementById("centerDiv"));
});

function setPositions() {
    var mainDiv = $("#main");
    var examList = $("#examList");
    var navbar = $("#navbar");

    var centerDiv = $("#centerDiv");
    /*centerDiv.css('left', (mainDiv.width()-examList.width())/2-centerDiv.width()/2);
    centerDiv.css('top', (mainDiv.height()-navbar.height())/2-centerDiv.height()/2);*/

    centerDiv.css('left', (mainDiv.width()-examList.width())/2-centerDiv.outerWidth()/2);
    centerDiv.css('top', (mainDiv.height()-navbar.height())/2-centerDiv.outerHeight()/2);
}

function refreshExamList() {
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
                var html = '<a id="'+json.exams[i].id+'"href="javascript:selectExam('+json.exams[i].id+')">'+json.exams[i].name+'</a>';
                examList.innerHTML = examList.innerHTML+html;
            }
        },
        dataType: "text"
    });
}

function selectExam(id) {
    getExamInfo(id);
}

function getExamInfo(id) {
    console.log("here");
    var localStorage = window.localStorage;
    var token = localStorage.getItem("token");
    if (token == null || token == undefined) {
        console.error("Couldn't get token!");
        return;
    }

    $.ajax({
        type: "POST",
        url: "./exams/"+id+"/",
        data: "token="+token,
        success: function(result) {
            /*var examList = document.getElementById("examList");
            examList.innerHTML = "";

            var json = JSON.parse(result);
            for (i = 0; i < json.exams.length; i++) {
                var html = '<a id="'+json.exams[i].id+'" href="javascript:void(0)">'+json.exams[i].name+'</a>';
                examList.innerHTML = examList.innerHTML+html;
            }*/
            console.log(result);
        },
        dataType: "text"
    });
}