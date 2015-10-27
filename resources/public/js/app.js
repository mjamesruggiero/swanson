var swanson = {
    drawBasic: function() {
        //var stubData =
                //[['City', '2010 Population',],
                //['New York City, NY', 8175000],
                //['Los Angeles, CA', 3792000],
                //['Chicago, IL', 2695000],
                //['Houston, TX', 2099000],
                //['Philadelphia, PA', 1526000]];

        var dynamicData = swanson.formatByWeek(swanson.getData());
        var data = google.visualization.arrayToDataTable(dynamicData);

        var options = {
            title: 'Spend by week',
            chartArea: {width: '50%'},
            hAxis: {
                title: 'Spend',
                minValue: 0
            },
            vAxis: { title: 'Date' }
        };

        var chart = new google.visualization.BarChart(document.getElementById('chart_div'));

        chart.draw(data, options);
    },

    formatByWeek: function(byWeek) {
        var header = ["Date", "Total"];
        var result = [header];
        for (var i = 0; i < byWeek.length; i++) {
            result.push([byWeek[i].week, -1 * byWeek[i].total]);
        }
        return result;
    },

    getData: function() {
        var json = $.ajax({
            url: "/by-week",
            dataType: "json",
            async: false
            }).responseText;
        return JSON.parse(json);
    }

};

google.load('visualization', '1', {packages: ['corechart', 'bar']});
google.setOnLoadCallback(swanson.drawBasic);
