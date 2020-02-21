const monthNames = ["January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

function formatDate(date) {
    return monthNames[date.getMonth()]+" "+date.getDate()+", "+date.getFullYear();
}

function formatTime(millis) {
    var hours = Math.floor(millis/3600000);
    var minutes = Math.floor((millis-hours*3600000)/60000);
    var seconds = Math.floor((millis-hours*3600000-minutes*60000)/1000);
    return hours+":"+minutes+":"+seconds;
}

$(document).ready(function() {
    updateUI();
    window.onresize = function(event) {
        updateUI();
    };

    try {
        new ResizeObserver(updateUI).observe(document.getElementById("centerDiv"));
    } catch (err) {
        console.error(err.message);
    }

    showExams();
});

function updateUI() {
    var mainDiv = $("#main");
    var sideNav = $("#sidenav");

    var centerDiv = $("#centerDiv");
    centerDiv.css('left', (mainDiv.width()-sideNav.width())/2-centerDiv.outerWidth()/2);
    centerDiv.css('top', (mainDiv.height())/2-centerDiv.outerHeight()/2);
}

function showExams() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Exams</h1><div style='max-height: 300px; overflow: auto;'><table id='examsTable' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Name</th><th>Groups</th><th>Questions</th><th>Start</th><th>End</th><th id='examsTHActive'>Active</th></thead><tbody id='examsTBody'></tbody></table></div>");
    $('#centerDiv').css('display','inline');

    $.ajax({
        type: "GET",
        url: "./exams",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            var exams = json.exams;

            var tbody = $("#examsTBody");

            for (var i = 0; i < exams.length; i++) {
                var id = exams[i].id;
                var name = exams[i].name;
                var groupsArray = exams[i].groups;
                var groups = "";
                for (var j = 0; j < groupsArray.length; j++) {
                    groups += groupsArray[j];
                    if (j < groupsArray.length - 1) groups += ", ";
                }
                var questions = exams[i].questions;
                var start = formatDate(new Date(exams[i].start));
                var end = formatDate(new Date(exams[i].end));
                var date = Date.now();
                var active = Number(exams[i].start) < date && Number(exams[i].end) > date;
                tbody.append("<tr"+(active?" style='background-color: #DFF3DF;'":"")+"><td>"+id+"</td><td>"+name+"</td><td>"+groups+"</td><td>"+questions+"</td><td data-value='"+exams[i].start+"'>"+start+"</td><td data-value='"+exams[i].end+"'>"+end+"</td><td data-value="+(active?1:0)+">"+(active?"Yes":"No")+"</td><td><button class='actionBtn' onclick='showEditExamForm("+id+")'>Edit</button></td></tr>");
            }

            appendButton("Create new exam","showEditExamForm()");
            Sortable.init();
            if (tbody.children().length > 0) $("#examsTHActive").click();
            $("#centerDiv").removeClass("skeleton");
        },
        error: function () {
            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
}

function showStartedExams() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Started Exams</h1><div style='max-height: 300px; overflow: auto;'> <table id='startedExamsTable' class='sortable-theme-light sortable-theme-dark' data-sortable> <thead> <th>#</th> <th>Name</th> <th>Student's ID</th> <th>Student's Name</th> <th>Elapsed Time</th> </thead> <tbody id='startedExamsTBody'></tbody> </table></div>");
    $('#centerDiv').css('display','inline');

    $.ajax({
        type: "GET",
        url: "./exams/started",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            var exams = json.exams;

            var tbody = $("#startedExamsTBody");

            for (var i = 0; i < exams.length; i++) {
                var id = exams[i].id;
                var name = exams[i].name;
                var elapsedTime = exams[i].elapsedtime;
                var studentID = exams[i].id;
                var studentName = exams[i].student.firstname+" "+exams[i].student.lastname;

                tbody.append("<tr><td>"+id+"</td><td>"+name+"</td><td>"+studentID+"</td><td>"+studentName+"</td><td data-value="+elapsedTime+">"+formatTime(elapsedTime)+"</td></tr>");
            }

            Sortable.init();
            $("#centerDiv").removeClass("skeleton");
        },
        error: function () {
            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
}

function showResults() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Results</h1><div style='max-height: 300px; overflow: auto;'><table id='resultsTable' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Student's ID</th><th>Student's Name</th><th>Exam</th><th>Date</th><th>Score</th></thead><tbody id='resultsTBody'></tbody></table></div>");
    $('#centerDiv').css('display','inline');

    $.ajax({
        type: "GET",
        url: "./exams/results",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            var results = json.results;

            var tbody = $("#resultsTBody");

            for (var i = 0; i < results.length; i++) {
                var id = results[i].id;
                var student = results[i].student;
                var date = formatDate(new Date(results[i].date));
                var exam = results[i].name;
                var percentInt = (Math.floor(parseFloat(results[i].correct) / parseFloat(results[i].questions) * 100));
                var score = percentInt + "% ("+results[i].correct+"/"+results[i].questions+")";
                tbody.append("<tr><td>"+id+"</td><td>"+student.id+"</td><td>"+student.name+"</td><td>"+exam+"</td><td data-value='"+results[i].date+"'>"+date+"</td><td data-value='"+percentInt+"'>"+score+"</td></tr>");
            }

            Sortable.init();
            $("#centerDiv").removeClass("skeleton");
        },
        error: function () {
            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
}

function showStudents() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Students</h1><div style='max-height: 300px; overflow: auto;'><table id='studentsTable' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Name</th><th>Login</th><th>Groups</th><th>Finished exams</th></thead><tbody id='studentsTBody'></tbody></table></div>");
    $('#centerDiv').css('display','inline');

    $.ajax({
        type: "GET",
        url: "./students",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            var students = json.students;

            var tbody = $("#studentsTBody");

            for (var i = 0; i < students.length; i++) {
                var id = students[i].id;
                var name = students[i].firstname+" "+students[i].lastname;
                var login = students[i].login;
                var groupsArray = students[i].groups;
                var groups = "";
                for (var j = 0; j < groupsArray.length; j++) {
                    groups += groupsArray[j];
                    if (j < groupsArray.length - 1) groups += ", ";
                }
                var results = students[i].results;
                tbody.append("<tr><td>"+id+"</td><td>"+name+"</td><td>"+login+"</td><td>"+groups+"</td><td>"+results+"</td></tr>");
            }

            appendButton("Create new student","showEditStudentForm()");
            Sortable.init();
            $("#centerDiv").removeClass("skeleton");
        },
        error: function () {
            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
}

function showGroups() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Groups</h1><div style='max-height: 300px; overflow: auto;'><table id='groupsTable' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Name</th><th>Students</th><th>Exams</th></thead><tbody id='groupsTBody'></tbody></table></div>");
    $('#centerDiv').css('display','inline');

    $.ajax({
        type: "GET",
        url: "./groups",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            var groups = json.groups;

            var tbody = $("#groupsTBody");

            for (var i = 0; i < groups.length; i++) {
                var id = groups[i].id;
                var name = groups[i].name;
                var students = groups[i].students;
                var exams = groups[i].exams;
                tbody.append("<tr><td>"+id+"</td><td>"+name+"</td><td>"+students+"</td><td>"+exams+"</td></tr>");
            }

            appendButton("Create new group","showEditGroupForm()");
            Sortable.init();
            $("#centerDiv").removeClass("skeleton");
        },
        error: function () {
            $("#centerDiv").removeClass("skeleton");
        },
        dataType: "text"
    });
}

function logOut() {
    window.localStorage.clear();
    window.location.href = window.location.href;
}

function showEditExamForm(id) {
    //TODO: Add remove question functionality
    $("#centerDiv").html('<h1 id="editExamHeader">Create new exam</h1> <div class="scrollingForm"> <input id="examID" type="hidden"> <input id="examName" type="text" class="field" placeholder="Name"> <br><input id="examDesc" type="text" class="field" placeholder="Description"> <br><input id="examQuestionCount" type="number" class="field" placeholder="Questions"> <br><select id="examGroups" style="width: 100%; font-size: 14px; margin: 3px 0px 3px 0px;" multiple="multiple"> <optgroup id="examGroupsOptGroup" label="Groups"> </optgroup> </select> <br><br><br><br><br><input id="examStart" class="left" type="datetime-local"> <input id="examEnd" class="right" type="datetime-local"> <br><h2 style="margin: 44px 0px 0px 0px; text-align: left;">Questions:</h2> <br><div class="question"> <p>Question 1</p><input name="id" type="hidden"> <input name="question" type="text" class="field" placeholder="Question"> <input name="answerA" type="text" class="field correct" placeholder="Answer A (Correct Answer)"> <input name="answerB" type="text" class="field" placeholder="Answer B"> <input name="answerC" type="text" class="field" placeholder="Answer C"> <input name="answerD" type="text" class="field" placeholder="Answer D"> </div><button id="addQuestionBtn" class="greenBtn" style="margin-top: 10px;" onclick="addQuestion()">Add question</button> </div><button class="greenBtn rightAlign" onclick="saveExam()">Save exam</button>');

    $('#examGroups').select2({
        placeholder: "Groups"
    });

    var groups = undefined;

    $.ajax({
            type: "GET",
            url: "./groups",
            dataType: "json",
            headers: {"Authorization":window.localStorage.getItem("token")},
            success: function (result) {
                var json = JSON.parse(result);
                groups = json.groups;
                console.log(groups);
            },
            error: function () {
                alert("Failed to load groups!");
            },
            dataType: "text"
    });

    var selectedGroups = undefined;

    if (id != undefined && id != null) {
        //TODO: Load exam data
        //TODO: Change #editExamHeader text to 'Edit [EXAM NAME]'
        $.ajax({
            type: "GET",
            url: "./exams/"+id,
            dataType: "json",
            headers: {"Authorization":window.localStorage.getItem("token")},
            success: function (result) {
                var json = JSON.parse(result);

                $("#examID").val(json.id);
                $("#examName").val(json.name);
                $("#examDesc").val(json.description);
                $("#examQuestionCount").val(json.questionCount);

                var startString = new Date(json.start).toISOString();
                var endString = new Date(json.end).toISOString();

                $("#examStart").val(startString.substring(0,startString.length-1));
                $("#examEnd").val(endString.substring(0,endString.length-1));

                console.log(json.questions);
                json.questions.forEach(function (question) {
                    addQuestionFromJSON(question);
                });

                selectedGroups = json.groups;
                console.log(selectedGroups);
                if (selectedGroups === undefined) selectedGroups = null;
            },
            error: function () {
                alert("Failed to load exam!");
                selectedGroups = null;
            },
            dataType: "text"
        });
    }

    var timeoutFunc = function() {
        if (groups == null || (selectedGroups !== null && selectedGroups == undefined)) {
            setTimeout(timeoutFunc, 200);
            return;
        }

        $("#examGroupsOptGroup").html("");
        if (groups != null && selectedGroups !== null) {
            console.log("here");
            console.log(groups);
            groups.forEach(function(group) {
                var selected = false;
                if (selectedGroups != null) {
                    selectedGroups.forEach(function (selectedGroup) {
                        if (group.id == selectedGroup.id) {
                            selected = true;
                        }
                    });
                }

                var newOption = new Option(group.name, group.id, selected, selected);
                $('#examGroupsOptGroup').append(newOption)/*.trigger('change')*/;
            });
            $('#examGroups').trigger('change');
        } else {
            console.log("here2");
            console.log(groups);
            groups.forEach(function(group) {
                var newOption = new Option(group.name, group.id, false, false);
                $('#examGroupsOptGroup').append(newOption)/*.trigger('change')*/;
            });
            $('#examGroups').trigger('change');
        }

        $("#centerDiv").css("display","inline");
    };
    setTimeout(timeoutFunc, 200);
}

function showEditStudentForm(id) {
    $("#centerDiv").html(' <h1 id="editGroupHeader">Create new student</h1> <input id="studentID" type="hidden"> <input id="studentFirstName" type="text" class="field" placeholder="First Name"> <input id="studentLastName" type="text" class="field" placeholder="Last Name"> <input id="studentUsername" type="text" class="field" placeholder="Username"> <input id="studentEmail" type="text" class="field" placeholder="E-mail"> <input id="studentPassword" type="password" class="field" placeholder="Password"> <br><select id="studentGroups" style="width: 100%; font-size: 14px; margin: 3px 0px 3px 0px;" multiple="multiple"> <optgroup id="studentGroupsOptGroup" label="Groups"> </optgroup> </select> <button class="greenBtn rightAlign" onclick="saveStudent()">Save student</button>');

    $('#studentGroups').select2({
        placeholder: "Groups"
    });

    $.ajax({
        type: "GET",
        url: "./groups",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            var json = JSON.parse(result);
            json.groups.forEach(function(group) {
            var newOption = new Option(group.name, group.id, false, false);
                $('#studentGroupsOptGroup').append(newOption)/*.trigger('change')*/;
            });
            $('#studentGroups').trigger('change');
        },
        error: function () {
            alert("Failed to load groups!");
        },
        dataType: "text"
    });

    if (id != undefined && id != null) {
        //TODO: Load group data
        //TODO: Change #editGroupHeader text to 'Edit [GROUP NAME]'
    }

    $("#centerDiv").css("display","inline");
}

function showEditGroupForm(id) {
    $("#centerDiv").html('<h1 id="editGroupHeader">Create new group</h1> <input id="groupID" type="hidden"> <input id="groupName" type="text" class="field" placeholder="Name"> <button class="greenBtn rightAlign" onclick="saveGroup()">Save group</button>');

    if (id != undefined && id != null) {
        //TODO: Load group data
        //TODO: Change #editGroupHeader text to 'Edit [GROUP NAME]'
    }

    $("#centerDiv").css("display","inline");
}

function addQuestion() {
    var questions = $(".question");

    var question = $('<div class="question"> <p>Question '+(questions.length+1)+'</p><input name="question" type="text" class="field" placeholder="Question"> <input name="answerA" type="text" class="field correct" placeholder="Answer A (Correct Answer)"> <input name="answerB" type="text" class="field" placeholder="Answer B"> <input name="answerC" type="text" class="field" placeholder="Answer C"> <input name="answerD" type="text" class="field" placeholder="Answer D"> </div>');
    question.insertAfter(questions.get(questions.length - 1));

    var parent = questions.eq(0).parent();
    parent.scrollTop(parent.prop("scrollHeight"));
}

function addQuestionFromJSON(question) {
    var questions = $(".question");

    var question = $('<div class="question"> <p>Question '+(questions.length+1)+'</p><input name="question" type="text" class="field" placeholder="Question" value="'+question.name+'"> <input name="answerA" type="text" class="field correct" placeholder="Answer A (Correct Answer)" value="'+question.answers[0].name+'"> <input name="answerB" type="text" class="field" placeholder="Answer B" value="'+question.answers[1].name+'"> <input name="answerC" type="text" class="field" placeholder="Answer C" value="'+question.answers[2].name+'"> <input name="answerD" type="text" class="field" placeholder="Answer D" value="'+question.answers[3].name+'"> </div>');
    question.insertAfter(questions.get(questions.length - 1));

    var parent = questions.eq(0).parent();
    parent.scrollTop(parent.prop("scrollHeight"));
}

function saveExam() {
    var id = $("#examID").val();
    var name = $("#examName").val();
    var description = $("#examDesc").val();
    var questionCount = $("#examQuestionCount").val();
    var groups = $("#examGroups").val();
    var start = new Date($("#examStart").val()).getTime()/1000;
    var end = new Date($("#examEnd").val()).getTime()/1000;

    var questions = $(".question");
    var questionArray = [];

    questions.each(function (i, element) {
        var questionObj = new Object();
        var question = $(element);
        questionObj.id = question.find("[name=id]").eq(0).val();
        questionObj.name = question.find("[name='question']").eq(0).val();
        questionObj.answerA = question.find("[name='answerA']").eq(0).val();
        questionObj.answerB = question.find("[name='answerB']").eq(0).val();
        questionObj.answerC = question.find("[name='answerC']").eq(0).val();
        questionObj.answerD = question.find("[name='answerD']").eq(0).val();
        questionArray[i] = questionObj;
    });
    
    var object = new Object();
    object.id = id;
    object.name = name;
    object.description = description;
    object.questionCount = questionCount;
    object.groups = groups;
    object.start = start;
    object.end = end;
    object.questions = questionArray;

    var json = JSON.stringify(object);

    $.ajax({
        type: "POST",
        data: json,
        url: "./exams/new",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            showExams();
            alert("Exam successfully created!");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 400) alert("At least one required field is empty!");
            else alert("An error ocurred while saving the exam!");
        },
        dataType: "text"
    });
}

function saveStudent() {
    var id = $("#studentID").val();
    var firstname = $("#studentFirstName").val();
    var lastname = $("#studentLastName").val();
    var username = $("#studentUsername").val();
    var email = $("#studentEmail").val();
    var password = $("#studentPassword").val();
    var groups = $("#studentGroups").val();

    var object = new Object();
    object.id = id;
    object.firstname = firstname;
    object.lastname = lastname;
    object.username = username;
    object.email = email;
    object.password = password;
    object.groups = groups;

    var json = JSON.stringify(object);

    $.ajax({
        type: "POST",
        data: json,
        url: "./students/new",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            showStudents();
            alert("Student successfully created!");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 400) alert("At least one required field is empty!");
            else alert("An error ocurred while saving the student!");
        },
        dataType: "text"
    });
}

function saveGroup() {
    var id = $("#groupID").val();
    var name = $("#groupName").val();

    var object = new Object();
    object.id = id;
    object.name = name;
    var json = JSON.stringify(object);

    $.ajax({
        type: "POST",
        data: json,
        url: "./groups/new",
        dataType: "json",
        headers: {"Authorization":window.localStorage.getItem("token")},
        success: function (result) {
            showGroups();
            alert("Group successfully created!");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 400) alert("At least one required field is empty!");
            else alert("An error ocurred while saving the group!");
        },
        dataType: "text"
    });
}

function appendButton(text, onclick) {
    $("#centerDiv").append("<button class='greenBtn rightAlign' onclick='"+onclick+"'>"+text+"</button>");
}