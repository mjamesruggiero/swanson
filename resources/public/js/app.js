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
        swanson.drawTable(parsed);
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
    },

    drawTable: function(data) {
        var table = $('<table class="table table-bordered"></table>');
        for(i = 0; i < data.length; i++){
            var dateVal = this.formatDate(data[i][0]);
            var numVal = this.formatNumber(data[i][1]);
            var row = $('<tr><td>' + dateVal + '</td><td>' + numVal + '</td></tr>');
            table.append(row);
        }
        $('#table-div').append(table);
    },

    formatDate: function(str) {
        if ('object' === typeof(str)) {
            var parts = [ str.getMonth() + 1, str.getDate(), str.getFullYear() ];
            return parts.join("/");
        } else {
            return str;
        }
    },

    formatNumber(num) {
        if ('number' === typeof(num)) {
            return num.toFixed(2);
        } else {
            return num;
        }
    }
};

google.load('visualization', '1', {packages: ['corechart', 'bar']});
google.setOnLoadCallback(swanson.drawBasic);
