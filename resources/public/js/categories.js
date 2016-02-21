var categories = {
    drawBasic: function() {
        var json = $.ajax({
            url: "/categories-ytd",
            dataType: "json",
            async: false
            }).responseText;
        var parsed = categories.formatRow(JSON.parse(json));
        console.log(parsed);

        var data = google.visualization.arrayToDataTable(parsed);

        var options = {
            title: 'Categories YTD',
            chartArea: {width: '50%', height: '100%'},
            hAxis: {
                title: 'Spend',
                minValue: 0
            },
            vAxis: { title: 'Category' }
        };
        var chart = new google.visualization.BarChart(document.getElementById('chart-div'));
        chart.draw(data, options);
    },

    formatRow: function(categories) {
        var header = ["Category", "Total"];
        var result = [header];
        for (var i = 0; i < categories.length; i++) {
            result.push([categories[i].category, categories[i].cost]);
        }
        return result;
    }
};

google.load('visualization', '1', {packages: ['corechart', 'bar']});
google.setOnLoadCallback(categories.drawBasic);
