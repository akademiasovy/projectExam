var interval;

$.ajax({
        type: "POST",
        url: "./exams/assigned",
        data: "token="+window.localStorage.getItem("token"),
        success: function(result) {
            var json = JSON.parse(result);
            $("#examList").html("<p id='examQuestionNum'>Question 1/"+json.questions+"</p><span>"+json.name+"</span><span id='examTime' name='"+json.start+"'>00:00:00</span>");
            interval = setInterval(updateTimer, 1000);

            $("#resultsBtn").css("display","none");
            $("#settingsBtn").css("display","none");

            $('#centerDiv').html('<p id="examQuestion">Q</p> <button id="answerA" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>A</span></span><span class="right"><span id="answerAval">A</span></span></button> <button id="answerB" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>B</span></span><span class="right"><span id="answerBval">B</span></span></button> <button id="answerC" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>C</span></span><span class="right"><span id="answerCval">C</span></span></button> <button id="answerD" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>D</span></span><span class="right"><span id="answerDval">D</span></span></button> <button id="nextQuestionBtn" onclick="sendAnswer()" class="greenBtn">Next question</button>');
            getCurrentQuestion();
            $('#centerDiv').css('display','inline');
            $('#mainPlaceholder').css('display','none');
        },
        error: function() {
            $(document).ready(function() {
                refreshExamList();
            });
        },
        dataType: "text"
});

$(document).ready(function() {
    updateUI();
    window.onresize = function(event) {
        updateUI();
    };

    window.addEventListener("click", function(e) {
        if (!document.getElementById("optionsPanel").contains(e.target) && !document.getElementById("optionsPanelBtn").contains(e.target)) {
            $("#optionsPanel").removeClass("open");
        }
    });

    new ResizeObserver(updateUI).observe(document.getElementById("centerDiv"));
    new ResizeObserver(updateUI).observe(document.getElementById("optionsPanel"));
});

function toggleOptionsPanel() {
    var optionsPanel = $("#optionsPanel");
    if (optionsPanel.css("visibility") == "hidden") {
        optionsPanel.addClass("open");
    } else {
        optionsPanel.removeClass("open");
    }
}

function updateUI() {
    var mainDiv = $("#main");
    var examList = $("#examList");
    var navbar = $("#navbar");

    var centerDiv = $("#centerDiv");
    centerDiv.css('left', (mainDiv.width()-examList.width())/2-centerDiv.outerWidth()/2);
    centerDiv.css('top', (mainDiv.height()-navbar.height())/2-centerDiv.outerHeight()/2);
    //TODO: Change, maybe?
    //centerDiv.css('max-width',centerDiv.height()*1.75);

    var optionsPanel = $("#optionsPanel");
    optionsPanel.css('left', mainDiv.width()-examList.width()-optionsPanel.outerWidth());
}

function selectAnswer(answer) {
    if (answer == null || answer == undefined) return;

    $("#answerA").removeClass("selected");
    $("#answerB").removeClass("selected");
    $("#answerC").removeClass("selected");
    $("#answerD").removeClass("selected");

    $(answer).addClass("selected");
}

function deselectAnswer() {
    $("#answerA").removeClass("selected");
    $("#answerB").removeClass("selected");
    $("#answerC").removeClass("selected");
    $("#answerD").removeClass("selected");
}

function updateTimer() {
    var timer = $("#examTime");
    if (timer == null || timer == undefined) return;
    //TODO: Remove results and setting from optionsPanel
    var duration = Date.now()-Number(timer.attr('name'));
    var hours = Math.floor(duration/3600000);
    var minutes = Math.floor((duration-hours*3600000)/60000);
    var seconds = Math.floor((duration-hours*3600000-minutes*60000)/1000);

    hours = hours.toString();
    minutes = minutes.toString();
    seconds = seconds.toString();
    if (hours.length == 1) hours = "0"+hours;
    if (minutes.length == 1) minutes = "0"+minutes;
    if (seconds.length == 1) seconds = "0"+seconds;

    timer.text(hours+":"+minutes+":"+seconds);
}

function sendAnswer() {
    var answer = undefined;

    if ($("#answerA").hasClass("selected")) answer = 0;
    else if ($("#answerB").hasClass("selected")) answer = 1;
    else if ($("#answerC").hasClass("selected")) answer = 2;
    else if ($("#answerD").hasClass("selected")) answer = 3;

    if (answer != undefined) {
        document.getElementById("nextQuestionBtn").disabled = true;
            $.ajax({
                type: "POST",
                url: "./exams/assigned/answer",
                data: "token="+window.localStorage.getItem("token")+"&answer="+answer,
                success: function(result, textStatus, jqXHR) {
                    if (jqXHR.status == 200) {
                        deselectAnswer();
                        getCurrentQuestion();
                        document.getElementById("nextQuestionBtn").disabled = false;
                    } else if (jqXHR.status == 201) {
                        location.reload();
                    }
                },
                error: function() {
                    document.getElementById("takeExamBtn").disabled = false;
                },
                dataType: "text"
            });
    }
}

function startExam() {
    document.getElementById("takeExamBtn").disabled = true;

    $.ajax({
        type: "POST",
        url: "./exams/"+$("#examInfoID").val()+"/start",
        data: "token="+window.localStorage.getItem("token"),
        success: function(result) {
            var json = JSON.parse(result);
            $("#examList").html("<p id='examQuestionNum'>Question 1/"+json.questions+"</p><span>"+json.name+"</span><span id='examTime' name='"+json.start+"'>00:00:00</span>");
            interval = setInterval(updateTimer, 1000);

            $("#resultsBtn").css("display","none");
            $("#settingsBtn").css("display","none");

            $('#centerDiv').addClass("skeleton");
            $('#centerDiv').html('<p id="examQuestion">Q</p> <button id="answerA" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>A</span></span><span class="right"><span id="answerAval">A</span></span></button> <button id="answerB" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>B</span></span><span class="right"><span id="answerBval">B</span></span></button> <button id="answerC" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>C</span></span><span class="right"><span id="answerCval">C</span></span></button> <button id="answerD" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>D</span></span><span class="right"><span id="answerDval">D</span></span></button> <button id="nextQuestionBtn" onclick="sendAnswer()" class="greenBtn">Next question</button>');
            getCurrentQuestion();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 409) {
                clearInterval(interval);
                location.reload();
            } else {
                document.getElementById("takeExamBtn").disabled = false;
            }
        },
        dataType: "text"
    });
}

function getCurrentQuestion() {
    $("#centerDiv").addClass("skeleton");

    $.ajax({
        type: "POST",
        url: "./exams/assigned/question",
        data: "token="+window.localStorage.getItem("token"),
        success: function(result) {
            var json = JSON.parse(result);
            $("#examQuestion").text(json.question);
            $("#answerAval").text(json.optionA);
            $("#answerBval").text(json.optionB);
            $("#answerCval").text(json.optionC);
            $("#answerDval").text(json.optionD);

            var examQuestionNum = $("#examQuestionNum");
            examQuestionNum.text("Question "+(json.number+1)+examQuestionNum.text().substring(examQuestionNum.text().lastIndexOf("/")));

            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
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
            examList.innerHTML = "<p>Exams</p>";

            var json = JSON.parse(result);
            if (json.exams.length > 0) {
                json.exams.sort(function(a, b) {
                    nameA = a.name.toLowerCase();
                    nameB = b.name.toLowerCase();
                    return (nameA < nameB) ? -1 : (nameA > nameB) ? 1 : 0;
                })
                for (i = 0; i < json.exams.length; i++) {
                    var html = '<a id="'+json.exams[i].id+'"href="javascript:selectExam('+json.exams[i].id+')">'+json.exams[i].name+'</a>';
                    examList.innerHTML = examList.innerHTML+html;
                }
            } else {
                examList.innerHTML = "<p>Exams</p><span>No exams found</span>"
            }
        },
        dataType: "text"
    });
}

var selectQuery;
function selectExam(id) {
    var localStorage = window.localStorage;
    var token = localStorage.getItem("token");
    if (token == null || token == undefined) {
        console.error("Couldn't get token!");
        return;
    }
    if (selectQuery != undefined && selectQuery != null) {
        selectQuery.abort();
    }

    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html(' <input id="examInfoID" type="hidden" value=""> <h1 id="examInfoName">skeleton</h1> <p id="examInfoDesc">skeleton</p> <p id="examInfoQuestions">skeleton</p> <p id="examInfoDeadline">skeleton</p> <button id="takeExamBtn" onclick="startExam()" class="greenBtn" style="width: 80%; margin-top: 10px;">Take exam</button>');

    $('#examInfoID').val("");
    $('#examInfoName').html("skeleton");
    $('#examInfoDesc').html("skeleton");
    $('#examInfoQuestions').html("skeleton");
    $('#examInfoDeadline').html("skeleton");

    $('#centerDiv').css('display','inline');
    $('#mainPlaceholder').css('display','none');

    selectQuery = $.ajax({
        type: "POST",
        url: "./exams/"+id,
        data: "token="+token,
        success: function(result) {
            console.log(result);
            var exam = JSON.parse(result);

            $('#examInfoID').val(exam.id);
            $('#examInfoName').text(exam.name);
            $('#examInfoDesc').html("<b>Description:</b> "+exam.description);
            $('#examInfoQuestions').html("<b>Questions:</b> "+exam.questions);
            $('#examInfoDeadline').html("<b>Deadline:</b> "+new Date(exam.end).toDateString());
            $('#centerDiv').removeClass("skeleton");
        },
        error: function() {
            $('#centerDiv').css('display','none');
            $('#mainPlaceholder').css('display','table-cell');
        },
        dataType: "text"
    });
}

function logOut() {
    window.localStorage.clear();
    location.reload();
}