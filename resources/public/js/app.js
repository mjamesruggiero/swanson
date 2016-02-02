var swanson = {
    drawBasic: function() {
        var json = $.ajax({
            url: "/by-week",
            dataType: "json",
            async: false
            }).responseText;
        var parsed = swanson.formatByWeek(JSON.parse(json));

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

    formatByWeek: function(byWeek) {
        var header = ["Date", "Total"];
        var result = [header];
        for (var i = 0; i < byWeek.length; i++) {
            var formattedDate = swanson.extractDate(byWeek[i].week);
            result.push([formattedDate, -1 * byWeek[i].total]);
        }
        return result;
    },

    extractDate: function(elem) {
        function numberAt(start, length) {
            return Number(elem.slice(start, start + length));
        }
        return new Date(numberAt(0, 4), numberAt(5, 2) - 1, numberAt(8, 2));
    }
};

google.load('visualization', '1', {packages: ['corechart', 'bar']});
google.setOnLoadCallback(swanson.drawBasic);
