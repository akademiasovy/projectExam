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

function showResults() {
    $('#centerDiv').addClass("skeleton");
    $('#centerDiv').html("<h1>Results</h1><div style='max-height: 300px; overflow: auto;'><table id='resultsTable' class='sortable-theme-light sortable-theme-dark' data-sortable><thead><th>#</th><th>Student</th><th>Exam</th><th>Date</th><th>Score</th></thead><tbody id='resultsTBody'></tbody></table></div>");
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
                var dateObj = new Date(results[i].date);
                var date = dateObj.getDate() + ". " + (dateObj.getMonth() + 1) + ". " + dateObj.getFullYear();
                var exam = results[i].name;
                var score = (Math.floor(parseFloat(results[i].correct) / parseFloat(results[i].questions) * 100)) + "% ("+results[i].correct+"/"+results[i].questions+")";
                tbody.append("<tr><td>" + id + "</td><td>"+student+"</td><td>" + date + "</td><td>" + exam + "</td><td>" + score + "</td></tr>");
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