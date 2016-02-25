var swanson = {
    drawByWeekChart: function() {
        var json = $.ajax({ url: "/by-week",
                            dataType: "json",
                            async: false }).responseText;

        var parsed = swanson.formatWeeks(JSON.parse(json));

        var data = google.visualization.arrayToDataTable(parsed);

        var options = {
            title: 'Spend by week',
            chartArea: {width: '50%', height: '100%'},
            hAxis: {
                title: 'Spend',
                minValue: 0
            },
            vAxis: { title: 'Date' }
        };
        var chart = new google.visualization.BarChart(document.getElementById('chart-div'));
        chart.draw(data, options);
    },

    drawByMonthChart: function() {
        var json = $.ajax({ url: "/months",
                            dataType: "json",
                            async: false }).responseText;

        var parsed = swanson.formatMonths(JSON.parse(json));

        var data = google.visualization.arrayToDataTable(parsed);

        var options = {
            title: 'Last six months',
            chartArea: {width: '50%', height: '100%'},
            hAxis: {
                title: 'Spend',
                minValue: 0
            },
            vAxis: { title: 'Month' }
        };
        var chart = new google.visualization.BarChart(document.getElementById('six-months-chart-div'));
        chart.draw(data, options);
    },

    formatWeeks: function(byWeek) {
        var header = ["Date", "Total"];
        var result = [header];
        for (var i = 0; i < byWeek.length; i++) {
            var formattedDate = swanson.parseDate(byWeek[i].week);
            result.push([formattedDate, byWeek[i].total]);
        }
        return result;
    },

    formatMonths: function(byMonth) {
        var header = ["Date", "Total"];
        var result = [header];
        for (var i = 0; i < byMonth.length; i++) {
            var formattedDate = new Date(byMonth[i].year, byMonth[i].month - 1, 1);
            result.push([formattedDate, byMonth[i].amount]);
        }
        return result;
    },

    parseDate: function(elem) {
        function numberAt(start, length) {
            return Number(elem.slice(start, start + length));
        }
        return new Date(numberAt(0, 4), numberAt(5, 2) - 1, numberAt(8, 2));
    }
};

google.load('visualization', '1', {packages: ['corechart', 'bar']});
google.setOnLoadCallback(swanson.drawByWeekChart);
google.setOnLoadCallback(swanson.drawByMonthChart);
