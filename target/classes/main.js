var interval;

if (window.localStorage.getItem("theme") == "dark") {
    $("#theme").attr("href","./resources/main-dark.css");
}

$(window).on("load", function(){
    $("body").css("opacity",1);
});

$.ajax({
        type: "GET",
        url: "./exams/assigned",
        headers: {"Authorization":window.localStorage.getItem("token")},
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

    if (window.localStorage.getItem("theme") == "dark") {
        $("#optionsIcon").attr("src","./resources/options-dark.png");
    }
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

function changeTheme() {
    if (window.localStorage.getItem("theme") == undefined || window.localStorage.getItem("theme") == "light") {
        window.localStorage.setItem("theme", "dark");
        $("#theme").attr("href","./resources/main-dark.css");
        $("#optionsIcon").attr("src","./resources/options-dark.png");

        var path = document.querySelector('#resultBar > svg > path:first-child');
        if (path != undefined) path.setAttribute("stroke", "#2F2F2F");
    } else if (window.localStorage.getItem("theme") == "dark") {
        window.localStorage.setItem("theme", "light");
        $("#theme").attr("href","./resources/main-light.css");
        $("#optionsIcon").attr("src","./resources/options-light.png");

        var path = document.querySelector('#resultBar > svg > path:first-child');
        if (path != undefined) path.setAttribute("stroke", "#EEEEEE");
    }
}

function showSettings() {
    $("#centerDiv").html('<h1>Settings</h1> <p style="margin-bottom: 41px;"></p> <p style="text-align: center; font-size: 24px; font-weight: bold;">Change password</p> <input type="password" id="oldPassword" name="password" placeholder="Old password"> <p style="margin: 0px 0px 15px 0px;"></p> <input type="password" id="newPassword" name="password" oninput="checkPassword()" placeholder="New password"> <p style="margin: 0px 0px 5px 0px;"></p> <input type="password" id="confirmPassword" name="password" oninput="checkPasswordMatch()" placeholder="Confirm password"> <p id="error" style="display: none;"></p>  <p style="margin: 0px 0px 35px 0px;"></p> <button onclick="changePassword()" class="greenBtn" style="width: 200px;">Change password</button> <ul class="requirements"><li id="reqHeader" style="font-weight: bold; margin-bottom: 5px;">Requirements:</li><li id="reqLength">At least 8 characters</li><li id="reqLower">At least 1 lowercase letter</li><li id="reqUpper">At least 1 uppercase letter</li><li id="reqDigit">At least 1 digit</li><li id="reqMatch">Passwords must match</li></ul>');
    $('#centerDiv').css('display','inline');
    $('#mainPlaceholder').css('display','none');
}

function showResults() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Results</h1><div style='max-height: 400px; overflow: auto;'><table id='resultsTable' style='width: 100%;' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Exam</th><th>Score</th></thead><tbody id='resultsTBody'></tbody></table></div>");
    $('#centerDiv').css('display','inline');
    $('#mainPlaceholder').css('display','none');

    $.ajax({
            type: "GET",
            url: "./exams/results",
            headers: {"Authorization":window.localStorage.getItem("token")},
            success: function(result) {
                var json = JSON.parse(result);
                var results = json.results;

                var tbody = $("#resultsTBody");

                for (var i = 0; i < results.length; i++) {
                    var number = results[i].number;
                    var exam = results[i].name;
                    var score = (Math.floor(parseFloat(results[i].correct)/parseFloat(results[i].questions)*100))+"%";
                    tbody.append("<tr><td>"+number+"</td><td>"+exam+"</td><td>"+score+"</td></tr>");
                }

                Sortable.init();
                $("#centerDiv").removeClass("skeleton");
            },
            error: function() {
                  $('#centerDiv').css('display','none');
                  $('#mainPlaceholder').css('display','table-cell');
            },
            dataType: "text"
    });
}

function changePassword() {
    var oldPassword = $("#oldPassword");
    var newPassword = $("#newPassword");
    var confirmPassword = $("#confirmPassword");

    var error = $("#error");
    var errorMsg;

    if (newPassword.val().length < 8) {
        errorMsg = "The password must be at least 8 characters long!";
    } else if (!newPassword.val().match(/[a-z]/g)) {
        errorMsg = "The password must contain at least 1 lowercase letter!"
    } else if (!newPassword.val().match(/[A-Z]/g)) {
        errorMsg = "The password must contain at least 1 uppercase letter!"
    } else if (!newPassword.val().match(/[0-9]/g)) {
        errorMsg = "The password must contain at least 1 digit!"
    } else if (newPassword.val() != confirmPassword.val()) {
        errorMsg = "The passwords don't match!";
    }

    if (errorMsg != undefined) {
        $("#error").css("color","");
        $("#error").text(errorMsg);
        $("#error").hide().fadeIn(150);
        newPassword.css("border-color","#CF5454");
        confirmPassword.css("border-color","#CF5454");
        return;
    }

    $.ajax({
        type: "POST",
        url: "./password/change",
        headers: {"Authorization":window.localStorage.getItem("token")},
        data: JSON.stringify({oldPassword: oldPassword.val(), newPassword: newPassword.val()}),
        success: function(result) {
            console.log(result);
            var json = JSON.parse(result);
            window.localStorage.setItem("token",json.token);
            oldPassword.val("");
            newPassword.val("");
            confirmPassword.val("");
            oldPassword.css("border-color","");
            newPassword.css("border-color","");
            confirmPassword.css("border-color","");
            $("#error").css("color","#4DDD78");
            $("#error").text("Password changed successfully!");
            $("#error").hide().fadeIn(150);
            $("#error").delay(2000).fadeOut(150);
            checkPassword();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 401) {
                $("#error").css("color","");
                $("#error").text("The password is incorrect!");
                $("#error").hide().fadeIn(150);
                oldPassword.css("border-color","#CF5454");
                newPassword.css("border-color","");
                confirmPassword.css("border-color","");
            } else {
                $("#error").css("color","");
                $("#error").text("An error occurred!");
                $("#error").hide().fadeIn(150);
                oldPassword.css("border-color","");
                newPassword.css("border-color","");
                confirmPassword.css("border-color","");
            }
        },
        dataType: "text"
    });
}

function checkPassword() {
    var password = $("#newPassword").val();
    if (password.length < 8) {
        $("#reqLength").removeClass("fulfilled");
    } else {
        $("#reqLength").addClass("fulfilled");
    }

    if (!password.match(/[a-z]/g)) {
        $("#reqLower").removeClass("fulfilled");
    } else {
        $("#reqLower").addClass("fulfilled");
    }

    if (!password.match(/[A-Z]/g)) {
        $("#reqUpper").removeClass("fulfilled");
    } else {
        $("#reqUpper").addClass("fulfilled");
    }

    if (!password.match(/[0-9]/g)) {
        $("#reqDigit").removeClass("fulfilled");
    } else {
        $("#reqDigit").addClass("fulfilled");
    }

    checkPasswordMatch();
}

function checkPasswordMatch() {
    var newPassword = $("#newPassword").val();
    var confirmPassword = $("#confirmPassword").val();

    if (newPassword != confirmPassword || newPassword == "" || confirmPassword == "") {
        $("#reqMatch").removeClass("fulfilled");
    } else {
        $("#reqMatch").addClass("fulfilled");
    }
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
                headers: {"Authorization":window.localStorage.getItem("token")},
                data: JSON.stringify({answer: answer}),
                success: function(result, textStatus, jqXHR) {
                    if (jqXHR.status == 200) {
                        deselectAnswer();
                        getCurrentQuestion();
                        document.getElementById("nextQuestionBtn").disabled = false;
                    } else if (jqXHR.status == 201) {
                        clearInterval(interval);
                        $("#centerDiv").addClass("skeleton");
                        $("#centerDiv").html('<h1 id="resultName">skeleton</h1> <div id="resultBar" class="progressBar"></div> <div id="resultInfo"> <span id="resultCorrect" style="font-size: 18px;"></span> <br> <span id="resultIncorrect" style="font-size: 18px;"></span> <br> <span id="resultQuestions" style="font-size: 18px;"></span> </div> <button class="greenBtn" onclick="window.location.href = window.location.href;"">Close</button>');

                        $.ajax({
                            type: "GET",
                            url: jqXHR.responseText,
                            headers: {"Authorization":window.localStorage.getItem("token")},
                            success: function(result, textStatus, jqXHR) {
                                if (jqXHR.status != 200) window.location.href = window.location.href;
                                var json = JSON.parse(jqXHR.responseText);

                                $("#resultName").text(json.name);

                                var bar = new ProgressBar.Circle(document.getElementById("resultBar"), {
                                  color: '#AAA',
                                  strokeWidth: 6,
                                  trailWidth: 6,
                                  trailColor: window.localStorage.getItem("theme") == "dark"?"#2F2F2F":"#EEEEEE",
                                  easing: 'easeInOut',
                                  duration: 1000,
                                  text: {
                                    autoStyleContainer: false
                                  },
                                  from: { color: '#FF3333'},
                                  to: { color: '#4DDD78'},

                                  step: function(state, circle) {
                                    circle.path.setAttribute('stroke', state.color);

                                    var value = Math.round(circle.value()*100);
                                    circle.setText(value+"%");
                                  }
                                });
                                bar.text.style.fontFamily = 'sans-serif';
                                bar.text.style.fontSize = '24px';
                                bar.animate(Math.floor(parseFloat(json.correct)/parseFloat(json.questions)*100)/100);

                                //$("#resultTime").text("Completed in "+$("#examTime").text()+"!");
                                $("#resultCorrect").html("<b>Correct answers: </b><b style='color: #4DDD78;'>"+parseInt(json.correct)+"</b>");
                                $("#resultIncorrect").html("<b>Incorrect answers: </b><b style='color: #FF3333;'>"+(parseInt(json.questions)-parseInt(json.correct))+"</b>");
                                $("#resultQuestions").html("<b>Total questions: "+parseInt(json.questions)+"</b>");

                                $("#examQuestionNum").text("Summary");
                                $("#examQuestionNum").hide().fadeIn(150);

                                $("#centerDiv").removeClass("skeleton");
                            },
                            error: function() {
                                window.location.href = window.location.href;
                            },
                            dataType: "text"
                        });
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
        url: "./exams/start",
        headers: {"Authorization":window.localStorage.getItem("token")},
        data: JSON.stringify({id: $("#examInfoID").val()}),
        success: function(result) {
            var json = JSON.parse(result);
            $("#examList").html("<p id='examQuestionNum'>Question 1/"+json.questions+"</p><span>"+json.name+"</span><span id='examTime' name='"+json.start+"'>00:00:00</span>");
            $("#examQuestionNum").hide().fadeIn(150);

            interval = setInterval(updateTimer, 1000);

            $("#resultsBtn").css("display","none");
            $("#settingsBtn").css("display","none");

            $('#centerDiv').addClass("skeleton");
            $('#centerDiv').html('<p id="examQuestion"></p> <button id="answerA" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>A</span></span><span class="right"><span id="answerAval">A</span></span></button> <button id="answerB" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>B</span></span><span class="right"><span id="answerBval">B</span></span></button> <button id="answerC" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>C</span></span><span class="right"><span id="answerCval">C</span></span></button> <button id="answerD" onclick="selectAnswer(this)" class="examAnswer"><span class="left"><span>D</span></span><span class="right"><span id="answerDval">D</span></span></button> <button id="nextQuestionBtn" onclick="sendAnswer()" class="greenBtn">Next question</button>');
            getCurrentQuestion();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 409) {
                //clearInterval(interval);
                window.location.href = window.location.href;
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
        type: "GET",
        url: "./exams/assigned/question",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function(result) {
            var json = JSON.parse(result);
            $("#examQuestion").hide();
            $("#examQuestion").text(json.question);
            $("#examQuestion").fadeIn(150);

            $("#answerAval").text(json.optionA);
            $("#answerBval").text(json.optionB);
            $("#answerCval").text(json.optionC);
            $("#answerDval").text(json.optionD);

            var examQuestionNum = $("#examQuestionNum");
            examQuestionNum.text("Question "+(json.number+1)+examQuestionNum.text().substring(examQuestionNum.text().lastIndexOf("/")));
           if (json.number > 0) examQuestionNum.hide().fadeIn(150);

            var questions = parseInt($(examQuestionNum).text().substring(examQuestionNum.text().lastIndexOf("/")+1));
            if (parseInt(json.number)+1==questions) $("#nextQuestionBtn").text("Finish exam");

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
        type: "GET",
        url: "./exams",
        headers: {"Authorization":window.localStorage.getItem("token")},
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
        type: "GET",
        url: "./exams/"+id,
        headers: {"Authorization":window.localStorage.getItem("token")},
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
    window.location.href = window.location.href;
    //location.reload(true);
}