const monthNames = ["January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

function formatDate(date) {
    return monthNames[date.getMonth()]+" "+date.getDate()+", "+date.getFullYear();
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
                tbody.append("<tr"+(active?" style='background-color: #DFF3DF;'":"")+"><td>"+id+"</td><td>"+name+"</td><td>"+groups+"</td><td>"+questions+"</td><td data-value='"+exams[i].start+"'>"+start+"</td><td data-value='"+exams[i].end+"'>"+end+"</td><td data-value="+(active?1:0)+">"+(active?"Yes":"No")+"</td></tr>");
            }

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